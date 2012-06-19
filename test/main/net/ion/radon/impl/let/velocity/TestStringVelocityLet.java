package net.ion.radon.impl.let.velocity;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestStringVelocityLet extends TestAradonExtend{

	private String prefixURL = "riap://component/template/velocity/string" ; 
	
	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, prefixURL + "/test.bleujin/hello") ;
		
		Form form = new Form() ;
		form.add("template", "¾È³ç Hello ${source.name}, ${request.source}") ;
		form.add("other", "{'name':'bleujin'}") ;
		request.setEntity(form.getWebRepresentation()) ;

		Response response = super.handle(request) ;
	}
	
	public void testGet() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/test.bleujin/hello") ;
		Response response = super.handle(request) ;
	}
	
	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL + "/test.bleujin/hello") ;
		
		Form form = new Form() ;
		form.add("source", "{'name':'bleujin'}") ;
		request.setEntity(form.getWebRepresentation()) ;

		Response response = super.handle(request) ;
	}
	
	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, prefixURL + "/test.bleujin/hello") ;
		
		Response response = super.handle(request) ;
	}
}
