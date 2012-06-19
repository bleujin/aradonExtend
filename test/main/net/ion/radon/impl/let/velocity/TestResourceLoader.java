package net.ion.radon.impl.let.velocity;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.bson.types.ObjectId;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.ext.velocity.RepresentationResourceLoader;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class TestResourceLoader extends TestVelocity{

	
	public void testLoader() throws Exception {

		ExtendedProperties props = new ExtendedProperties();
	    props.put("input.encoding", "utf-8");
	    props.put("output.encoding", "utf-8");
		props.put("resource.loader", "myloader") ;

		RepresentationResourceLoader loader = new RepresentationResourceLoader(new StringRepresentation("")) ;
		loader.init(props) ;
		
		Map<String, Representation> store = loader.getStore() ;
		store.put("hello", new StringRepresentation(tbody, MediaType.TEXT_HTML, Language.DEFAULT, CharacterSet.valueOf("utf-8"))) ;

		Debug.debug(loader.isCachingOn(), loader.resourceExists("hello")) ;

		TemplateRepresentation tr = new TemplateRepresentation("hello", getDataModel(), MediaType.valueOf("text/html;charset=utf-8")) ;
		Debug.debug(tr.getText()) ;
	}
	
	public void testLocalFileLoader() throws Exception {
		Properties props = new Properties();
	    props.put("input.encoding", "utf-8");
	    props.put("output.encoding", "utf-8");
	    props.put("file.resource.loader.cache", "true");
	    props.put("file.resource.loader.description", "Velocity File Resource Loader");
	    props.put("file.resource.loader.class", " org.apache.velocity.runtime.resource.loader.FileResourceLoader");
	    props.put("file.resource.loader.modificationCheckInterval", "10");
	    props.put("file.resource.loader.path", "./test/net/ion/radon/impl/let/template, /template");

	    Velocity.init(props);
	    

	    Template template = Velocity.getTemplate("HelloWorld.vm");
	    VelocityContext context = new VelocityContext();
	    context.put("param", JsonObject.fromObject(parameter)) ;

	    Writer writer = new StringWriter();
	    template.merge(context, writer);

	    System.out.println(writer.toString());
	}
	
	
	
	public void testStringLoader() throws Exception {
//		initVelocity() ;

		Debug.debug(Velocity.getProperty("file.resource.loader.path")) ;
		
	    StringResourceRepository repo = StringResourceLoader.getRepository("simple");
	    if (repo == null) throw new IllegalStateException("not initialized velocity. confirm filter") ;
	    String templateId = new ObjectId().toString() ;
	    repo.putStringResource(templateId, tbody) ;
	    
		TemplateRepresentation tr = new TemplateRepresentation(Velocity.getTemplate(templateId), getDataModel(), MediaType.TEXT_ALL) ;
		Debug.line(templateId, tr.getText());

//		TemplateRepresentation tr2 = new TemplateRepresentation(Velocity.getTemplate("HelloWorld.vm"), getDataModel(), MediaType.TEXT_ALL) ;
//		Debug.line(tr2.getTemplate().getEncoding(), tr2.getText());

	}
	
}
