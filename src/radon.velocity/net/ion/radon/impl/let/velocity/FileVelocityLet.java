package net.ion.radon.impl.let.velocity;


import net.ion.radon.core.let.AbstractLet;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.restlet.data.Status;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class FileVelocityLet extends AbstractLet {

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

		Template template = getTemplate() ;
		return new TemplateRepresentation(template, VLetHelper.makeDataModel(getInnerRequest().getFormParameter()), VLetHelper.getMediaType(getContext())) ;
	}


	private Template getTemplate() {
		String uid = getInnerRequest().getAttribute("uid") ;
		String suffix = getContext().getAttributeObject("template.file.suffix", ".vm", String.class) ;
		final String resourceName = uid + suffix;
		if (Velocity.resourceExists(resourceName)){
			return Velocity.getTemplate(resourceName) ;
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "resource:" + resourceName) ;
		}
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return notImpl();
	}

}
