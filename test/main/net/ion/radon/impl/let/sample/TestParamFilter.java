package net.ion.radon.impl.let.sample;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.parse.gson.JsonUtil;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.param.ParamToBeanFilter;
import net.ion.radon.util.AradonTester;

import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class TestParamFilter extends TestCase{

	public void testFilter() throws Exception {
		Aradon aradon = AradonTester.create().register("", "/test", ConfirmLet.class).getAradon() ;
		aradon.addPreFilter(ParamToBeanFilter.create("emp", Employee.class)) ;
		
		AradonClient ac = AradonClientFactory.create(aradon) ;
		IAradonRequest request = ac.createRequest("/test") ;
		request.addParameter("empNo", "100").addParameter("ename", "bleujin").addParameter("job", "dev").addParameter("sal", "500").addParameter("names", "bleu").addParameter("names", "jin") ;
		
		Response res = request.handle(Method.POST) ;
		JsonObject rtn = JsonParser.fromString(res.getEntityAsText()).getAsJsonObject() ;
		
		assertEquals("bleujin", rtn.asString("ename")) ;
		assertEquals(100, rtn.asInt("empNo")) ;
		assertEquals(100L, JsonUtil.toSimpleObject(rtn.get("empNo"))) ;
		assertEquals("[\"bleu\",\"jin\"]", rtn.asJsonArray("names").toString()) ;
	}
}


class ConfirmLet extends AbstractServerResource {
	
	@Get
	public String confirm(){
		Employee emp = getContext().getAttributeObject("emp", Employee.class) ;
		return JsonParser.fromObject(emp).toString() ;
	}
	
	@Post
	public String post(){
		return confirm() ;
	}
	
}