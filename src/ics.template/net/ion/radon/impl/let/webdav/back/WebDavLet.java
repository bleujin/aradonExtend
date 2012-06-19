package net.ion.radon.impl.let.webdav.back;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ion.framework.util.IOUtil;
import net.ion.framework.util.StringUtil;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.AbstractLet;
import net.ion.radon.impl.let.vfs.VFSEntry;

import org.apache.commons.vfs2.FileSystemException;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.data.Tag;
import org.restlet.engine.header.Header;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.util.Series;
import org.xml.sax.SAXException;

public class WebDavLet extends AbstractLet {

	private static DocumentBuilder builder = null;
	static {
		createBuilder();
	}

	private static void createBuilder() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			builder = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Representation myMethodHandle(Method method, Representation entity) throws IOException, SAXException {

		VFile vfile = getVirtualFile();
		Representation result = EMPTY_REPRESENTATION;
		if (method == null) {
			setStatus(Status.CLIENT_ERROR_BAD_REQUEST, "No method specified");
		} else if (Method.PROPFIND.equals(method)) {
			//result = PropFind.create(this, vfile, builder).handle();
			setStatus(new Status(207));
		} else if (Method.COPY.equals(method)) {
			// throw new UnsupportedOperationException();
		} else if (Method.MOVE.equals(method)) {
			// throw new UnsupportedOperationException();
		} else if (Method.PROPPATCH.equals(method)) {
			// throw new UnsupportedOperationException();
		} else if (Method.LOCK.equals(method)) {
			setStatus(Status.SUCCESS_OK);
			// throw new UnsupportedOperationException();
		} else if (Method.MKCOL.equals(method)) {
			// throw new UnsupportedOperationException();
		} else if (Method.UNLOCK.equals(method)) {
			// throw new UnsupportedOperationException();
		}
		// SEARCH, SUBSCRIBE, UNSUBSCRIBE, POLL, NOTIFY

		// Representation result = response.getEntity() ;
		//		
		// if (result == null) return result ;
		if (vfile.getContent().isOpen())
			vfile.close();
		return result;
		//		
		// return new StringRepresentation(text, MediaType.APPLICATION_XML) ;
	}

	private VFile getVirtualFile() throws FileSystemException {
		VFSEntry entry = getContext().getAttributeObject("system.vfs.entry", VFSEntry.class);
		FileSystemEntry fe = entry.getFileSystemEntry();
		return fe.resolveFile(getPath());
	}

	private String getPath() {
		String scheme = getInnerRequest().getAttribute("scheme");
		return scheme + "://" + getInnerRequest().getRemainPath();
	}

	void setResponseHeader(final String header, final String value) {
		Series<Header> headers = getInnerResponse().getHeaders();
		headers.add(header, value);
	}

	int getDepth() {
		final String depth = getInnerRequest().getHeader("Depth");
		return StringUtil.isBlank(depth) ? 1 : (StringUtil.isNumeric(depth) ? Integer.parseInt(depth) : 32);
	}

	boolean isNoRoot() {
		final String depth = StringUtil.defaultIfEmpty(getInnerRequest().getHeader("Depth"), "1").toLowerCase();
		return depth.contains("noroot");
	}

	@Override
	protected Representation myDelete() throws FileSystemException {
		VFile vfile = getVirtualFile();
		boolean result = vfile.delete();
		getResponse().setStatus(Status.SUCCESS_ACCEPTED);
		return AbstractLet.EMPTY_REPRESENTATION;
	}

	@Override
	protected Representation myGet() throws FileSystemException {
		VFile vfile = getVirtualFile();
		Representation result = new InputRepresentation(vfile.getInputStream(), MediaType.valueOf(vfile.getContent().getContentType()));
		result.setSize(vfile.getLengthAsInt());
		result.setModificationDate(new Date(vfile.getContent().getLastModifiedTime()));
		result.setTag(new Tag(String.valueOf(vfile.getETag())));
		return result;
	}

	@Override
	protected Representation myPost(Representation entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Representation myPut(Representation rentity) throws IOException {
		// boolean overwrite = ! "F".equals(getHeader("Overwrite", true)) ; // default : true ;
		VFile vfile = getVirtualFile();
		try {
			if (rentity == null || rentity.getSize() <= 0) {
				// vfile.getParent().createFolder();
			} else {
				writeEntity(getRequestEntity(), vfile);
			}
		} finally {
			vfile.close();
		}
		setStatus(Status.SUCCESS_OK);
		return AbstractLet.EMPTY_REPRESENTATION;
	}

	private void writeEntity(Representation entity, VFile vfile) throws IOException {
		final OutputStream output = vfile.getOutputStream();
		final InputStream input = entity.getStream();
		IOUtil.copyNClose(input, output);
	}

	@Override
	protected Representation myOptions() {
		getInnerResponse().getAllowedMethods().add(Method.HEAD);
		getInnerResponse().getAllowedMethods().add(Method.GET);
		getInnerResponse().getAllowedMethods().add(Method.PUT);
		getInnerResponse().getAllowedMethods().add(Method.POST);
		getInnerResponse().getAllowedMethods().add(Method.PROPFIND);
		getInnerResponse().getAllowedMethods().add(Method.OPTIONS);
		getInnerResponse().getAllowedMethods().add(Method.DELETE);

		getInnerResponse().getAllowedMethods().add(Method.LOCK);
		getInnerResponse().getAllowedMethods().add(Method.UNLOCK);

		// response.getAllowedMethods().add(Method.PROPPATCH);
		// response.getAllowedMethods().add(Method.MKCOL);
		// response.getAllowedMethods().add(Method.COPY);
		// response.getAllowedMethods().add(Method.MOVE);

		getInnerResponse().getHeaders().set("MS-Author-Via", "DAV");
		getInnerResponse().getHeaders().set("DAV", "1");
		return EMPTY_REPRESENTATION;
	}

}
