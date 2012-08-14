package net.ion.radon.plugin.hello;

import java.io.IOException;
import java.net.InetAddress;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class HelloPlugInLet extends AbstractServerResource{

	@Get
	public Representation sayhello() throws IOException{
		JsonObject json = new JsonObject() ;
		json.put("greeting", "hello") ;
		
		json.put("home", getAradon().getGlobalConfig().plugin().findAradonFile(".").getCanonicalPath()) ;
		json.put("pluginHome", getAradon().getGlobalConfig().plugin().findPlugInFile("net.bleujin.sample.hello", ".")) ;
		json.put("param", getInnerRequest().getParameter("name")) ;
		json.put("hostAddress", InetAddress.getLocalHost().getHostAddress()) ;
		
		return new StringRepresentation(json.toString());
	}
	
	@Post
	public Representation hi() {
		return new StringRepresentation("hi !") ;
	}
}
