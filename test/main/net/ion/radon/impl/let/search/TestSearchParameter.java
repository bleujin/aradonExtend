package net.ion.radon.impl.let.search;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.param.MyParameter;

public class TestSearchParameter extends TestCase{

	
	public void testParam() throws Exception {
		String param = "{query:'text', sort:'a, b', searchfilter:[{type:'nrange', name:'empno', from:3, to:5}, {type:'term', name:'empname', terms:'end'}], filter:'time:0 TO 222', page:{listNum:10, pageNo:1}, param:{userQuery:'', userId:'bleujin'} }";
		MyParameter mparam = MyParameter.create(param) ;
		
		Debug.debug(mparam.getParams("searchfilterDD")) ;
		Debug.debug(mparam.getParams("searchfilter")) ;
		
		Debug.debug(  MyParameter.create(mparam.getParams("searchfilter")[0]).getParam("from").getClass() ) ;
	}
}
