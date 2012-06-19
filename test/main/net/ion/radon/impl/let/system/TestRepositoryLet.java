package net.ion.radon.impl.let.system;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestRepositoryLet extends TestAradonExtend{

	private String prefixURL = "riap://component/system/repository/test" ;
	public void testAll() throws Exception {
		testDelete() ;
		testPut() ;
		testGet() ;
		testPost() ;
	}
	
	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, prefixURL + "/4cc7e5e8e435954b14ba6ee8");
		
		Form form = new Form() ;
		form.add("_parameter", "{vid:\"bleujin\", subject:\"bleujin Hi\", name:\"bleujin Seoul\"}") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	
	public void testGet() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/4cc7e5e8e435954b14ba6ee8");
		super.handle(request);
	}
	
	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, prefixURL + "/4cc7e5e8e435954b14ba6ee8");
		super.handle(request);
	}
	
	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL );
		Form form = new Form() ;
		form.add("_parameter", "{weight:20, subject:\"bleujin Hi\", name:\"bleujin\"}") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	
}
