package net.ion.radon.plugin.vfs;

import java.io.File;

import net.ion.framework.util.Debug;
import net.ion.framework.util.PathMaker;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

public class DirLet extends AbstractServerResource{


	@Get
	public Representation getFile() throws Exception {
		
		String remainingPart = getRequest().getResourceRef().getRemainingPart();
		if (remainingPart.startsWith("afield/")){
			remainingPart = StringUtil.replace(remainingPart, "afield/", "afieldfile/") ;
		} else if (remainingPart.startsWith("meta/")){
			remainingPart = StringUtil.replace(remainingPart, "meta/", "metafile/") ;
		}
		
		String filePath = PathMaker.getFilePath(getContext().getAttributeObject("base.dir", "./", String.class), remainingPart) ;
		Debug.line(filePath, remainingPart) ;
		File file = new File(filePath) ;

		if (! file.exists()) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getRequest().getResourceRef().getPath()) ; 

		MediaType mtype = getMetadataService().getMediaType(StringUtil.substringAfterLast(file.getName(), ".")) ;
		if (mtype == null) mtype = MediaType.ALL ; 
		
		final FileRepresentation result = new FileRepresentation(file, mtype);
		return result;
	}

	@Post
	public Representation getFileWithPost(Representation entity) throws Exception {
		return getFile();
	}
}
