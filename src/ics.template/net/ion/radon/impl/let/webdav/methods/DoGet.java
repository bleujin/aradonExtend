package net.ion.radon.impl.let.webdav.methods;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import net.ion.framework.util.DateUtil;
import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

public class DoGet extends AbstractMethod {
	
	private DoGet(VFileStore store) {
		super(store, Method.GET);
	}
	
	public static DoGet create(VFileStore store){
		return new DoGet(store);
	}
	
	@Override
	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws IOException {
		MediaType mtype = getVFile().isFile() ? MediaType.valueOf(getVFile().getContent().getContentType()) : MediaType.TEXT_HTML ;
		
		final InputStream input = getInputStream(transaction, getVFile(), request);
		InputRepresentation result = new InputRepresentation(input, mtype, getVFile().isFile() ? getVFile().getContent().getSize() : -1);
		response.setStatus(Status.SUCCESS_OK) ;
		response.setEntity(result) ;
		return response.getEntity();
	}
	
	private InputStream getInputStream(ITransaction transaction, VFile vfile, Request request) throws IOException {
		if(vfile.isFile())
			return vfile.getInputStream();
		else {
			return folderBody(request, vfile);
		}
	}

	protected InputStream folderBody(Request request, VFile vfile) throws IOException {
		String path = vfile.getName().getPath() ;

		StringBuilder childrenTemp = new StringBuilder();
		childrenTemp.append("<html><head><title>Content of folder");
		childrenTemp.append(path);
		childrenTemp.append("</title><style type=\"text/css\">");
		childrenTemp.append(getCSS());
		childrenTemp.append("</style></head>");
		childrenTemp.append("<body>");
		childrenTemp.append(getHeader(path));
		childrenTemp.append("<table>");
		childrenTemp.append("<tr><th>Name</th><th>Size</th><th>Modified</th></tr>");
		childrenTemp.append("<tr>");
		childrenTemp.append("<td colspan=\"3\"><a href=\"../\">Parent</a></td></tr>");
		boolean isEven = false;
		
		for (VFile child : vfile.getChildren()) {
			isEven = !isEven;
			childrenTemp.append("<tr class=\"");
			childrenTemp.append(isEven ? "even" : "odd");
			childrenTemp.append("\">");
			childrenTemp.append("<td>");
			childrenTemp.append("<a href=\"");
			childrenTemp.append(request.getResourceRef().getBaseRef().toString() + child.getName().getPath());
			if (child.isDir()) {
				childrenTemp.append("/");
			}
			childrenTemp.append("\">");
			childrenTemp.append(child.getName().getBaseName());
			childrenTemp.append("</a></td>");
			if (child.isDir()) {
				childrenTemp.append("<td>Folder</td>");
			} else {
				childrenTemp.append("<td>");
				childrenTemp.append(child.getContent().getSize());
				childrenTemp.append(" Bytes</td>");
			}
			childrenTemp.append("<td>");
			childrenTemp.append(DateUtil.toHTTPDateFormat(new Date(vfile.getContent().getLastModifiedTime())));
			childrenTemp.append("</td>");
			childrenTemp.append("</tr>");
		}
		childrenTemp.append("</table>");
		childrenTemp.append(getFooter(path));
		childrenTemp.append("</body></html>");
		
		return new ByteArrayInputStream(childrenTemp.toString().getBytes("UTF-8")) ;
	}

	protected String getCSS() {
		String retVal = "body {\n" + "	font-family: Arial, Helvetica, sans-serif;\n" + "}\n" + "h1 {\n" + "	font-size: 1.5em;\n" + "}\n" + "th {\n" + "	background-color: #9DACBF;\n" + "}\n" + "table {\n" + "	border-top-style: solid;\n"
				+ "	border-right-style: solid;\n" + "	border-bottom-style: solid;\n" + "	border-left-style: solid;\n" + "}\n" + "td {\n" + "	margin: 0px;\n" + "	padding-top: 2px;\n" + "	padding-right: 5px;\n" + "	padding-bottom: 2px;\n"
				+ "	padding-left: 5px;\n" + "}\n" + "tr.even {\n" + "	background-color: #CCCCCC;\n" + "}\n" + "tr.odd {\n" + "	background-color: #FFFFFF;\n" + "}\n" + "";
		return retVal;
	}

	protected String getHeader(String path) {
		return "<h1>Content of folder " + path + "</h1>";
	}

	protected String getFooter(String path) {
		return "";
	}

}
