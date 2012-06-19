package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;

import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;
import net.ion.radon.impl.let.webdav.WebdavStatus;
import net.ion.radon.impl.let.webdav.back.WebDavUtil;
import net.ion.radon.impl.let.webdav.exceptions.WebdavException;
import net.ion.radon.impl.let.webdav.fromcatalina.XMLHelper;
import net.ion.radon.impl.let.webdav.fromcatalina.XMLWriter;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DoProppatch extends AbstractMethod {

	private DoProppatch(VFileStore store) {
		super(store, Method.PROPPATCH);
	}

	public static AbstractMethod create(VFileStore store) {
		return new DoProppatch(store);
	}

	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse resp) throws IOException, SAXException {
		String parentPath = getVFile().getParent().getName().getPath();

		// TODO for now, PROPPATCH just sends a valid response, stating that everything is fine, but doesn't do anything.

		// Retrieve the resources
		if (!getVFile().exists()) {
			throw WebdavException.create(Status.CLIENT_ERROR_NOT_FOUND);
		}

		List<String> toset = null;
		List<String> toremove = null;
		List<String> tochange = new Vector<String>();
		// contains all properties from
		// toset and toremove

		Node tosetNode = null;
		Node toremoveNode = null;

		if (request.getEntity() == null || request.getEntity().getSize() == 0) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
		DocumentBuilder documentBuilder = getDocBuilder();
		InputStream input = request.getEntity().getStream();
		
		Document document = documentBuilder.parse(new InputSource(input));
		input.close() ;
		// Get the root element of the document
		Element rootElement = document.getDocumentElement();

		tosetNode = XMLHelper.findSubElement(XMLHelper.findSubElement(rootElement, "set"), "prop");
		toremoveNode = XMLHelper.findSubElement(XMLHelper.findSubElement(rootElement, "remove"), "prop");

		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("DAV:", "D");

		if (tosetNode != null) {
			toset = XMLHelper.getPropertiesFromXML(tosetNode);
			tochange.addAll(toset);
		}

		if (toremoveNode != null) {
			toremove = XMLHelper.getPropertiesFromXML(toremoveNode);
			tochange.addAll(toremove);
		}
		// Create multistatus object
		StringWriter writer = new StringWriter();
		XMLWriter generatedXML = new XMLWriter(writer, namespaces);
		generatedXML.writeXMLHeader();
		generatedXML.writeElement("DAV::multistatus", XMLWriter.OPENING);

		generatedXML.writeElement("DAV::response", XMLWriter.OPENING);
		String status = new String("HTTP/1.1 " + WebdavStatus.SC_OK + " " + WebdavStatus.getStatusText(WebdavStatus.SC_OK));

		// Generating href element
		generatedXML.writeElement("DAV::href", XMLWriter.OPENING);

		String baseName = WebDavUtil.escape(getVFile().getName().getBaseName());
		if ("".equals(baseName))
			baseName = "/";
		String hrefPath = getVFile().getName().getPath() + (getVFile().isDir() ? "/" : "");
		if ("//".equals(hrefPath))
			hrefPath = "/";

		generatedXML.writeText(hrefPath);

		generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);

		for (Iterator<String> iter = tochange.iterator(); iter.hasNext();) {
			String property = (String) iter.next();

			generatedXML.writeElement("DAV::propstat", XMLWriter.OPENING);

			generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);
			generatedXML.writeElement(property, XMLWriter.NO_CONTENT);
			generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);

			generatedXML.writeElement("DAV::status", XMLWriter.OPENING);
			generatedXML.writeText(status);
			generatedXML.writeElement("DAV::status", XMLWriter.CLOSING);

			generatedXML.writeElement("DAV::propstat", XMLWriter.CLOSING);
		}

		generatedXML.writeElement("DAV::response", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::multistatus", XMLWriter.CLOSING);

		generatedXML.sendData();

		Representation result = new StringRepresentation(writer.getBuffer(), MediaType.valueOf("text/xml; charset=UTF-8"));
		resp.setStatus(Status.SUCCESS_MULTI_STATUS);
		return result;
	}
}
