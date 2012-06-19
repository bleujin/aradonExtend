package net.ion.radon.impl.let.system;

import java.io.IOException;

import net.ion.radon.core.let.DefaultLet;
import net.ion.radon.param.request.AradonRequest;
import net.ion.radon.param.request.RequestParser;

import org.apache.commons.fileupload.FileUploadException;
import org.restlet.representation.Representation;

public class Lets extends DefaultLet {

	protected Representation myPost(Representation entity) throws IOException, FileUploadException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		String param = getInnerRequest().getParameter(ARADON_PARAMETER) ;
		AradonRequest ars = RequestParser.parse(param) ;
		
		return ars.handle(this) ;
	}
	
}
