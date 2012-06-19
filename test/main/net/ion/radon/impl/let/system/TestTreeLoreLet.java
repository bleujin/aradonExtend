package net.ion.radon.impl.let.system;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestTreeLoreLet extends TestAradonExtend{

	private String prefixURL = "riap://component/system/tree/self/test/root" ;
	public void testAll() throws Exception {
		testDelete() ;
		testPut() ;
		testGet() ;
		testPost() ;
	}
	
	public void testCreateRoot() throws Exception {
		Request request = new Request(Method.POST, prefixURL);
		
		Form form = new Form() ;
		form.add("name", "root") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}

	public void testDeleteRoot() throws Exception {
		Request request = new Request(Method.DELETE, prefixURL);
		
		Form form = new Form() ;
		form.add("name", "root") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	
	public void testUpdateRoot() throws Exception {
		Request request = new Request(Method.PUT, prefixURL);
		
		Form form = new Form() ;
		form.add("name", "root") ;
		form.add("address", "seoul") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	
	public void testGetRoot() throws Exception {
		Request request = new Request(Method.GET, prefixURL);
		
		Form form = new Form() ;
		form.add("name", "root") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}


	
	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, prefixURL);
		
		Form form = new Form() ;
		form.add("ename", "hero") ;
		form.add("empno", "7756") ;
		form.add("sal", "100") ;
		form.add("dept", "4층개발팀") ;
		form.add("address", "경기도 성남시 분당") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	
	public void testGet() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/7756");
		super.handle(request);
	}
	public void testGetList() throws Exception {
		Request request = new Request(Method.GET, prefixURL);
		super.handle(request);
	}
	
	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, prefixURL + "/7756");
		super.handle(request);
	}
	
	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL + "/7756" );
		Form form = new Form() ;
		form.add("ename", "bleujin") ;
		form.add("empno", "7756") ;
		form.add("sal", "100") ;
		form.add("dept", "4층개발팀") ;
		form.add("address", "경기도 성남시 분당") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}	
	public void testPost2() throws Exception {
		Request request = new Request(Method.POST, prefixURL + "/7780" );
		Form form = new Form() ;
		form.add("ename", "hero") ;
		form.add("empno", "7780") ;
		form.add("sal", "120") ;
		form.add("dept", "4층개발팀") ;
		form.add("address", "경기도 성남시 분당") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}
	
	public void testParentPath() throws Exception {
		String path = "/ion/dev/floor6/7756" ;
		final String[] paths = StringUtil.split(path, "./");
		Debug.debug(paths, StringUtil.join(paths, "/", 0, paths.length-1)) ; 
		
		
		
	}
}