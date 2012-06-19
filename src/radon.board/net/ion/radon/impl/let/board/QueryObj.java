package net.ion.radon.impl.let.board;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import net.ion.framework.db.Page;
import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.handlers.JSONHandler;
import net.ion.framework.db.procedure.IParameterQueryable;
import net.ion.framework.db.procedure.IQueryable;
import net.ion.framework.db.procedure.ParameterQueryable;
import net.ion.framework.db.procedure.QueryType;
import net.ion.framework.db.procedure.UserProcedures;
import net.ion.framework.parse.gson.JsonObject;


public class QueryObj {

	private IQueryable query ; 
	private int commandType ;
	QueryObj(IQueryable query, int ctype){
		this.query = query ;
		this.commandType = ctype ;
	}
	
	public JsonObject execute() throws SQLException{
		if (commandType == IQueryable.QUERY_COMMAND){
			Rows rows = query.execQuery() ;
			return (JsonObject) rows.toHandle(new JSONHandler()); 
		} else {
			int rowcount = query.execUpdate() ;
			JsonObject body = new JsonObject() ;
			body.put("ROWCOUNT", rowcount) ;
			
			JsonObject result = new JsonObject() ;
			result.put("RESULT", body) ;
			return result ;
		}
	}

	public Page getPage() {
		return query.getPage();
	}

	public Object getParam(String paramName) {
		return getQuery().getNamedParam().get(paramName);
	}

	public QueryObj getQueryObj(int idx){
		return new QueryObj(getQuery(idx), this.commandType) ;
	}
	
	public Map getParams() {
		return Collections.unmodifiableMap(getQuery().getNamedParam());
	}

	public int getQueryType() {
		return getQuery().getQueryType() ;
	}

	public Object getCommandType() {
		return commandType;
	}
	
	private ParameterQueryable getQuery(){
		return getQuery(this.query, 0) ;
	}
	private IParameterQueryable getQuery(int idx){
		return getQuery(this.query, idx) ;
	}
	
	private ParameterQueryable getQuery(IQueryable upt, int i) {
		if (upt.getQueryType() == QueryType.USER_PROCEDURES){
			UserProcedures upts = (UserProcedures)upt ;
			return getQuery(upts.getQuery(i), i) ;
		} else return (ParameterQueryable)upt ;
	}

	public String getProcSQL() {
		return query.getProcSQL();
	}

	
}
