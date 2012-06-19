package net.ion.radon.impl.let.webdav;

import java.io.File;
import java.util.Date;

import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.RadonAttributeKey;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.StringRepresentation;

public class TestWebDav extends TestAradonExtend{

	public void testProfind() throws Exception {
		final Request request = new Request(Method.PROPFIND, "riap://component/webdav/afield/Updates");
		Form form = (Form) request.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
		if (form == null) form = new Form() ;
		form.add("Depth", "1") ;
		
		String text = "" + 
		"<?xml version=\"1.0\" ?>\n" + 
		"<D:propfind xmlns:D=\"DAV:\">\n" + 
		"        <D:allprop/>\n" + 
		"</D:propfind>\n" ;
		
		StringRepresentation re = new StringRepresentation(text) ;
		// request.setEntity(re); 
		
		Response response = super.handle("resource/config/plugin-system-vfs.xml", request);

		Debug.debug(response.getEntityAsText()) ;
	}

	public void testOptions() throws Exception {
		final Request request = new Request(Method.OPTIONS, "riap://component/webdav/afield");
		
		Response response = handle("resource/config/plugin-system-vfs.xml", request);
		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
		Debug.debug(headers) ;
	}

	public void testHead() throws Exception {
		final Request request = new Request(Method.HEAD, "riap://component/webdav/afield/setup.exe");
		
		Response response = handle("resource/config/plugin-system-vfs.xml", request);
		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
		Debug.debug(headers) ;
	}
	
	public void testMove() throws Exception {
		final Request request = new Request(Method.GET, "riap://component/webdav/afield/setup.xml");
	}

	public void testGet() throws Exception {
		final Request request = new Request(Method.GET, "riap://component/webdav/afield/setup.xml");
		
		Response response = handle("resource/config/plugin-system-vfs.xml", request);
		Debug.debug(response.getEntityAsText()) ;
		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
		Debug.debug(headers) ;
	}

	public void testGetDir() throws Exception {
		final Request request = new Request(Method.GET, "riap://component/webdav/afield/abcd/");
		
		Response response = handle("resource/config/plugin-system-vfs.xml", request);
		Debug.debug(response.getEntityAsText()) ;
		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
		Debug.debug(headers) ;
	}

	public void testDelete() throws Exception {
		final Request request = new Request(Method.DELETE, "riap://component/webdav/afield/1.test");
		
		Response response = handle("resource/config/plugin-system-vfs.xml", request);
		Debug.debug(response.getEntityAsText()) ;
		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
		Debug.debug(headers) ;
	}

	public void testMkCreate() throws Exception {
		final Request request = new Request(Method.MKCOL, "riap://component/webdav/afield/mydir/newdir");
		
		Response response = handle("resource/config/plugin-system-vfs.xml", request);
		Debug.debug(response.getEntityAsText()) ;
		Form headers = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS) ;
		Debug.debug(headers) ;
		assertEquals(Status.SUCCESS_CREATED.getCode(), response.getStatus().getCode()) ;
	}


//
	public void testPut() throws Exception {
		final Request request = new Request(Method.PUT, "riap://component/webdav/afield/dd.xml");
		request.setEntity(new FileRepresentation(new File("resource/bak/dd.xml"), MediaType.APPLICATION_XML)) ;
		
		Response response = handle("resource/config/plugin-system-vfs.xml", request);
		Debug.debug(response.getEntity()) ;
		Debug.line(response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS)) ;
		
//		final Request getRequest = new Request(Method.GET, "riap://component/webdav/ram/dd.xml");
//		Response getResponse = aradon.getContext().getClientDispatcher().handle(getRequest) ;
//		Debug.debug(getResponse.getEntityAsText()) ;
	}
	
	public void testCalendar() throws Exception {
		Debug.debug(DateUtil.toHTTPDateFormat(new Date())) ;
		
		Debug.debug(DateUtil.dateToString(new Date(), "EEE, dd MMM yyyy HH:mm:ss zzz")) ;
	}
}
