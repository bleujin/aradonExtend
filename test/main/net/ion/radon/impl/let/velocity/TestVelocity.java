package net.ion.radon.impl.let.velocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.restlet.data.MediaType;
import org.restlet.ext.velocity.TemplateRepresentation;

public class TestVelocity extends TestCase{
	
	final String tbody = "하이 Hello bleujin ${param.query}, ${param.color[0]},  $esc.html(\"<>\")";
	String parameter = "{query:\"아이온\", sort:\"\", requestfilter:\"key:value AND range:[200 TO 300]\", page:{pageNo:3, listNum:10, screenCount:20}, color:[\"red\",\"white\",\"blue\"], param:{query:3, siteid:\"scat\"}}";

	
	private void evaluateFor(Context innerTool) throws Exception {
		//initVelocity() ;
		
		VelocityContext vcon = new VelocityContext(getDataModel(), innerTool) ;
		StringWriter w = new StringWriter() ;
		Velocity.evaluate(vcon, w, "log", StringUtil.toString(tbody)) ;
		Debug.line(w.getBuffer());
	}
	
	
	
	public void representationFor(Context innerTool) throws Exception {
		TemplateRepresentation tr2 = new TemplateRepresentation(Velocity.getTemplate("test2.vm"), MediaType.TEXT_ALL) ;
		tr2.setDataModel(getDataModel()) ;
		
		
		Debug.line(tr2.getTemplate().getEncoding(), tr2.getText());
	}


	public void testSpeed() throws Exception {
		initVelocity() ;
		Context context = initToolbox() ;
		
		context = new VelocityContext() ;
		for (int i = 0; i < 2; i++) {
			evaluateFor(context) ;
			// representationFor(context) ;
			// testFileLoader() ;
		}
	}
	
	protected Map<String, Object> getDataModel() {
		JsonObject obj = JsonObject.fromObject(parameter) ;
		
		Map<String, Object> dataModel = new HashMap<String, Object>() ;
		dataModel.put("param", obj) ;
		return dataModel;
	}


	private void initVelocity() throws FileNotFoundException, IOException {
		
		Properties props = new Properties();
		props.load(new FileInputStream(new File("resource/config/velocity.init.props"))) ;
	    Velocity.init(props);
	}
	
	private ToolContext initToolbox() throws Exception {
		ToolManager manager = new ToolManager();
		manager.configure("./resource/config/toolbox.xml");
		return manager.createContext();
	}
	
}


