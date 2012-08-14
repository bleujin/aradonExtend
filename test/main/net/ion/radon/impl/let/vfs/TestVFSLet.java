package net.ion.radon.impl.let.vfs;

import java.util.Date;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.core.Aradon;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.representation.StringRepresentation;

public class TestVFSLet extends TestCase{

	private Aradon aradon ;

	public void setUp() throws Exception{
		aradon = Aradon.create("resource/config/plugin-system-vfs.xml") ;
		aradon.start() ;
	}
	
	public void testGet() throws Exception {
		Request request = new Request(Method.GET, "riap://component/vfs/afield/imsi/Autobiography.txt") ;
		request.setEntity(new StringRepresentation("Hi Hello" +  new Date())) ;
	
		aradon.handle(request) ;
	}

	public void testGetDir() throws Exception {
		Request request = new Request(Method.GET, "riap://component/vfs/afield/imsi") ;
		request.setEntity(new StringRepresentation("Hi Hello" +  new Date())) ;
		
		Response response = aradon.handle(request) ;
		Debug.debug(response.getEntityAsText()) ;
	}

	
	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, "riap://component/vfs/afield/abcd/1123.txt") ;
		request.setEntity(new StringRepresentation("Hi Hello" +  new Date())) ;
		
		aradon.handle(request) ;
	}

	public void testPost() throws Exception {
		Request request = new Request(Method.POST, "http://localhost:9002/vfs/afield/abcd/1123.txt") ;
		request.setEntity(new StringRepresentation("Hi Hello" +  new Date())) ;
		
		aradon.handle(request) ;
	}

	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, "http://localhost:9002/vfs/afield/abcd/1123.txt") ;
		
		aradon.handle(request) ;
	}
	
	public void testFormPost() throws Exception {
		Request request = new Request(Method.POST, "http://localhost:9002/vfs/afield/abcd/1123.txt") ;
		Form form = new Form() ;
		form.add("key", "value") ;
		request.setEntity(form.getWebRepresentation()) ;
		
		aradon.handle(request) ;
	}

	
}
