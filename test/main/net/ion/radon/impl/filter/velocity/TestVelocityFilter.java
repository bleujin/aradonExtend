package net.ion.radon.impl.filter.velocity;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.RadonAttributeKey;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.config.Attribute;
import net.ion.radon.core.config.PathConfiguration;
import net.ion.radon.core.config.SectionConfiguration;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.core.filter.HelloBean;
import net.ion.radon.core.filter.HiFilter;
import net.ion.radon.core.filter.IRadonFilter;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.core.let.PathService;
import net.ion.radon.impl.let.HelloWorldLet;
import net.ion.radon.impl.let.velocity.VelocityEntry;
import net.ion.radon.impl.section.PathInfo;
import net.ion.radon.param.ParamToBeanFilter;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

public class TestVelocityFilter extends TestAradonExtend{

	
	public void testLoad() throws Exception {
		initAradon() ;
		
		Request request = new Request(Method.GET, "riap://component/") ;
		Response response = aradon.handle(request) ;
		
		assertEquals(200, response.getStatus().getCode()) ;
		Debug.debug(response.getEntityAsText()) ;
	}
	
	
	public void testLoadLet() throws Exception {
		initAradon() ;
		SectionService section = aradon.getChildService("another");

		section.attach(PathConfiguration.create("mylet", "/bleujin, /bleujin/{greeting}", HelloWorldLet.class));
		
		Request request = new Request(Method.GET, "riap://component/another/bleujin") ;
		Response response = aradon.handle(request) ;
		
		assertEquals(200, response.getStatus().getCode()) ;
		Debug.debug(response.getEntityAsText()) ;
	}
	
	public void testVelocity() throws Exception {
		initAradon() ;
		SectionService sec = aradon.attach(SectionConfiguration.createBlank("test")) ;

		sec.attach(PathConfiguration.create("mylet", "/datas", TestDataLet.class));
		
		
		PathService pservice = sec.getChildService("mylet");
		
		final String entryId = "aradon.template.velocity.entry";
		IRadonFilter filter = new VelocityFilter(entryId) ;
		filter.addAttribute("hello", Attribute.testCreate(MapUtil.create("[@script-source]", "helloworld.vm"), "")) ;
		
		final String configPath = "src/radon.velocity/resource/velocity.init.props";
		final String toolboxConfigPath = "src/radon.velocity/resource/toolbox.xml";


		VelocityEntry entry = new VelocityEntry(configPath, toolboxConfigPath) ;
		pservice.getServiceContext().putAttribute(entryId, entry) ;
		
		pservice.getConfig().addAfterFilter(filter) ;

		
		Request request = new Request(Method.GET, "riap://component/test/datas?aradon.result.format=html.hello&id=bleujin");
		Response response = aradon.handle(request);

		Debug.line(response.getEntityAsText(), response.getEntity().getMediaType()) ;
	}
	
	
	
	
	
}
