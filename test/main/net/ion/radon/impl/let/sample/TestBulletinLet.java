package net.ion.radon.impl.let.sample;

import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestBulletinLet extends TestAradonExtend {
	public void testAll() throws Exception {
		testDelete();
		testPut();
		testGet();
		testPost();
	}

	public void testPut() throws Exception {
		for (int i = 21; i < 22; i++) {
			Request request = new Request(Method.PUT, "riap://component/sample/bulletin/usergallery");

			Form form = new Form();
			form.add("boardid", "usergallery");
			form.add("reguserid", "bleujin");
			form.add("memo", "Hello Seoul Seoul");
			request.setEntity(form.getWebRepresentation());
			super.handle(request);
		}
	}

	public void testGet() throws Exception {
		Request request = new Request(Method.GET, "riap://component/sample/bulletin/bleujin/4cb686dfca31e99d78313085");
		super.handle(request);
	}

	public void testGetList() throws Exception {
		Request request = new Request(Method.GET, "riap://component/sample/bulletin/bleujin?page.pageNo=1&page.listNum=10");

		super.handle(request);
	}

	public void testDelete() throws Exception {
		Request request = new Request(Method.DELETE, "riap://component/sample/bulletin/bleujin/4c93228d2531f5f302082f43");
		Response response = handle(request);
		Debug.debug(response.getEntityAsText());
	}

	public void testPost() throws Exception {
		for (int i = 0; i < 200; i++) {
			Request request = new Request(Method.POST, "riap://component/sample/bulletin/usergallery?aradon.result.format=json");
			Form form = new Form();
			form.add("boardid", "usergallery");
			form.add("reguserid", "bleujin");
			form.add("memo", "Hello Busan");
			// form.add("aradon.result.format", "json");
			request.setEntity(form.getWebRepresentation());
			super.handle(request);
		}
	}
	
	public void testIsNumber() throws Exception {
		assertEquals(true, StringUtil.isNumeric("123")) ;
		assertEquals(false, StringUtil.isNumeric("123.22")) ;
		assertEquals(false, StringUtil.isNumeric("123d")) ;
		
		
	}

}