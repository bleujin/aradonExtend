package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.IMethodExecutor;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;
import net.ion.radon.impl.let.webdav.WebdavStatus;
import net.ion.radon.impl.let.webdav.exceptions.WebdavException;
import net.ion.radon.impl.let.webdav.fromcatalina.RequestUtil;
import net.ion.radon.impl.let.webdav.fromcatalina.URLEncoder;
import net.ion.radon.impl.let.webdav.fromcatalina.XMLWriter;

import org.apache.commons.vfs2.FileSystemException;
import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.xml.sax.SAXException;

public abstract class AbstractMethod implements IMethodExecutor {

	private final VFileStore store;
	private final Method method ;
	private VFile vfile;
	private String path;
	
	private static URLEncoder URL_ENCODER; // Array containing the safe characters set.
	private static final int INFINITY = 3; // Default depth is infite.
	
	protected static final SimpleDateFormat CREATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Simple date format for the creation date ISO 8601 representation (partial).
	protected static final SimpleDateFormat LAST_MODIFIED_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US); // Simple date format for the last modified date. (RFC 822 updated by RFC 1123)
	protected static int BUF_SIZE = 65536; // size of the io-buffer
	protected static final int DEFAULT_TIMEOUT = 3600; // Default lock timeout value.
	protected static final int MAX_TIMEOUT = 604800; // Maximum lock timeout.

	private static DocumentBuilder docBuilder = createDocumentBuilder();
	static {
		CREATION_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
		LAST_MODIFIED_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));

		URL_ENCODER = new URLEncoder();
		URL_ENCODER.addSafeCharacter('-');
		URL_ENCODER.addSafeCharacter('_');
		URL_ENCODER.addSafeCharacter('.');
		URL_ENCODER.addSafeCharacter('*');
		URL_ENCODER.addSafeCharacter('/');
	}

	protected AbstractMethod(VFileStore store, Method method) {
		this.store = store;
		this.method = method ;
	}

	public DocumentBuilder getDocBuilder() {
		return docBuilder;
	}

	public void handle(ITransaction transaction, InnerRequest request, InnerResponse response) {
		try {
			initVFile(transaction, request);
			
			response.setEntity(myHandle(transaction, request, response));
		} catch (ResourceException e) {
			sendReport(MapUtil.create(path, e.getStatus()));
			response.setStatus(e.getStatus()) ;
		} catch (WebdavException e) {
			sendReport(MapUtil.create(path, e.getStatus()));
			response.setStatus(e.getStatus()) ;
		} catch (IOException e) {
			sendReport(MapUtil.create(path, Status.SERVER_ERROR_INTERNAL));
			response.setStatus(Status.SERVER_ERROR_INTERNAL) ;
			e.printStackTrace() ;
		} catch (SAXException e) {
			sendReport(MapUtil.create(path, Status.SERVER_ERROR_INTERNAL));
			response.setStatus(Status.SERVER_ERROR_INTERNAL) ;
			e.printStackTrace() ;
		} catch (Throwable e) {
			e.printStackTrace() ;
			throw new ResourceException(e);
		}
	}

	protected abstract Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws SAXException, IOException;

	protected String parseDestinationHeader(InnerRequest request, InnerResponse response) throws IOException {

		String destPath = request.getHeader("Destination");

		// Remove url encoding from destination
		destPath = RequestUtil.URLDecode(destPath, "UTF8");

		int protocolIndex = destPath.indexOf("://");
		if (protocolIndex >= 0) {
			// if the Destination URL contains the protocol, we can safely trim everything upto the first "/" character after "://"
			int firstSeparator = destPath.indexOf("/", protocolIndex + 4);
			if (firstSeparator < 0) {
				destPath = "/";
			} else {
				destPath = destPath.substring(firstSeparator);
			}
		} else {
			// String hostName = request.getServerName();
			String hostName = request.getHostRef().getPath();
			if ((hostName != null) && (destPath.startsWith(hostName))) {
				destPath = destPath.substring(hostName.length());
			}

			int portIndex = destPath.indexOf(":");
			if (portIndex >= 0) {
				destPath = destPath.substring(portIndex);
			}

			if (destPath.startsWith(":")) {
				int firstSeparator = destPath.indexOf("/");
				if (firstSeparator < 0) {
					destPath = "/";
				} else {
					destPath = destPath.substring(firstSeparator);
				}
			}
		}

		// Normalize destination path (remove '.' and' ..')
		destPath = normalize(destPath);
		destPath = StringUtil.substringAfter(destPath, request.getResourceRef().getBaseRef().getPath());

		return destPath;
	}

	private String normalize(String path) {

		if (path == null)
			return null;

		// Create a place for the normalized path
		String normalized = path;

		if (normalized.equals("/."))
			return "/";

		// Normalize the slashes and add leading slash if necessary
		if (normalized.indexOf('\\') >= 0)
			normalized = normalized.replace('\\', '/');
		if (!normalized.startsWith("/"))
			normalized = "/" + normalized;

		// Resolve occurrences of "//" in the normalized path
		while (true) {
			int index = normalized.indexOf("//");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) + normalized.substring(index + 1);
		}

		// Resolve occurrences of "/./" in the normalized path
		while (true) {
			int index = normalized.indexOf("/./");
			if (index < 0)
				break;
			normalized = normalized.substring(0, index) + normalized.substring(index + 2);
		}

		// Resolve occurrences of "/../" in the normalized path
		while (true) {
			int index = normalized.indexOf("/../");
			if (index < 0)
				break;
			if (index == 0)
				return (null); // Trying to go outside our context
			int index2 = normalized.lastIndexOf('/', index - 1);
			normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
		}

		// Return the normalized path that we have completed
		return (normalized);

	}
	

	private VFile initVFile(ITransaction transaction, InnerRequest request) throws FileSystemException {
		this.path = getRelativePath(request);
		this.vfile = store.resolveFile(transaction, request.getAttribute("scheme"), path);
		
		if (Method.PUT.equals(method) || Method.MKCOL.equals(method) || Method.LOCK.equals(method) || Method.UNLOCK.equals(method)){
			; // ignore
		} else {
			if (!this.vfile.exists())
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, path);
		}
		return vfile ;
	}
	
	protected int getDepth(InnerRequest req) {
		final String depthStr = req.getHeader("Depth");
		int depth = StringUtil.isBlank(depthStr) ? INFINITY : (StringUtil.isNumeric(depthStr) ? Integer.parseInt(depthStr) : INFINITY);
		return depth;
	}


	protected VFile resolveFile(ITransaction transaction, String scheme, String relPath) throws FileSystemException {
		return store.resolveFile(transaction, scheme, relPath);
	}

	protected VFile getVFile(){
		return vfile ;
	}
	
	private String getPath(){
		return vfile.getName().getPath() ;
	}
	
	protected VFileStore getStore(){
		return store ;
	} 

	protected String getRelativePath(Request request) {
		return request.getResourceRef().getRemainingPart(true, false);
	}
	private String getParentPath(String path) {
		int slash = path.lastIndexOf('/');
		if (slash != -1) {
			return path.substring(0, slash);
		}
		return null;
	}

	private String getCleanPath(String path) {

		if (path.endsWith("/") && path.length() > 1)
			path = path.substring(0, path.length() - 1);
		return path;
	}

	private static DocumentBuilder createDocumentBuilder() {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			return documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw WebdavException.create(Status.SERVER_ERROR_INTERNAL, e);
		}
	}

	private Representation sendReport(Map<String, Status> errorList) {

		// String relativePath = getRelativePath(req);

		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("DAV:", "D");

		XMLWriter generatedXML = new XMLWriter(namespaces);
		generatedXML.writeXMLHeader();

		generatedXML.writeElement("DAV::multistatus", XMLWriter.OPENING);

		for (Entry<String, Status> entry : errorList.entrySet()) {

			generatedXML.writeElement("DAV::response", XMLWriter.OPENING);

			generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
			String toAppend = null;
			if (path.endsWith(entry.getKey())) {
				toAppend = path;

			} else if (path.contains(entry.getKey())) {
				int endIndex = path.indexOf(entry.getKey()) + entry.getKey().length();
				toAppend = path.substring(0, endIndex);
			}
			if (!toAppend.startsWith("/") && !toAppend.startsWith("http:"))
				toAppend = "/" + toAppend;
			generatedXML.writeText(entry.getKey());
			generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
			generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
			generatedXML.writeText("HTTP/1.1 " + entry.getValue().getCode() + " " + WebdavStatus.getStatusText(entry.getValue().getCode()));
			generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);

			generatedXML.writeElement("DAV::response", XMLWriter.CLOSING);

		}

		generatedXML.writeElement("DAV::multistatus", XMLWriter.CLOSING);

		return new StringRepresentation(generatedXML.toString(), MediaType.APPLICATION_XML, Language.ALL, CharacterSet.UTF_8);
	}
}
