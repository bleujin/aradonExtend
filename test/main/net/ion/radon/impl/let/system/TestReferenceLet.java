package net.ion.radon.impl.let.system;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

public class TestReferenceLet extends TestAradonExtend{

	private String prefixURL = "riap://component/system/reference" ;
	public void testAll() throws Exception {
		testDelete() ;
		testPut() ;
		testGetFrom() ;
		testPost() ;
	}
	
	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, prefixURL + "/test.json");
		super.handle(request);
	}

	public void testGetFrom() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/4d072202f2596f58ac4a8712");
		Response response = super.handle(request);
	}

	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL + "/4d072202f2596f58ac4a8712/part/4d072b04366f0b4a715a4b90");
		super.handle(request);

		request = new Request(Method.POST, prefixURL + "/4d072202f2596f58ac4a8712/part/4d072b04366f0b4a725a4b90");
		super.handle(request);
	}
	
	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, prefixURL + "/4c9346e7464cf5f3c6a7d842");
		super.handle(request);
	}
	
}
