package net.ion.radon.impl.let.system;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class RepositoryLet extends MongoDefaultLet{

	
	public RepositoryLet() {
		super(MediaType.TEXT_ALL);
	}

	@Override
	protected Representation myGet() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ResourceException {
		
		String id = getInnerRequest().getAttribute("id");
		NodeCursor findRows  = null ;
		SessionQuery findQuery = createQuery().id(id);
		if (StringUtil.isNotBlank(id)) {
			findRows = findQuery.find() ;
		} else {
			findRows = getSession().createQuery().find() ;
		}

		if (findRows == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, findQuery.toString()) ;
		}
		List<Map<String, ?>> nodes = findRows.toMapList(getInnerRequest().getAradonPage()) ;
		
		return toMyRepresentation(nodes);
	}

	@Override
	protected Representation myDelete() throws IOException, ResourceException  {
		
		Node node = getIdEqualQuery().findOne(); 
		int result = getIdEqualQuery().remove();
		if (result == 0) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getIdEqualQuery().toString()) ;
		}
		return toRepresentation(node);
	}

	private SessionQuery getIdEqualQuery() {
		String id = getInnerRequest().getAttribute("id");
		return createQuery().id(id);
	}

	// update ;
	@Override
	protected Representation myPut(Representation entity) throws IOException, ResourceException  {
		final SessionQuery findQuery = getIdEqualQuery();
		long count =  findQuery.count();
		if (count == 0) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, findQuery.toString());
		} else {
			Map<String, Object> props = getInnerRequest().getFormParameter();
			findQuery.overwriteOne(props);
			
			return toRepresentation(ListUtil.create(props));
		}
	}

	// create ;
	@Override
	protected Representation myPost(Representation entity) throws IOException, ResourceException {
		final Node newRow = getSession().newNode() ;
		newRow.putAll(getInnerRequest().getFormParameter());
		getSession().commit() ;
		return toRepresentation(newRow) ;
	}

	private Session getSession() {
		return getSession("workspace") ;
	}

	private SessionQuery createQuery() {
		return getSession().createQuery() ;
	}
}
