package net.ion.radon.impl.let.db;

import java.lang.reflect.Field;

import net.ion.framework.db.procedure.IParameterQueryable;
import net.ion.framework.db.procedure.IUserProcedure;
import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.Debug;
import net.ion.heeya.DBTestCaseHeeya;
import net.ion.radon.core.EnumClass.IFormat;

import org.restlet.representation.Representation;

public class TestDB extends DBTestCaseHeeya{

	public void testParameter() throws Exception {
		
		IParameterQueryable upt = dc.createParameterQuery("Sample@editEmployeeWith(:empno,:ename,:job,:mgr,:hiredate,:sal,:comm,:deptno)") ;
		upt.addParameter("empno", 7369, java.sql.Types.INTEGER) ;
		upt.addParameter("ename", "dasom", java.sql.Types.VARCHAR) ;
		upt.addParameter("job", "job", java.sql.Types.VARCHAR) ;
		upt.addParameter("mgr", 7653, java.sql.Types.INTEGER) ;
		upt.addParameter("hiredate", "20101010", java.sql.Types.VARCHAR) ;
		upt.addParameter("sal", 100, java.sql.Types.INTEGER) ;
		upt.addParameter("comm", 200, java.sql.Types.INTEGER) ;
		upt.addParameter("deptno", 10, java.sql.Types.INTEGER) ;
		
		int result = upt.execUpdate();
//		Rows rows = upt.execQuery();
//		ResultSetHandler rsh = new JSONHandler();
//		JSONObject jroot = (JSONObject)rows.toHandle(rsh) ;
//		Debug.debug(jroot) ;
	}
	
	public void testUserProcedure() throws Exception {
		IUserProcedure upt = dc.createUserProcedure("Sample@editEmployeeWith(?,?,?,?,?,?,?,?)");
													 
		upt.addParam(0, 7369);
		upt.addParam(1, "heeya");
		upt.addParam(2, "job");
		upt.addParam(3, 7653);
		upt.addParam(4, "20101010");
		upt.addParam(5, 100);
		upt.addParam(6, 200);
		upt.addParam(7, 10);
		int result = upt.execUpdate();
		
		Debug.debug(upt.getProcFullSQL(), upt.getProcName(), upt.getProcSQL(), upt.getStatement());
	}
	
	public void testType() throws Exception {
		Field[] fields = java.sql.Types.class.getFields() ;
		for (Field field : fields) {
			Debug.debug(field.getName(), field.getInt(null)) ;
		}
	}
	
	//IProcedureExecuteHandler
	//ProcedureExecuteQueryHandler
	//ProcedureExecuteUpdateHandler
	public void testQueryRepresentation() throws Exception {
		String resultClass = "net.ion.radon.impl.let.db.ProcedureExecuteQUERYHandler";
		Class clz = Class.forName(resultClass);
		IProcedureExecuteHandler handler = (IProcedureExecuteHandler) clz.newInstance();
		
		IUserProcedure upt = dc.createUserProcedure("Sample@viewEmployeeBy(?)");
		upt.addParam(7369);
		
		Representation representation =  handler.toRepresentation(upt, IFormat.XML, IRequest.EMPTY_REQUEST, IResponse.EMPTY_RESPONSE);
		Debug.debug(representation.getText());
	}
	
	public void testUpdateRepresentation() throws Exception {
		String resultClass = "net.ion.radon.impl.let.db.ProcedureExecuteUPDATEHandler";
		Class clz = Class.forName(resultClass);
		IProcedureExecuteHandler handler = (IProcedureExecuteHandler) clz.newInstance();
		
		IUserProcedure upt = dc.createUserProcedure("Sample@editEmployeeWith(?,?,?,?,?,?,?,?)") ;
		upt.addParam(7369);
		upt.addParam("heeya1");
		upt.addParam("job");
		upt.addParam(7654);
		upt.addParam(20101010);
		upt.addParam(100);
		upt.addParam(200);
		upt.addParam(10);
		int result = upt.execUpdate();
		
		Representation representation =  handler.toRepresentation(upt, IFormat.XML, IRequest.EMPTY_REQUEST, IResponse.EMPTY_RESPONSE);
		Debug.debug(representation.getText());
	}
	

}
