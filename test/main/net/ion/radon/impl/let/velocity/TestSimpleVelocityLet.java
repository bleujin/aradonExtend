package net.ion.radon.impl.let.velocity;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestSimpleVelocityLet extends TestAradonExtend{

	private String prefixURL = "riap://component/template/velocity/simple";

	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL);

		Form form = new Form();
		form.add("template", "�ȳ� Hello ${source.name}, ${request.template}");
		form.add("source", "{'name':'bleujin'}");
		request.setEntity(form.getWebRepresentation());

		Response response = super.handle(request);
		

		
	}

}
