package net.ion.radon.impl.let.extract;

import java.net.URL;

import net.ion.framework.util.HashFunction;
import net.ion.radon.core.let.AbstractLet;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class UrlExtractorLet extends AbstractLet{

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return notImpl(entity);
	}

	@Override
	protected Representation myDelete() throws Exception {
		return notImpl();
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		ExtractConfigLoader loader = ExtractHelper.getConfigLoader(getContext());
		if(loader== null){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, " Extract Config Information not found");
		}
		
		String urlString = (String) getInnerRequest().getGeneralParameter().get("url");
		URL url = new URL(urlString);
		
		return ExtractHelper.extractorFile(url.openStream(), loader.getConfigPath(), loader.getExePath(), String.valueOf(HashFunction.hashGeneral(urlString)));
		
	}

	@Override
	protected Representation myGet() throws Exception {
		return notImpl();
	}

}
