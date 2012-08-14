package org.jminix.console.resource;

import java.io.File;
import java.io.IOException;

import net.ion.radon.core.let.AbstractServerResource;

import org.apache.commons.io.FileUtils;
import org.jminix.console.radon.MyConstants;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

public class IndexHtmlLet extends AbstractServerResource{

	
	@Get
	public Representation readHTMLFile() throws IOException{
//		String filePath = StringUtil.defaultIfEmpty(getContext().getAttributeObject(IndexHtmlLet.class.getCanonicalName(), String.class), "src/radon.jmx/jminix/console/index.html") ;
//		File file = new File(filePath);
		File file = getAradon().getGlobalConfig().plugin().findPlugInFile(MyConstants.PLUGIN_ID, "jminix/console/index.html") ;
		String result = "not found file. confirm context attribute[" + IndexHtmlLet.class.getCanonicalName() + "]" ;
		if (file.exists()){
			result = FileUtils.readFileToString(file, "UTF-8") ;
		}
		
		StringRepresentation rep = new StringRepresentation(result) ;
		rep.setMediaType(MediaType.TEXT_HTML) ;
		
		return rep ;
	}
	
	@Post
	public Representation readPost() throws IOException{
		return readHTMLFile() ;
	}
}
