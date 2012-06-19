package net.ion.radon.impl.let.sample;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

public class TestEmployee extends TestCase{

	public void testGetRequest() throws Exception {
		// DefaultServer.main(new String[0]) ;
		
		for (int i = 0; i < 1; i++) {
			long start = System.currentTimeMillis() ;
			ClientResource helloResource = new ClientResource("http://localhost:9002/sample/basic/employee/xml"); 
			Representation representation = helloResource.get();
			representation.write(System.out);
			Debug.debug("\n" + (System.currentTimeMillis() - start)) ;
		}
	}
	

	public void testPostRequest() throws Exception {
		// DefaultServer.main(new String[0]) ;
		
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.POST, "http://localhost:9002/sample/basic/employee");
		Form form = new Form() ;
		form.add("empNo", "2000") ;
		form.add("ename", "bleujin") ;
		form.add("job", "Dev") ;
		form.add("mgr", "7756") ;
		form.add("mgr", "7756") ;
		form.add("hireDate", "20080101-124121") ;
		form.add("sal", "500") ;
		form.add("deptNo", "20") ;
		
		request.setEntity(form.getWebRepresentation()) ;
		Response response = client.handle(request);
		Debug.debug(response.getEntityAsText()) ;
		client.stop() ;
	}
	
	public void testCreateEmployee() throws Exception {
		
	}
	
	public static void main(String[] args) throws Exception{
		final TestEmployee test = new TestEmployee();
		test.setUp() ;
		test.testGetRequest() ;
	}
}
