package net.ion.radon.impl.let.search;

import net.ion.framework.util.RandomUtil;
import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestIndexLet  extends TestAradonExtend{
	

	private String prefixURL = "riap://component/system/index/test.mygroup" ;
	
	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL + "/4cc6c273485d954b6c73a803" + RandomUtil.nextRandomString(10));

		Form form = new Form();
		form.add("vid", String.valueOf(RandomUtil.nextRandomInt(100)));
		form.add("subject", "bleujin Hi");
		form.add("content", "Hello Seoul Seoul");
		request.setEntity(form.getWebRepresentation());		
		
		super.handle(request);
	}

	public void testPut() throws Exception {
		Request request = new Request(Method.PUT, prefixURL + "/4cc6c273485d954b6c73a803");

		Form form = new Form();
		form.add("vid", RandomUtil.nextRandomString(20));
		form.add("subject", "bleujin Hi");
		form.add("content", "Hello Seoul Seoul");
		request.setEntity(form.getWebRepresentation());		
		
		super.handle(request);
	}

}
