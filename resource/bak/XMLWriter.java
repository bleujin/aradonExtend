package net.ion.radon.impl.let.vfs;

import java.io.IOException;
import java.io.Writer;

/**
 * XMLWriter helper class.
 * 
 * @author <a href="mailto:remm@apache.org">Remy Maucherat</a>
 */
public class XMLWriter {

	// -------------------------------------------------------------- Constants

	/**
	 * Opening tag.
	 */
	public static final int OPENING = 0;

	/**
	 * Closing tag.
	 */
	public static final int CLOSING = 1;

	/**
	 * Element with no content.
	 */
	public static final int NO_CONTENT = 2;

	// ----------------------------------------------------- Instance Variables

	/**
	 * Buffer.
	 */
	protected StringBuffer buffer = new StringBuffer();

	/**
	 * Writer.
	 */
	protected Writer writer = null;

	// ----------------------------------------------------------- Constructors

	/**
	 * Constructor.
	 */
	public XMLWriter() {
	}

	/**
	 * Constructor.
	 * 
	 * @param theWriter
	 *            the provided Writer
	 */
	public XMLWriter(Writer theWriter) {
		writer = theWriter;
	}

	// --------------------------------------------------------- Public Methods

	/**
	 * Retrieve generated XML.
	 * 
	 * @return String containing the generated XML
	 */
	@Override
	public String toString() {
		return buffer.toString();
	}

	/**
	 * Write property to the XML.
	 * 
	 * @param namespace
	 *            Namespace
	 * @param namespaceInfo
	 *            Namespace info
	 * @param name
	 *            Property name
	 * @param value
	 *            Property value
	 */
	public void writeProperty(String namespace, String namespaceInfo, String name, String value) {
		writeElement(namespace, namespaceInfo, name, OPENING);
		buffer.append(value);
		writeElement(namespace, namespaceInfo, name, CLOSING);

	}

	/**
	 * Write property to the XML.
	 * 
	 * @param namespace
	 *            Namespace
	 * @param name
	 *            Property name
	 * @param value
	 *            Property value
	 */
	public void writeProperty(String namespace, String name, String value) {
		writeElement(namespace, name, OPENING);
		buffer.append(value);
		writeElement(namespace, name, CLOSING);
	}

	/**
	 * Write property to the XML.
	 * 
	 * @param namespace
	 *            Namespace
	 * @param name
	 *            Property name
	 */
	public void writeProperty(String namespace, String name) {
		writeElement(namespace, name, NO_CONTENT);
	}

	/**
	 * Write an element.
	 * 
	 * @param name
	 *            Element name
	 * @param namespace
	 *            Namespace abbreviation
	 * @param type
	 *            Element type
	 */
	public void writeElement(String namespace, String name, int type) {
		writeElement(namespace, null, name, type);
	}

	/**
	 * Write an element.
	 * 
	 * @param namespace
	 *            Namespace abbreviation
	 * @param namespaceInfo
	 *            Namespace info
	 * @param name
	 *            Element name
	 * @param type
	 *            Element type
	 */
	public void writeElement(String namespace, String namespaceInfo, String name, int type) {
		if (namespace != null && namespace.length() > 0)
			switch (type) {
			case OPENING:
				if (namespaceInfo != null)
					buffer.append("<" + namespace + ":" + name + " xmlns:" + namespace + "=\"" + namespaceInfo + "\">");
				else
					buffer.append("<" + namespace + ":" + name + ">");
				break;
			case CLOSING:
				buffer.append("</" + namespace + ":" + name + ">\n");
				break;
			case NO_CONTENT:
			default:
				if (namespaceInfo != null)
					buffer.append("<" + namespace + ":" + name + " xmlns:" + namespace + "=\"" + namespaceInfo + "\"/>");
				else
					buffer.append("<" + namespace + ":" + name + "/>");
				break;
			}
		else
			switch (type) {
			case OPENING:
				buffer.append("<" + name + ">");
				break;
			case CLOSING:
				buffer.append("</" + name + ">\n");
				break;
			case NO_CONTENT:
			default:
				buffer.append("<" + name + "/>");
				break;
			}
	}

	/**
	 * Write text.
	 * 
	 * @param text
	 *            Text to append
	 */
	public void writeText(String text) {
		buffer.append(text);
	}

	/**
	 * Write data.
	 * 
	 * @param data
	 *            Data to append
	 */
	public void writeData(String data) {
		buffer.append("<![CDATA[" + data + "]]>");
	}

	/**
	 * Write XML Header.
	 */
	public void writeXMLHeader() {
		buffer.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n");
	}

	/**
	 * Send data and reinitializes buffer.
	 * 
	 * @throws IOException
	 */
	public void sendData() throws IOException {
		if (writer != null) {
			writer.write(buffer.toString());
			buffer = new StringBuffer();
		}
	}

}
