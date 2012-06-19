package net.ion.radon.impl.let.system;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestSequenceLet extends TestAradonExtend{

	private String prefixURL = "riap://component/system/sequence" ;
	public void testAll() throws Exception {
		testDelete() ;
		testPut() ;
		testGet() ;
		testPost() ;
	}
	
	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, prefixURL + "/bulletin.bleujin/205");
		super.handle(request);
	}

	public void testGet() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/bulletin.bleujin");
		Response response = super.handle(request);
		assertEquals(200, response.getStatus().getCode()) ;
	}

	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL + "/bulletin.bleujin");
		Form form = new Form() ;
		// form.add("test", "value") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	
	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, prefixURL + "/bulletin.bleujin");
		super.handle(request);
	}
	
}
