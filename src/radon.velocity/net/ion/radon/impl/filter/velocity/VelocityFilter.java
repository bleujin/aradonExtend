package net.ion.radon.impl.filter.velocity;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import net.ion.framework.util.StringUtil;
import net.ion.radon.core.IService;
import net.ion.radon.core.RadonAttributeKey;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.config.Attribute;
import net.ion.radon.core.filter.IFilterResult;
import net.ion.radon.core.filter.IRadonFilter;
import net.ion.radon.core.let.ResultFormat;
import net.ion.radon.core.representation.CloneStringRepresentation;
import net.ion.radon.impl.let.velocity.RadonVelocityContext;
import net.ion.radon.impl.let.velocity.VelocityEntry;

import org.apache.velocity.context.Context;
import org.restlet.Request;
import org.restlet.Response;

public class VelocityFilter extends IRadonFilter implements RadonAttributeKey{

	
	private String entryId ;
	public VelocityFilter(String entryId){
		this.entryId = entryId ;
	}
	
	@Override
	public IFilterResult afterHandle(IService service, Request request, Response response) {
		
		ResultFormat rf = getInnerRequest(request).getResultFormat();
		String templateId = rf.getTemplateId() ;
		
		Attribute findAttribute = null ;
		if (StringUtil.isBlank(templateId)) {
			Map<String, Attribute> attrs = getAttributes() ;
			for (Attribute attr : attrs.values()) {
				if ("true".equals(attr.getValue("[@default]"))){
					findAttribute = attr ;
					break ;
				}
			}
			
		} else {
			findAttribute = getAttribute(templateId) ;
		}
		
		if (findAttribute == null) {
			return IFilterResult.CONTINUE_RESULT ;
		}
		
		TreeContext context = service.getServiceContext() ;
		Context vcon = RadonVelocityContext.create(request, context, response);
		VelocityEntry ventry = context.getAttributeObject(entryId, VelocityEntry.class) ;
		String resource = findAttribute.getValue("[@script-source]") ;
		
		StringWriter writer = new StringWriter() ;
		if (StringUtil.isNotBlank(resource)) {
			ventry.merge(vcon, resource, writer) ;
		} else if (StringUtil.isNotBlank(findAttribute.getElementValue())){
			StringReader reader = new StringReader(findAttribute.getElementValue()) ;
			ventry.evaluate(vcon, reader, writer) ;
		}
		response.setEntity(new CloneStringRepresentation(writer.getBuffer(), rf.getFormat().getMediaType())) ;
		return IFilterResult.CONTINUE_RESULT;
	}

	@Override
	public IFilterResult preHandle(IService service, Request request, Response response) {
		return afterHandle(service, request, response);
	}

	
}
