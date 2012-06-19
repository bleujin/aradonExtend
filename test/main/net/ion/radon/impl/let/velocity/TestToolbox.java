package net.ion.radon.impl.let.velocity;

import java.io.StringReader;
import java.io.StringWriter;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;

public class TestToolbox extends TestCase{

	public void testFirst() throws Exception {
		Velocity.init() ;
		
		VelocityEngine engine = new VelocityEngine() ;
		StringWriter writer = new StringWriter() ;
		ToolManager manager = new ToolManager();
		
		manager.configure("./resource/config/toolbox.xml");
		Context tcontext = manager.createContext();
		
		Context vcontext = new ChainContext(tcontext) ;
		
		engine.evaluate(vcontext, writer, "log", "Test template : $esc.html(\"<>\")");
		
		Debug.debug(writer) ;
	}
	
	public void testVelocityEntry() throws Exception {
		VelocityEntry entry = new VelocityEntry("././resource/config/velocity.init.props", "./resource/config/toolbox.xml");
		StringReader reader = new StringReader("Test template : $esc.html(\"<>\")") ;
		StringWriter writer = new StringWriter() ;
		
		final VelocityContext mycontext = new VelocityContext();
		mycontext.put("mykey", "myvalue") ;
		
		ChainContext c = new ChainContext(mycontext) ;
		entry.evaluate(c, reader, writer) ;
		
		
		Debug.debug(writer) ;
	}
}
