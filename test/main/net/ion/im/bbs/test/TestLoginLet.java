package net.ion.im.bbs.test;

import net.ion.framework.util.Debug;
import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestLoginLet extends TestAradonExtend {
	private String prefixURL = "riap://component/im/login" ;

	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL);
		String configPath = "plugin/im.bbs/plugin-im.xml";
		
		Form form = new Form();
		form.add("userId", "airkjh");
		form.add("password", "a2poNTY2MA==");
		form.add("auth", "719ff54d45d7951ceb1e1ff796dae45f");
		
		request.setEntity(form.getWebRepresentation());
		Response response = handle(configPath, request);
		
		Debug.debug(response.getEntityAsText());
	}
}
