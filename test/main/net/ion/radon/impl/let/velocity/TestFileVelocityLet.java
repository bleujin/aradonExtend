package net.ion.radon.impl.let.velocity;

import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestFileVelocityLet extends TestAradonExtend{

	private String prefixURL = "riap://component/template/velocity/file/HelloWorld";

	public void testPost() throws Exception {
		Request request = new Request(Method.POST, prefixURL);

		Form form = new Form();
		form.add("template", "¾È³ç Hello ${source.name}, ${request.source}");
		form.add("source", "{'name':'bleujin'}");
		request.setEntity(form.getWebRepresentation());

		Response response = super.handle(request);
	}

}
