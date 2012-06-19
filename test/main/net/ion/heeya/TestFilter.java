package net.ion.heeya;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

public class TestFilter extends TestAradonExtend{
	
	private String prefixURL = "riap://component/db/proc" ;
	public void testCountFilter() throws Exception {
		
		Request request = new Request(Method.GET, prefixURL + "/selectAll");
		Response response =  super.handle(request);
		
		request = new Request(Method.GET, prefixURL + "/selectAll");
		response =  super.handle(request);
		
		request = new Request(Method.GET, prefixURL + "/selectAll");
		response =  super.handle(request);
		
		request = new Request(Method.GET, prefixURL + "/selectAll");
		response =  super.handle(request);
		
		
		final String entityAsText = response.getEntityAsText();
		assertEquals(true, entityAsText != null && entityAsText.length() > 10) ;
	}
	
	public void testTimeFilter() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/selectAll");
		Response response =  super.handle(request);
		
		Thread.sleep(1000);

		request = new Request(Method.GET, prefixURL + "/selectAll");
		response =  super.handle(request);
		
		
		Thread.sleep(3300);
		request = new Request(Method.GET, prefixURL + "/selectAll");
		response =  super.handle(request);
		
		final String entityAsText = response.getEntityAsText();
		assertEquals(true, entityAsText != null && entityAsText.length() > 10) ;
		
	}
	
	public void testHttpMethod() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/selectAll");
		Response response =  super.handle(request);
		
		request = new Request(Method.GET, prefixURL + "/selectAll");
		response =  super.handle(request);
		
		request = new Request(Method.GET, prefixURL + "/selectAll");
		response =  super.handle(request);
		
		request = new Request(Method.GET, prefixURL + "/selectAll");
		response =  super.handle(request);
		
	}
}
