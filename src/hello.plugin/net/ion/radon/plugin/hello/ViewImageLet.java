package net.ion.radon.plugin.hello;

import java.io.File;

import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.data.MediaType;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class ViewImageLet extends AbstractServerResource{

	
	@Get
	public Representation viewImage(){
		String name = getInnerRequest().getAttribute("name") ;
		File imageFile = getAradon().getGlobalConfig().plugin().findPlugInFile("net.bleujin.sample.hello", "/public/img/" + name) ;
		return new FileRepresentation(imageFile, MediaType.IMAGE_JPEG) ;
	}
}
