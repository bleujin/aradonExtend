package net.ion.radon.impl.let.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.Rows;
import net.ion.framework.db.ScreenInfo;
import net.ion.framework.db.bean.JSONRowProcessor;
import net.ion.framework.db.bean.handlers.MapListHandler;
import net.ion.framework.db.procedure.IQueryable;
import net.ion.framework.rest.IMapListRepresentationHandler;
import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.let.MapListRepresentationHandler;
import net.ion.radon.impl.let.db.Procedures.ExecType;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class QueryHandler {

	public static Representation toRepresentation(IQueryable query, String exectype, IMapListRepresentationHandler handler, TreeContext context)
			throws ResourceException, SQLException {
		return toRepresentation(query, ExecType.valueOf(exectype), handler, context);
	}

	public static Representation toRepresentation(IQueryable query, ExecType etype, IMapListRepresentationHandler handler, TreeContext context)
			throws ResourceException {
		try {
			if (etype.isQuery()) {
				Rows rows = query.execPageQuery();
				IRequest request = IRequest.create(MapUtil.create("page", query.getPage()));
				final ScreenInfo screenInfo = rows.getScreenInfo();
				IResponse response = IResponse.create(MapUtil.create("screen", screenInfo.toString()));
				List<Map<String, ?>> datas = (List<Map<String, ?>>) (rows.toHandle(new MapListHandler(new JSONRowProcessor())));

				return MapListRepresentationHandler.create(handler, request, datas, response, context).toRepresentation();
			} else {
				int count = query.execUpdate();
				IResponse response = IResponse.create(MapUtil.create("RowCount", (Object) count));
				return handler.toRepresentation(IRequest.EMPTY_REQUEST, ListUtil.EMPTY, response);
			}
		} catch (SQLException ex) {
			final Status status = ex.getErrorCode() == 1153 ?  Status.SERVER_ERROR_SERVICE_UNAVAILABLE : Status.SERVER_ERROR_INTERNAL;
			throw new ResourceException(status, ex);
		}
	}

}
