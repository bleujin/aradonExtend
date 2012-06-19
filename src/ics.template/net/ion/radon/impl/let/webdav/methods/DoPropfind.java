package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;
import net.ion.radon.impl.let.webdav.WebdavStatus;
import net.ion.radon.impl.let.webdav.back.WebDavUtil;
import net.ion.radon.impl.let.webdav.exceptions.WebdavException;
import net.ion.radon.impl.let.webdav.fromcatalina.XMLHelper;
import net.ion.radon.impl.let.webdav.fromcatalina.XMLWriter;

import org.apache.commons.vfs2.FileSystemException;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DoPropfind extends AbstractMethod {

	private static enum PropertyFindType {
		FIND_BY_PROPERTY, FIND_ALL_PROP, FIND_PROPERTY_NAMES;
	}

	private DoPropfind(VFileStore store) {
		super(store, Method.PROPFIND);
	}

	public static DoPropfind create(VFileStore store) {
		return new DoPropfind(store);
	}

	public Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws  IOException, SAXException {
		int depth = getDepth(request);

		List<String> properties = null;
		PropertyFindType propertyFindType = PropertyFindType.FIND_ALL_PROP;
		Node propNode = null;

		if (! getVFile().exists()){
			throw WebdavException.create(Status.CLIENT_ERROR_NOT_FOUND) ;
		}

		// Windows 7 does a propfind with content length 0
		if (request.getEntity() != null && request.getEntity().getSize() > 0) {
			DocumentBuilder documentBuilder = getDocBuilder();
			Document document = documentBuilder.parse(new InputSource(request.getEntity().getStream()));
			Element rootElement = document.getDocumentElement();

			propNode = XMLHelper.findSubElement(rootElement, "prop");
			propertyFindType = getPropertyFindType(propNode, rootElement);
		}

		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("DAV:", "D");

		if (propertyFindType == PropertyFindType.FIND_BY_PROPERTY) {
			properties = XMLHelper.getPropertiesFromXML(propNode);
		}

		StringWriter writer = new StringWriter();

		// Create multistatus object
		XMLWriter generatedXML = new XMLWriter(writer, namespaces);
		generatedXML.writeXMLHeader();
		generatedXML.writeElement("DAV::multistatus", XMLWriter.OPENING);
		if (depth == 0) {
			parseProperties(transaction, generatedXML, getVFile(), propertyFindType, properties);
		} else {
			recursiveParseProperties(transaction, getVFile(), generatedXML, propertyFindType, properties, depth);
		}
		generatedXML.writeElement("DAV::multistatus", XMLWriter.CLOSING);
		generatedXML.sendData();

		response.setEntity(new StringRepresentation(writer.getBuffer(), MediaType.TEXT_XML, Language.ALL, CharacterSet.UTF_8));
		response.setStatus(new Status(207));
		return response.getEntity();
	}

	private PropertyFindType getPropertyFindType(Node propNode, Element rootElement) {
		if (propNode != null) {
			return PropertyFindType.FIND_BY_PROPERTY;
		} else if (XMLHelper.findSubElement(rootElement, "propname") != null) {
			return PropertyFindType.FIND_PROPERTY_NAMES;
		} else if (XMLHelper.findSubElement(rootElement, "allprop") != null) {
			return PropertyFindType.FIND_ALL_PROP;
		}
		return PropertyFindType.FIND_ALL_PROP;
	}

	private void recursiveParseProperties(ITransaction transaction, VFile vfile, XMLWriter generatedXML, PropertyFindType propertyFindType, List<String> properties, int depth) throws FileSystemException {

		parseProperties(transaction, generatedXML, vfile, propertyFindType, properties);

		if (depth > 0) {
			List<VFile> files = getStore().getChildren(transaction, vfile);
			for (VFile file : files) {
				recursiveParseProperties(transaction, file, generatedXML, propertyFindType, properties, depth - 1);
			}
		}
	}

	private void parseProperties(ITransaction transaction, XMLWriter generatedXML, VFile vfile, PropertyFindType propertyFindType, List<String> propertiesVector) throws FileSystemException {

		boolean isFolder = vfile.isDir();
		String creationdate = CREATION_DATE_FORMAT.format(new Date(vfile.getContent().getLastModifiedTime()));
		String lastModified = LAST_MODIFIED_DATE_FORMAT.format(new Date(vfile.getContent().getLastModifiedTime()));

		generatedXML.writeElement("DAV::response", XMLWriter.OPENING);
		String status = new String("HTTP/1.1 " + WebdavStatus.SC_OK + " " + WebdavStatus.getStatusText(WebdavStatus.SC_OK));

		generatedXML.writeElement("DAV::href", XMLWriter.OPENING);

		String baseName = WebDavUtil.escape(vfile.getName().getBaseName());
		if ("".equals(baseName))
			baseName = "/";
		String hrefPath = vfile.getName().getPath() + (vfile.isDir() ? "/" : "");
		if ("//".equals(hrefPath)) {
			hrefPath = "/";
		}

		generatedXML.writeText(hrefPath);
		generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);

		switch (propertyFindType) {
		case FIND_ALL_PROP:
			generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
			generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

			generatedXML.writeProperty("DAV::creationdate", creationdate);
			generatedXML.writeElement("DAV::displayname", XMLWriter.OPENING);
			generatedXML.writeData(baseName);
			generatedXML.writeElement("DAV::displayname", XMLWriter.CLOSING);
			if (!isFolder) {
				generatedXML.writeProperty("DAV::getlastmodified", lastModified);
				generatedXML.writeProperty("DAV::getcontentlength", String.valueOf(vfile.getContent().getSize()));
				generatedXML.writeProperty("DAV::getcontenttype", vfile.getContent().getContentType());
				generatedXML.writeProperty("DAV::getetag", String.valueOf(vfile.getETag()));
				generatedXML.writeElement("DAV::resourcetype", XMLWriter.NO_CONTENT);
			} else {
				generatedXML.writeElement("DAV::resourcetype", XMLWriter.OPENING);
				generatedXML.writeElement("DAV::collection", XMLWriter.NO_CONTENT);
				generatedXML.writeElement("DAV::resourcetype", XMLWriter.CLOSING);
			}

			// writeSupportedLockElements(transaction, generatedXML, path);
			// writeLockDiscoveryElements(transaction, generatedXML, path);

			generatedXML.writeProperty("DAV::source", "");
			generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
			generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
			generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

			break;

		case FIND_PROPERTY_NAMES:

			generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
			generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

			generatedXML.writeElement("DAV::creationdate", XMLWriter.NO_CONTENT);
			generatedXML.writeElement("DAV::displayname", XMLWriter.NO_CONTENT);
			if (!isFolder) {
				generatedXML.writeElement("DAV::getcontentlanguage", XMLWriter.NO_CONTENT);
				generatedXML.writeElement("DAV::getcontentlength", XMLWriter.NO_CONTENT);
				generatedXML.writeElement("DAV::getcontenttype", XMLWriter.NO_CONTENT);
				generatedXML.writeElement("DAV::getetag", XMLWriter.NO_CONTENT);
				generatedXML.writeElement("DAV::getlastmodified", XMLWriter.NO_CONTENT);
			}
			generatedXML.writeElement("DAV::resourcetype", XMLWriter.NO_CONTENT);
			generatedXML.writeElement("DAV::supportedlock", XMLWriter.NO_CONTENT);
			generatedXML.writeElement("DAV::source", XMLWriter.NO_CONTENT);

			generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
			generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
			generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

			break;

		case FIND_BY_PROPERTY:

			List<String> propertiesNotFound = new ArrayList<String>();
			generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
			generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

			Iterator<String> properties = propertiesVector.iterator();

			while (properties.hasNext()) {

				String property = properties.next();

				if (property.equals("DAV::creationdate")) {
					generatedXML.writeProperty("DAV::creationdate", creationdate);
				} else if (property.equals("DAV::displayname")) {
					generatedXML.writeElement("DAV::displayname", XMLWriter.OPENING);
					generatedXML.writeData(baseName);
					generatedXML.writeElement("DAV::displayname", XMLWriter.CLOSING);
				} else if (property.equals("DAV::getcontentlanguage")) {
					if (isFolder) {
						propertiesNotFound.add(property);
					} else {
						generatedXML.writeElement("DAV::getcontentlanguage", XMLWriter.NO_CONTENT);
					}
				} else if (property.equals("DAV::getcontentlength")) {
					if (isFolder) {
						propertiesNotFound.add(property);
					} else {
						generatedXML.writeProperty("DAV::getcontentlength", String.valueOf(vfile.getContent().getSize()));
					}
				} else if (property.equals("DAV::getcontenttype")) {
					if (isFolder) {
						propertiesNotFound.add(property);
					} else {
						generatedXML.writeProperty("DAV::getcontenttype", vfile.getContent().getContentType());
					}
				} else if (property.equals("DAV::getetag")) {
					// if (isFolder || so.isNullResource()) {
					if (isFolder || (!vfile.exists())) {
						propertiesNotFound.add(property);
					} else {
						generatedXML.writeProperty("DAV::getetag", String.valueOf(vfile.getETag()));
					}
				} else if (property.equals("DAV::getlastmodified")) {
					if (isFolder) {
						propertiesNotFound.add(property);
					} else {
						generatedXML.writeProperty("DAV::getlastmodified", lastModified);
					}
				} else if (property.equals("DAV::resourcetype")) {
					if (isFolder) {
						generatedXML.writeElement("DAV::resourcetype", XMLWriter.OPENING);
						generatedXML.writeElement("DAV::collection", XMLWriter.NO_CONTENT);
						generatedXML.writeElement("DAV::resourcetype", XMLWriter.CLOSING);
					} else {
						generatedXML.writeElement("DAV::resourcetype", XMLWriter.NO_CONTENT);
					}
				} else if (property.equals("DAV::source")) {
					generatedXML.writeProperty("DAV::source", "");
				} else {
					propertiesNotFound.add(property);
				}

			}

			generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
			generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
			generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

			Iterator<String> propertiesNotFoundList = propertiesNotFound.iterator();

			if (propertiesNotFoundList.hasNext()) {

				status = new String("HTTP/1.1 " + WebdavStatus.SC_NOT_FOUND + " " + WebdavStatus.getStatusText(WebdavStatus.SC_NOT_FOUND));

				generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);
				generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);

				while (propertiesNotFoundList.hasNext()) {
					generatedXML.writeElement((String) propertiesNotFoundList.next(), XMLWriter.NO_CONTENT);
				}

				generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);
				generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
				generatedXML.writeText(status);
				generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);
				generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);

			}

			break;

		}

		generatedXML.writeElement("DAV::response", XMLWriter.CLOSING);

		vfile.close();
	}

}
