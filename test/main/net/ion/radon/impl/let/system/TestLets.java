package net.ion.radon.impl.let.system;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestLets extends TestAradonExtend{

	private String prefixURL = "riap://component/system/lets" ;
	public void testLetsGet() throws Exception {
		String aparam = "{aradon:" + 
		"[{name:'abcd',  section:'system', path:'/repository/bulletin.bleujin', param:{p1:'abc', p2:'${sequence.result.nodes[0].currval}', p3:['red','green','white'], modified:'${currdate.result.nodes[0].now}', 'aradon.result.format':'json', 'aradon.result.method':'get'}, page:{pageNo:1, listNum:10, screenCount:1}}, " +
		" {name:'sequence', section:'system', path:'/sequence/myseq', param:{'aradon.result.format':'json', 'aradon.result.method':'put'}}, " +
		" {name:'currdate', section:'system', path:'/utils/datetime', param:{'aradon.result.format':'json', 'aradon.result.method':'get'}}" +
		"]}" ;
		Request request = new Request(Method.POST, prefixURL);
		Form form = new Form() ;
		form.add("aradon.parameter", aparam) ;
		request.setEntity(form.getWebRepresentation()) ;
		
		handle("resource/config/aradon-config.xml", request);
	}
	
	public void testLetsPost() throws Exception {
		String aparam = "{aradon:" + 
		"[{name:'board',  	section:'system', path:'/repository/bulletin.bleujin', param:{subject:'HiHi This is Let', content:'11월엔 투피어', boardid:'board1', reguserid:'bleujin', no:'${sequence.result.nodes[0].currval}',  modified:'${currdate.result.nodes[0].now}', 'aradon.result.format':'json', 'aradon.result.method':'post', 'aradon.page.pageNo':1, 'aradon.page.listNum':10, 'aradon.page.screenCount':10}}, " +
		" {name:'sequence', section:'system', path:'/sequence/bulletin.bleujin', param:{'aradon.result.format':'json', 'aradon.result.method':'put'}}, " +
		" {name:'currdate', section:'system', path:'/utils/datetime', param:{'aradon.result.format':'json', 'aradon.result.method':'get'}}" +
		"]}" ;
		
		Request request = new Request(Method.POST, prefixURL);
		Form form = new Form() ;
		form.add("aradon.parameter", aparam) ;
		request.setEntity(form.getWebRepresentation()) ;
		
		handle("resource/config/aradon-config.xml", request);
	}
	
	public void testPost2() throws Exception {
		String aparam = "{'aradon':" +
			"[{'name':'employee','section':'system','path':'/lore/test/ion.dev.floor4/3477','param':{'empno':'3477','ename':'ddd','address':'ddd','sal':'20','dept':'ddd','memo':'222','aradon.result.method':'post', 'aradon.result.format':'json'}}, " +
			" {'name':'indexer','section':'system','path':'/index/ion.dev.floor4/3477','param':{'empno':'3477','ename':'ddd','address':'ddd','sal':'20','dept':'ddd','memo':'222','aradon.result.method':'post', 'aradon.result.format':'json'}}" +
			"]}" ;
		Request request = new Request(Method.POST, prefixURL);
		Form form = new Form() ;
		form.add("aradon.parameter", aparam) ;
		request.setEntity(form.getWebRepresentation()) ;
		
		handle("resource/config/aradon-config.xml", request);
	}
}
