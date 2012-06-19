package net.ion.radon.impl.let.db;

import net.ion.framework.db.procedure.IUserProcedure;
import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.radon.core.EnumClass.IFormat;

import org.restlet.representation.Representation;

public interface IProcedureExecuteHandler {

	public abstract Representation toRepresentation(IUserProcedure upt, IFormat format, IRequest request, IResponse response) throws Exception;
//	public abstract Representation toRepresentation(IUserProcedureBatch upt, IFormat format, IRequest request, IResponse response) throws Exception;
//	public abstract Representation toRepresentation(IFormat format, IRequest request, IResponse response) throws Exception;

}
