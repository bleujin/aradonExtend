package net.ion.radon.impl.let.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.let.DefaultLet;
import net.ion.radon.repository.IPropertyFamily;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.myapi.ICredential;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

import com.mongodb.DB;

public class MongoDefaultLet extends DefaultLet{

	public MongoDefaultLet() {
		super() ;
	}

	public MongoDefaultLet(MediaType mediaType) {
		super(mediaType) ;
	}

	protected Representation toRepresentationByNode(List<Node> datas) throws ResourceException {
		List<Map<String, ?>> data = new ArrayList<Map<String, ?>>();
		for (Node node : datas) {
			data.add(node.toMap());
		}
		return toMyRepresentation(data);
	}

	protected Representation toRepresentation(IPropertyFamily obj) throws ResourceException {
		return obj == null ? EMPTY_REPRESENTATION : toMyRepresentation( (List<Map<String, ?>>)((Object)ListUtil.toList(obj.toMap() )) );
	}
	
	protected Representation toMyRepresentation(List<Map<String, ? extends Object>> data) throws ResourceException {
		return toMyRepresentation(IRequest.EMPTY_REQUEST, data, IResponse.EMPTY_RESPONSE);
	}

	protected Representation toMyRepresentation(IRequest request, List<Map<String, ? extends Object>> data, IResponse response) throws ResourceException {
		List<Map<String, ?>> result = ListUtil.newList() ;
		for (Map<String, ?> map : data) {
			Map<String, Object> store = new HashMap<String, Object>(map) ;
			result.add(store) ;
		}
		
		return toRepresentation(request, result, response);	
	}

	
	protected Representation toRepresentation(NodeScreen screen) throws ResourceException {
		IRequest request = IRequest.create(MapUtil.create("page", getInnerRequest().getAradonPage().toString()));
		IResponse response = IResponse.create(MapUtil.create("totalCount", (Object) screen.getScreenSize()));
		List<Map<String, ?>> datas = screen.getPageMap();

		return toMyRepresentation(request, datas, response);
	}

	

	protected Session getSession(String wname) {
		Session session = getContext().getAttributeObject(MongoEntry.MONGO_ID, MongoEntry.class).login(getRequest()) ;
		session.changeWorkspace(getInnerRequest().getAttribute(wname)) ;
		return session ;
	}
	
}



class RequestCredential implements ICredential {

	private Request request;

	RequestCredential(Request request) {
		this.request = request;
	}

	public String getUniqueId() {
		return request.getCookies().getFirstValue("JSESSIONID") + "/" + request.getClientInfo().getAddress();
	}

	public boolean isAuthenticated(DB arg0) {
		return true;
	}

	public boolean isBlank() {
		return false;
	}
}
