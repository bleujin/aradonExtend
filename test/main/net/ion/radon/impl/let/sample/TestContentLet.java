package net.ion.radon.impl.let.sample;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestContentLet extends TestAradonExtend{

	public void testAll() throws Exception {
		testDelete() ;
		testPut() ;
		testGet() ;
		testPost() ;
	}
	
	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, "riap://component/sample/content/test/content/bleujin");
		
		Form form = new Form() ;
		form.add("vid", "bleujin") ;
		form.add("subject", "bleujin Hi") ;
		form.add("content", "Hello Seoul") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	
	public void testGet() throws Exception {
		Request request = new Request(Method.GET, "riap://component/sample/content/test/content/bleujin");
		super.handle(request);
	}
	
	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, "riap://component/sample/content/test/content/bleujin");
		super.handle(request);
	}
	
	public void testPost() throws Exception {
		Request request = new Request(Method.POST, "riap://component/sample/content/test/content/bleujin");
		Form form = new Form() ;
		form.add("vid", "bleujin") ;
		form.add("subject", "bleujin Hi") ;
		form.add("content", "Hello Busan") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	

}
