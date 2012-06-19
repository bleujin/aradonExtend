package net.ion.radon.impl.let.system;

import java.util.List;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.Session;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class ReferenceLet extends MongoDefaultLet{
	
	protected Representation myGet() throws Exception {
		String from = getInnerRequest().getAttribute("from") ;
		String type = getInnerRequest().getAttribute("type") ;
		String to = getInnerRequest().getAttribute("to") ;
		
		
		if (StringUtil.isBlank(from)) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "empty reference") ;
		}
		MongoEntry entry = getContext().getAttributeObject(MongoEntry.MONGO_ID, MongoEntry.class) ;
		final Session session = entry.login(getRequest());
		
		
		List<Map<String, ?>> findRows = session.createQuery().id(from).findOne().relation(type).fetchs().toMapList(getInnerRequest().getAradonPage()) ;
		return toMyRepresentation(findRows) ;
	}
	
	protected Representation myPut(Representation entity) throws Exception {
		return myPost(entity) ;
	}

	protected Representation myPost(Representation entity) throws Exception {
		String from = getInnerRequest().getAttribute("from") ;
		String type = getInnerRequest().getAttribute("type") ;
		String to = getInnerRequest().getAttribute("to") ;
		
		if (StringUtil.isBlank(from) || StringUtil.isBlank(type) || StringUtil.isBlank(to)) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "illegal reference") ;
		}
		MongoEntry entry = getContext().getAttributeObject(MongoEntry.MONGO_ID, MongoEntry.class) ;
		final Session session = entry.login(getRequest());
		session.createQuery().id(from).findOne().toRelation(type, session.createQuery().id(to).findOne().selfRef()) ;
		session.commit() ;
		
		List<Map<String, ?>> findRows = session.createQuery().id(from).findOne().relation(type).fetchs().toMapList(getInnerRequest().getAradonPage()) ;
		return toRepresentation(findRows);
	}
	

	protected Representation myDelete() throws Exception {
		String from = getInnerRequest().getAttribute("from") ;
		String type = getInnerRequest().getAttribute("type") ;
		String to = getInnerRequest().getAttribute("to") ;
		
		if (StringUtil.isBlank(from)) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "illegal reference") ;
		}
		
		MongoEntry entry = getContext().getAttributeObject(MongoEntry.MONGO_ID, MongoEntry.class) ;
		final Session session = entry.login(getRequest());
		session.createQuery().id(from).findOne().relation(type).remove() ;
		session.commit() ;
		return toRepresentation(session.getAttribute(NodeResult.class.getCanonicalName(), NodeResult.class)) ;
	}

	
	private Representation toRepresentation(NodeResult nresult) throws ClassNotFoundException, InstantiationException, IllegalAccessException, ResourceException  {
		Map<String, Object> result =  MapUtil.newMap();
		result.put("errorMessage", nresult.getErrorMessage()) ;
		result.put("rowCount", nresult.getRowCount()) ;
		
		return toRepresentation(ListUtil.create(result));
	}

}
