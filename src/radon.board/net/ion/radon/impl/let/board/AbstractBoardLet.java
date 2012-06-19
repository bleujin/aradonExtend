package net.ion.radon.impl.let.board;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.let.AbstractLet;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;

import org.apache.commons.lang.ArrayUtils;
import org.restlet.data.ClientInfo;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

import com.mongodb.MongoException;

public abstract class AbstractBoardLet extends AbstractLet {
	
	private static String [] keywords = new String[] {"subject", "boardid", "no"} ;
	private static final User ANONY_USER = new User("anony");

	private static RepositoryCentral CENTRAL = null ; 
	@Override
	public void doInit() {
		super.doInit() ;
		if (CENTRAL == null) {
			initRepository() ;
		}
	}

	protected void initRepository() {
		try {
			CENTRAL = RepositoryCentral.create("61.250.201.157", 27017) ;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		} ;
	}
	
	protected Session login(){
		return CENTRAL.testLogin("board") ;		
	}
	
	protected SessionQuery createQuery(){
		return login().createQuery() ;
	}
	
	protected Session login(String workspaceName){
		return CENTRAL.testLogin(workspaceName) ;		
	}
	
	protected Node findBoard(){
		Session session = login();
		Node bnode = session.createQuery().path("/" + getBoardId()).findOne() ;
		if (bnode == null){
			bnode = session.newNode(getBoardId()) ;
		}
		return bnode;
	}
	
	protected Node findArticle(){
		Session session = login();
		return session.createQuery().path("/" + getBoardId() + "/" + getArticleNo()).findOne() ;
	}
	

	protected PropertyFamily makeFormToProperty() {
		final Map<String, Object> form = getInnerRequest().getGeneralParameter() ;
		ClientInfo clientInfo = getInnerRequest().getClientInfo() ;
		PropertyFamily props = PropertyFamily.create();
		
		User user = ObjectUtil.coalesce(clientInfo.getUser(), ANONY_USER) ; 
		props.put("boardid", getBoardId());
		props.put("creuser", user.getIdentifier());
		props.put("credate", DateUtil.currentGMTToString()) ;
		props.put("subject", ObjectUtil.toString(form.get("subject"))) ;
		for (Entry<String, Object> param : form.entrySet()) {
			if (ArrayUtils.contains(keywords, param.getKey())) continue ;
			props.put(param.getKey(), ObjectUtil.toString(param.getValue())) ;
		}
		
		return props;
	}

	
	protected Representation toRepresentation(Node node){
		if(node == null){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
		return toRepresentation(IRequest.EMPTY_REQUEST, ListUtil.create(node.toPropertyMap()) , IResponse.EMPTY_RESPONSE);
	}
	
	public String getBoardId() {
		return getInnerRequest().getAttribute("boardid");
	}

	public String getArticleNo() {
		return getInnerRequest().getAttribute("no");
	}

	protected Representation myDelete() throws Exception {
		return notImpl();
	}

	protected String makePath(Object... param) {
		return  "/" + StringUtil.join(param, "/"); 
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return notImpl();
	}
}
