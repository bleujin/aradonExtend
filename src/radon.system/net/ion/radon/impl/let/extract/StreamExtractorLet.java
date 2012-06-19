package net.ion.radon.impl.let.extract;

import net.ion.radon.core.let.AbstractLet;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class StreamExtractorLet extends AbstractLet{
	
	//public final static String APP_NAME = StreamExtractorLet.class.getCanonicalName();
	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return notImpl(entity);
	}

	@Override
	protected Representation myDelete() throws Exception {
		return notImpl();
	}

	@Override
	protected Representation myGet() throws Exception {
		return notImpl();
	}
	
	@Override
	protected Representation myPost(Representation entity) throws Exception {
		ExtractConfigLoader loader = ExtractHelper.getConfigLoader(getContext());
		if(loader== null){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, " Extract Config Information not found");
		}
		return ExtractHelper.extractorFile(getRequest().getEntity().getStream(), loader.getConfigPath(), loader.getExePath(), "url");
	}
	
	
	
	

}
