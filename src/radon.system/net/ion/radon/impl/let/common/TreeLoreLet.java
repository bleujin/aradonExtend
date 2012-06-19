package net.ion.radon.impl.let.common;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.StringUtil;
import net.ion.radon.impl.let.system.MongoDefaultLet;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class TreeLoreLet extends MongoDefaultLet {

	private final static String SELF = "self" ;
	private final static String CHILD = "child" ;
	private final static String REL = "rel" ;
	
	public TreeLoreLet() {
		super(MediaType.TEXT_ALL);
	}

	@Override
	protected Representation myGet() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ResourceException {

		if (isSelfTarget()) {
			Node target = findSelfNode();
			return toRepresentation(target);
		} else if (isChildTarget()){
			Node target = findSelfNode();

			List<Map<String, ?>> children = target.getChild().toMapList(getInnerRequest().getAradonPage());
			return toMyRepresentation(children);
		}
		
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, getTarget());
	}

	@Override
	protected Representation myDelete() throws IOException, ResourceException,  ClassNotFoundException, InstantiationException, IllegalAccessException {

		if (isSelfTarget()) {
			Node target = findSelfNode() ;
			
			if (target.getChild().count() > 0 ){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "child node exist");
			}
			getSession().remove(target) ;
			return toRepresentation(target) ;
		}
		
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, getTarget());
	}

	// update ;
	@Override
	protected Representation myPut(Representation entity) throws IOException, ResourceException {
		if (! isSelfTarget()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, getTarget());

		Node target = findSelfNode() ;
		target.putAll(getInnerRequest().getGeneralParameter()) ;
		getSession().commit() ;
		
		return toRepresentation(target) ;
	}

	
	// create ;
	@Override
	protected Representation myPost(Representation entity) throws IOException, ResourceException {
		if (! isSelfTarget()) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, getTarget());
		
		long count =  getAradonQuery().count();
		if (count > 0) {
			throw new ResourceException(Status.CLIENT_ERROR_CONFLICT, getPath());
		}
		
		
		final String parentPath = getParentPath();

		if ("/".equals(StringUtil.trim(parentPath))) {
			Node newNode = getSession().newNode() ;
			newNode.putAll(getInnerRequest().getGeneralParameter()) ;
			newNode.setAradonId(getPath(), getLastPathName());
			getSession().commit() ;
			return toRepresentation(newNode) ;
		} else {

			Node parent = getSession().createQuery().path(parentPath).findOne();
			if (parent == null) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, getPath());
			}

			Node child = parent.createChild(getLastPathName());
			child.putAll(PropertyFamily.createByMap(getInnerRequest().getGeneralParameter()).toMap());

			child.setAradonId(getPath(), getLastPathName());
			getSession().commit();

			return toRepresentation(child);
		}
	}
	
	
	private Session getSession(){
		return getSession("workspace") ;
	}
	
	private Node findSelfNode() {
		Node node = getAradonQuery().findOne();
		if (node == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getAradonQuery().toString());
		}
		return node;
	}

	private boolean isSelfTarget() {
		return SELF.equals(getTarget());
	}
	private boolean isChildTarget() {
		return CHILD.equals(getTarget());
	}

	private String[] getPaths() {
		return StringUtil.split(getPath(), "./");
	}

	private String getLastPathName() {
		String[] paths = getPaths();
		if (paths == null || paths.length == 0) return "" ;
		return paths[paths.length - 1];
	}

	private String getPath() {
		return getRequest().getResourceRef().getRemainingPart();
	}
 
	private String getParentPath() {
		final String[] paths = getPaths();
		return "/" + StringUtil.join(paths, "/", 0, paths.length - 1);
	}


	private SessionQuery getAradonQuery() {
		return getSession().createQuery().aradonGroupId(getPath(), getLastPathName());
	}


	private String getTarget(){
		return getInnerRequest().getAttribute("target") ;
	}

}
