package net.ion.radon.impl.let.db;

import net.ion.framework.util.Debug;
import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestProcedureLet extends TestAradonExtend{

	private String prefixURL = "riap://component/db/proc" ;
	
	public void testGet() throws Exception {
		//  /deptno/10?aradon.result.format=JSON
		//  /deptno/10?aradon.result.method=POST
		
		Request request = new Request(Method.GET, prefixURL + "/selectAll?aradon.result.format=json");
		Response response =  super.handle(request);
		
	}
	 
	public void testPostSelect() throws Exception {
		//  /deptno/10?aradon.result.format=JSON
		//  /deptno/10?aradon.result.method=POST
		
		Request request = new Request(Method.GET, prefixURL + "/employee_view?empno=7782&aradon.result.format=json&aradon.result.method=post");
		Response response =  super.handle(request);
		
	}
	
	public void testPostSelect2() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/employee_viewdept?empno=7369&deptno=10&aradon.result.format=json&aradon.result.method=post");
		Response response =  super.handle(request);
		
	}
	
	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL+"/employee_add?aradon.result.format=json");
		Form form = new Form() ;
		form.add("empno", "7777") ;
		form.add("ename", "heeya") ;
		form.add("job", "job");
		form.add("mgr", "7654");
		form.add("hiredate", "20101010");
		form.add("sal", "100") ;
		form.add("comm", "200") ;
		form.add("deptno", "10") ;
		request.setEntity(form.getWebRepresentation()) ;
		Response response = super.handle(request);
	}
	
	public void testPostGet() throws Exception {
		Request request = new Request(Method.POST, prefixURL+"/employee_edit");
		Form form = new Form() ;
		form.add("empno", "7777") ;
		form.add("ename", "heeya") ;
		form.add("job", "heeya job");
		form.add("mgr", "7654");
		form.add("hiredate", "20101010");
		form.add("sal", "100") ;
		form.add("comm", "200") ;
		form.add("deptno", "10") ;
		request.setEntity(form.getWebRepresentation()) ;
		Response response = super.handle(request);
		Debug.line(response);
		
		Request reqView = new Request(Method.POST, prefixURL + "/employee_view");
		Form formView = new Form();
		formView.add("empno", "7777");
		reqView.setEntity(formView.getWebRepresentation());
		response = super.handle(reqView);
		Debug.line(response);
	}
	
	public void testValidPost() throws Exception {
		Request request = new Request(Method.POST , prefixURL + "/selectAll");
		Response response =  super.handle(request);
		
		request = new Request(Method.POST , prefixURL + "/selectAll1");
		response =  super.handle(request);
		
		request = new Request(Method.POST , prefixURL + "/selectAll1");
		response =  super.handle(request);
	}
	
	public void testBatchPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL+"/dept_create");
		
		Form form = new Form() ;
		form.add("deptno", "100") ;
		form.add("deptno", "200") ;
		form.add("deptno", "300");
		form.add("dname", "ENGINE") ;
		form.add("dname", "ENGINE") ;
		form.add("dname", "ENGINE");
		form.add("loc", "CHICAGO") ;
		form.add("loc", "CHICAGO") ;
		form.add("loc", "CHICAGO");
		
		request.setEntity(form.getWebRepresentation()) ;
		Response response = super.handle(request);
	}
	
	public void testPostRemove() throws Exception {
		Request request = new Request(Method.POST, prefixURL+"/dept_remove");
		
		Form form = new Form() ;
		form.add("deptno", "100") ;
		form.add("deptno", "200") ;
		form.add("deptno", "300");
		
		request.setEntity(form.getWebRepresentation()) ;
		Response response = super.handle(request);
		
	} 
	
	public void testProcedures() throws Exception {
		Request request = new Request(Method.POST, prefixURL+"/dept_create_edit");
		
		Form form = new Form() ;
		form.add("deptno", "100") ;
		
		request.setEntity(form.getWebRepresentation()) ;
		Response response = super.handle(request);
		
	}
	
	public void testProceduresBatch() throws Exception {
		
		//dept_create_batch , dept_create_edit_batch
		Request request = new Request(Method.POST, prefixURL+"/dept_create_command");
		
		Form form = new Form() ;
		form.add("deptno", "200") ;
		form.add("deptno", "300") ;
		form.add("dname", "400") ;
		form.add("dname", "400") ;
		form.add("loc", "400") ;
		form.add("loc", "400") ;
		
		request.setEntity(form.getWebRepresentation()) ;
		Response response = super.handle(request);
	}

}
