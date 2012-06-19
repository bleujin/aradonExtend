package net.ion.radon.impl.let.system;

import java.io.IOException;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class LoreLet extends MongoDefaultLet {

	public LoreLet() {
		super(MediaType.TEXT_ALL);
	}

	@Override
	protected Representation myGet() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ResourceException {

		String uid = getUId();
		NodeCursor findRows = null;
		if (StringUtil.isNotBlank(uid)) {
			findRows = getIdEqualQuery().find();
		} else {
			findRows =getGroupEqualQuery().find() ;
			if (StringUtil.isNotBlank(getSort())) findRows = findRows.sort(PropertyFamily.create(getSort(), -1));
		}

		if (findRows == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getIdEqualQuery().toString());
		}
		NodeScreen screen = findRows.screen(getInnerRequest().getAradonPage());

		return toRepresentation(screen);
	}

	@Override
	protected Representation myDelete() throws IOException, ResourceException  {
		
		Node node = getIdEqualQuery().findOne();
		int result = getIdEqualQuery().remove();
		if (result == 0) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getIdEqualQuery().toString());
		}
		return toRepresentation(node);
	}

	// update ;
	@Override
	protected Representation myPut(Representation entity) throws IOException, ResourceException  {
		long count = getIdEqualQuery().count();
		if (count == 0) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, getIdEqualQuery().toString());
		}
		Map<String, Object> props = getInnerRequest().getGeneralParameter();
		getIdEqualQuery().overwriteOne(props);

		return toMyRepresentation(ListUtil.create(props));
	}

	// create ;
	@Override
	protected Representation myPost(Representation entity) throws IOException, ResourceException  {
		long count = getIdEqualQuery().count();
		if (count > 0) {
			throw new ResourceException(Status.CLIENT_ERROR_CONFLICT,  getIdEqualQuery().toString());
		}
		
		final Node newRow = getSession().newNode() ;
		newRow.putAll(getInnerRequest().getGeneralParameter());
		newRow.setAradonId(getGroupId(), getUId());
		getSession().commit() ;
		return toRepresentation(newRow);
	}


	private Session getSession() {
		return getSession("workspace") ;
	}

	private String getGroupId() {
		return getInnerRequest().getAttribute("groupid");
	}

	private String getUId() {
		return getInnerRequest().getAttribute("uid");
	}

	private String getSort() {
		return getInnerRequest().getAttribute("sort");
	}

	private SessionQuery getIdEqualQuery() {
		return getSession().createQuery().aradonGroupId(getGroupId(), getUId()) ; 
	}

	private SessionQuery getGroupEqualQuery() {
		return getSession().createQuery().aradonGroup(getGroupId()) ;
	}
}
