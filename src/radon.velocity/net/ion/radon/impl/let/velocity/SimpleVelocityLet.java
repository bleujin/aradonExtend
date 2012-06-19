package net.ion.radon.impl.let.velocity;


import java.io.StringWriter;
import java.util.Map;

import net.ion.framework.util.StringUtil;
import net.ion.radon.core.let.AbstractLet;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class SimpleVelocityLet extends AbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		return notImpl();
	}

	@Override
	protected Representation myGet() throws Exception {
		return myPost(getRequest().getEntity()) ;
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		Map<String, Object> dataModel = VLetHelper.makeDataModel(getInnerRequest().getFormParameter());
		String template = getInnerRequest().getParameter(VLetHelper.getTemplateParamName(getContext())) ;

		if (StringUtil.isBlank(template)) throw new IllegalArgumentException("template is null") ;
		
		VelocityContext vcon = new VelocityContext(dataModel) ;
		StringWriter w = new StringWriter() ;
		Velocity.evaluate(vcon, w, "log", StringUtil.toString(template)) ;
		
		return new StringRepresentation(w.getBuffer(), VLetHelper.getMediaType(getContext())) ;  
//		new TemplateRepresentation(Velocity.getTemplate(templateId), dataModel, VLetHelper.getMediaType(getContext()));
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return notImpl(entity);
	}

}
