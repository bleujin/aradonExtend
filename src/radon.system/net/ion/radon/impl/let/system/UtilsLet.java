package net.ion.radon.impl.let.system;

import java.util.Map;

import net.ion.framework.util.DateUtil;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class UtilsLet extends MongoDefaultLet{
	

	protected Representation myGet() throws Exception {
		
		final String umethod = getInnerRequest().getAttribute("method");
		if ("datetime".equalsIgnoreCase(umethod)){
			Node now = getSession().newNode() ;
			final String format = "yyyyMMdd-HHmmss";
			now.put("now", DateUtil.currentDateToString(format)) ;
			now.put("format", format) ;
			return toRepresentation(now);
		} else if ("unique".equalsIgnoreCase(umethod)){
			Node now = getSession().newNode() ;
			return toRepresentation(now);
		}
		throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "method:" + umethod) ;
	}

	public void register(Map<String, Object> result) {
		result.put("/utils/{method}/{format}", UtilsLet.class);
		result.put("/utils/{method}", UtilsLet.class);
	}
	
	private Session getSession() {
		return getSession("_date") ;
	}
}
