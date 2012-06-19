package net.ion.radon.impl.let.search;

import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.let.DefaultLet;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Parameter;

public class TestSearchLet extends TestAradonExtend{

	private String prefixURL = "riap://component/system/search/test.mygroup" ;
	
	public void testGet() throws Exception {
		Request request = new Request(Method.GET, prefixURL + "/bleujin/vid/asc");
		super.handle(request);
	}


	// "{query:'text', sort:'a, b', searchfilter:[{type:'nrange', name:'empno', from:3, to:5}, {type:'term', name:'empname', terms:'end'}], filter:'time:0 TO 222', page:{listNum:10, pageNo:1}, param:{userQuery:'', userId:'bleujin'} }";
	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL + "/bleujin/vid/asc");
		Form form = new Form() ;
		form.add(new Parameter(DefaultLet.ARADON_PARAMETER, "{query:'bleujin', sort:'vid asc', filter:'aradon.group:test'}"));
		
		request.setEntity(form.getWebRepresentation()) ;
		super.handle(request);
	}

}
