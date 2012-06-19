package net.ion.radon.impl.let.velocity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import net.ion.framework.util.Debug;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;

public class VelocityEntry {

	private static final String SUFFIX = ".vm";
	private VelocityEngine engine ;
	private ChainContext rootContext ;
	
	public VelocityEntry(String configPath) throws FileNotFoundException, IOException{
		initEngine(configPath);
	}
	
	
	public VelocityEntry(String configPath, String toolboxConfigPath) throws FileNotFoundException, IOException{
		this(configPath) ;
		setToolBox(toolboxConfigPath) ;
	}


	private void initEngine(String configPath) throws IOException, FileNotFoundException {
		Properties props = new Properties() ;
		props.load(new FileInputStream(new File(configPath))) ;
		Velocity.init(props) ;
		this.engine = new VelocityEngine();
		engine.init() ;
		this.rootContext = new ChainContext();
	}

	
	public void setToolBox(String toolboxConfigPath){
		ToolManager manager = new ToolManager();
		Debug.line(toolboxConfigPath) ;
		manager.configure(toolboxConfigPath);
		rootContext = new ChainContext(manager.createContext());
	}
	
	public void evaluate(Context context, Reader reader, Writer writer){
		final Context newRequestContext = newRequestContext(context);
		engine.evaluate(newRequestContext, writer, "log", reader);
	}
	
	public void merge(Context context, String resource, Writer writer){
		Template template = Velocity.getTemplate(resource) ;
		template.merge(newRequestContext(context), writer) ;
	}	

	private Context newRequestContext(Context context) {
		ChainContext result = new ChainContext(this.rootContext);
		result.addContext(context) ;
		return result ;
	}

}
