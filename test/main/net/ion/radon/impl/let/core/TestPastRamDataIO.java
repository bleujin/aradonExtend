package net.ion.radon.impl.let.core;

import net.ion.framework.util.Debug;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.impl.section.PathInfo;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestPastRamDataIO extends TestAradonExtend{
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		initAradon();
		SectionService section = aradon.attach("core", XMLConfig.BLANK);
		section.attach(PathInfo.create("dataio", "/dataio/{groupid}, /dataio/{groupid}/{id}" , RamDataStore.class)) ;
	}
	
	public void testAdd() throws Exception {
		
		Request request = new Request(Method.PUT, "riap://component/core/dataio/bleujin/1") ;
		Form form = new Form() ;
		form.add("subject", "Bleujin Hi~") ;
		form.add("key", "greeting") ;
		form.add("creuserid", "bleujin");
		form.add("content", "Hello") ;
		request.setEntity(form.getWebRepresentation());
		
		Response response = aradon.handle(request);
		Debug.line(response.getEntityAsText());
	}
	
	
	
	public void testGet() throws Exception {
		testAdd() ;
		Request request = new Request(Method.GET, "riap://component/core/dataio/bleujin/1") ;

		Response response = aradon.handle(request);
		Debug.line(response.getEntityAsText());
	}
	
	public void testDelete() throws Exception {
		testAdd();
		Request request = new Request(Method.DELETE, "riap://component/core/dataio/bleujin/1") ;
		Response response = aradon.handle(request);
		Debug.line(response.getEntityAsText());
	}
	
	public void testEdit() throws Exception {
		
		testAdd() ;
		
		Request request = new Request(Method.POST, "riap://component/core/dataio/bleujin/1") ;
		Form form = new Form() ;
		form.add("subject", "Heeya Hi~") ;
		form.add("key", "greeting") ;
		form.add("creuserid", "bleujin");
		form.add("content", "Hello") ;
		request.setEntity(form.getWebRepresentation());
		
		Response response = aradon.handle(request);
		Debug.line(response.getEntityAsText());
	}


}
