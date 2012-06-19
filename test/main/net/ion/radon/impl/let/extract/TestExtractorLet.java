package net.ion.radon.impl.let.extract;

import java.io.FileInputStream;

import net.ion.framework.util.Debug;
import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.representation.InputRepresentation;

public class TestExtractorLet extends TestAradonExtend{

	public String prefix = "riap://component/extract";
	public void testExtractGet() throws Exception {
		Request requeset = new Request(Method.GET,  prefix + "/stream");
		Response response = super.handle(requeset);
		Debug.debug(response);
		
	}
	
	public void testExtractor() throws Exception {
		Request request = new Request(Method.POST, prefix + "/stream");
		request.setEntity(new InputRepresentation(new FileInputStream("./imsi/input/hello.doc"))) ;
		Response response = super.handle(request);
		Debug.debug(response);
	}
	
	public void testUrlExtractor() throws Exception {
		Request request = new Request(Method.POST, prefix + "/url");
		
		Form form = new Form() ;
		form.add("url", "http://sports.media.daum.net/baseball/news/breaking/view.html?cateid=1028&newsid=20100824103029740&p=nocut") ;
		request.setEntity(form.getWebRepresentation()) ;
		
		Response response = super.handle(request);
		Debug.debug(response);
	}
	

}
