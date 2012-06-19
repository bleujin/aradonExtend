package net.ion.radon.impl.let.db;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.procedure.IQueryable;
import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.ListUtil;
import net.ion.radon.core.let.AbstractLet;

import org.restlet.representation.Representation;

public class ProcedureLet extends AbstractLet {

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return notImpl(entity);
	}

	@Override
	protected Representation myDelete() throws Exception {
		return notImpl();
	}
	
	@Override
	protected Representation myPost(Representation entity) throws Exception {
		
		Procedures procs = ProcedureHelper.getProcedures(getContext(), getInnerRequest().getAttribute("id"));
		IDBController dc = getDBController();
		
		IQueryable query = procs.createQuery(dc, getInnerRequest().getGeneralParameter()) ;
		query.setPage(getInnerRequest().getAradonPage().toPage()) ;
		
		return QueryHandler.toRepresentation(query, procs.getExecType(), newMapListFormatHandler(), getContext()) ;
	}

	private IDBController getDBController() {
		String db_attriId = getContext().getAttributeObject("connect.db.attribute.id", String.class);
		return ProcedureHelper.getDBController(getContext(), db_attriId);
	}

	@Override
	protected Representation myGet() throws Exception {
		Procedures procs = ProcedureHelper.getProcedures(getContext(), getInnerRequest().getAttribute("id"));
		return toRepresentation(IRequest.EMPTY_REQUEST, ListUtil.EMPTY, IResponse.create(procs.toMap()));
	}
}


