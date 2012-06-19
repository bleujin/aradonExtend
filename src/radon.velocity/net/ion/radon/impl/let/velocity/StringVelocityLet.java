package net.ion.radon.impl.let.velocity;


import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.impl.let.system.MongoDefaultLet;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResource;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.restlet.data.Status;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class StringVelocityLet extends MongoDefaultLet {

	@Override
	protected Representation myDelete() throws Exception {
		Node node = getIdEqualQuery().findOne();
		
		int result = getIdEqualQuery().remove();
		if (result == 0) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getIdEqualQuery().toString());
		}
		return toRepresentation(node);
	}

	@Override
	protected Representation myGet() throws Exception {
		return toRepresentation(findTemplateNode());
	}

	private Node findTemplateNode() {
		Node findRows = getIdEqualQuery().findOne();
		if (findRows == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getIdEqualQuery().toString());
		}
		return findRows;
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {

		StringResourceRepository repo = getResourceRepository();

		Node templateNode = findTemplateNode() ;
		Map<String, Object> dataModel = VLetHelper.makeDataModel(getInnerRequest().getFormParameter());
		
		final String templateId = templateNode.getIdentifier();
		if (! Velocity.resourceExists(templateId)){ // when not loaded
			repo.putStringResource(templateId, StringUtil.toString(templateNode.get(VLetHelper.getTemplateParamName(getContext()))));
		}

		
		// StringResourceLoader.clearRepositories() ;
		
		Template template = Velocity.getTemplate(templateId);
		

		Debug.debug(template.isSourceModified(), template.getResourceLoader().isCachingOn()) ;
		final TemplateRepresentation representation = new TemplateRepresentation(template, dataModel, VLetHelper.getMediaType(getContext()));
		
		return representation;
		
		
		
//		Context vcon = new VelocityContext(dataModel) ;
//		StringWriter w = new StringWriter() ;
//		Velocity.evaluate(vcon, w, "log", StringUtil.toString(templateNode.get(VLetHelper.getTemplateParamName(getContext())))) ;
//			
//		return new StringRepresentation(w.toString());
	}

	private StringResourceRepository getResourceRepository() {
		StringResourceRepository repo = StringResourceLoader.getRepository("string");
		
		if (repo == null)
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "not initialized velocity. confirm filter");
		return repo;
	}

	// create or update
	@Override
	protected Representation myPut(Representation entity) throws Exception {
		final SessionQuery findQuery = getIdEqualQuery();
		
		String templateBody = getInnerRequest().getParameter(VLetHelper.getTemplateParamName(getContext())) ;
		
		Node node = findQuery.findOne() ;
		if (node == null) {
			node = getSession().newNode();
			node.setAradonId(getGroupId(), getUId());
		}
		node.putAll(MapUtil.create("template", templateBody));
		getSession().commit() ;
		
		// refresh resource
		StringResource sr = getResourceRepository().getStringResource(node.getIdentifier()) ;
		if (sr == null){
			getResourceRepository().putStringResource(node.getIdentifier(), templateBody) ;
		} else {
			sr.setBody(templateBody) ;
		}
		
		//ResourceCacheImpl.getInstance().put(result.getIdentifier(), new Template()) ;

		return toRepresentation(node);
	}

	
	
	private Session getSession() {
		return getSession("_velocity") ;
	}
	
	private String getGroupId() {
		return getInnerRequest().getAttribute("groupid");
	}

	private String getUId() {
		return getInnerRequest().getAttribute("uid");
	}

	private PropertyQuery getGroupEqualQuery() {
		return PropertyQuery.createByAradon(getGroupId()) ;
	}
	
	private SessionQuery getIdEqualQuery() {
		return  getSession().createQuery().aradonGroupId(getGroupId(), getUId()) ;
	}


}
