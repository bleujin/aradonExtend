package net.ion.radon.impl.let.velocity;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.core.TreeContext;

import org.apache.velocity.context.Context;
import org.restlet.routing.VirtualHost;

public class TestVelocityEntry extends TestCase {

	private VelocityEntry entry;

	public void setUp() throws Exception {
		
		entry = new VelocityEntry("./resource/config/velocity.init.props", "./resource/config/toolbox.xml");
	}

	public void testLoad() throws Exception {
	}

	public void testToolBox() throws Exception {
		entry.setToolBox("D:\\eclipse\\workspace\\AradonExtend\\plugin\\radon.velocity/resource/toolbox.xml") ;
		Context context = RadonVelocityContext.create(null, TreeContext.createRootContext(new VirtualHost(new org.restlet.Context())), null);
		context.put("name", "bleujin");
		StringReader reader = new StringReader("This is a ${name} Test template : $esc, $esc.html(\"<>\") $req.html");
		StringWriter writer = new StringWriter();
		entry.evaluate(context, reader, writer);

		Debug.debug(writer);
	}
	
	public void testToolBox2() throws Exception {
		Context context = RadonVelocityContext.create(null, TreeContext.createRootContext(new VirtualHost(new org.restlet.Context())), null);
		context.put("name", "bleujin");
		StringReader reader = new StringReader("This is a ${name} Test template : ${request}.html");
		StringWriter writer = new StringWriter();
		entry.evaluate(context, reader, writer);

		Debug.debug(writer);
	}
	
	public void testResource() throws Exception {
		Context context = RadonVelocityContext.create(null, TreeContext.createRootContext(new VirtualHost(new org.restlet.Context())), null);
		context.put("name", "bleujin");
		String resourceName = "helloworld.vm";
		StringWriter writer = new StringWriter();
		entry.merge(context, resourceName, writer);

		Debug.debug(writer);
	}

	public void testSpeed() throws Exception {
		for (int i = 0; i < 10000; i++) {
			testToolBox();
		}
	}
	
	public void testContext() throws Exception {
		Debug.debug(Context.class.getCanonicalName()) ;
	}
}
