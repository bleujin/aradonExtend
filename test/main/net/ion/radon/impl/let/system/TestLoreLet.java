package net.ion.radon.impl.let.system;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestLoreLet  extends TestAradonExtend{

	private String prefixURL = "riap://comonnent/system/lore/test/ion.dev.floor4" ;
	public void testAll() throws Exception {
		testDelete() ;
		testPut() ;
		testGet() ;
		testPost() ;
	}
	
	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, prefixURL + "/7756");
		
		Form form = new Form() ;
		form.add("ename", "hero") ;
		form.add("empno", "7756") ;
		form.add("sal", "100") ;
		form.add("dept", "4층개발팀") ;
		form.add("address", "경기도 성남시 분당") ;
		request.setEntity(form.getWebRepresentation()) ;
		super.handle("resource/config/aradon-config.xml", request);
	}
	
	public void testGet() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/7756");
		super.handle("resource/config/aradon-config.xml", request);
	}
	public void testGetList() throws Exception {
		Request request = new Request(Method.GET, prefixURL);
		super.handle("resource/config/aradon-config.xml", request);
	}
	
	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, prefixURL + "/7756");
		super.handle("resource/config/aradon-config.xml", request);
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
		handle("resource/config/aradon-config.xml", request);
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
		handle("resource/config/aradon-config.xml", request);
	}
}