package net.ion.radon.impl.let.board;

import java.util.Map.Entry;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Page;
import net.ion.framework.db.procedure.IParameterQueryable;
import net.ion.framework.db.procedure.IQueryable;
import net.ion.framework.db.procedure.IUserProcedure;
import net.ion.framework.db.procedure.IUserProcedures;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;


public class ToQueryable {

	private final String QUERY = "query";
	private final String COMMAND = "command";
	private final String PARAM = "param";
	private final String PAGE = "page";
	private final String CTYPE = "ctype" ;
	private final String TYPE = "type" ;
	private final String LISTNUM = "listnum";
	private final String PAGENO = "pageno";

	private IDBController dc ;
	public ToQueryable(IDBController dc){
		this.dc = dc ;
	}
	
	public QueryObj parseToQueryable(JsonObject jo) {
		IQueryable cmd = parse(jo) ;
		
		int ctype = parseCommandType(jo.asJsonObject(QUERY).asString(CTYPE));
		return new QueryObj(cmd, ctype);
	}

	
	private IQueryable parse(JsonObject jo){
		IQueryable cmd = null ;
		Object type = jo.asJsonObject(QUERY).get(TYPE);
		if ("userprocedure".equalsIgnoreCase(String.valueOf(type))) {
			cmd = parseUserProcedure(jo);
		} else if ("userprocedures".equalsIgnoreCase(String.valueOf(type))){
			cmd = parseUserProcedures(jo) ;
		} else { // default
			cmd = parseParameterQuery(jo) ;
		}
		return cmd ;
	}
	
	private IQueryable parseUserProcedures(JsonObject jo) {
		
		Object name = jo.asJsonObject(QUERY).get("name") ;
		String uptsName = StringUtil.defaultIfEmpty(String.valueOf(name), "not_defined") ;
		IUserProcedures upts = dc.createUserProcedures(uptsName) ;
		
		
		JsonArray ja = jo.asJsonObject(QUERY).asJsonArray(COMMAND) ;
		for (int i = 0, last=ja.size(); i < last; i++) {
			upts.add(parse(ja.asJsonObject(i))) ;
		}
		
		return upts;
	}

	private IQueryable parseParameterQuery(JsonObject jo) {
		String query = jo.asJsonObject(QUERY).asString(COMMAND); // mandatory
		IParameterQueryable upt = dc.createParameterQuery(query);	
		setParam(upt, jo.asJsonObject(QUERY).asJsonObject(PARAM)) ;
		upt.setPage(parsePage(jo.asJsonObject(QUERY).asJsonObject(PAGE)));
		return upt;
	}

	private IQueryable parseUserProcedure(JsonObject jo) {
		String query = jo.asJsonObject(QUERY).asString(COMMAND); // mandatory
		IUserProcedure upt = dc.createUserProcedure(query) ;
		setParam(upt, jo.asJsonObject(QUERY).asJsonObject(PARAM)) ;
		upt.setPage(parsePage(jo.asJsonObject(QUERY).asJsonObject(PAGE)));
		return upt;
	}

	private void setParam(IParameterQueryable query, JsonObject jo) {
		for (Entry<String, Object> entry : jo.toMap().entrySet()) {
			if (entry.getValue().getClass().isArray()) {
				// TODO
				Debug.debug(entry.getValue().getClass().isArray(), entry.getValue(), entry.getValue().getClass()) ;
			} else {
				query.addParam(entry.getKey(), entry.getValue()) ;	
			}
		}
	}

	private int parseCommandType(Object type) {
		return (type != null && "dml".equalsIgnoreCase(type.toString())) ? IQueryable.UPDATE_COMMAND : IQueryable.QUERY_COMMAND ;
		
	}

	private Page parsePage(JsonObject pageObj) {
		if (pageObj == null || "null".equalsIgnoreCase(pageObj.toString())) return Page.TEN ;
		
		int listNum = ifNull(pageObj, LISTNUM, 10) ;
		int pageNo = ifNull(pageObj, PAGENO, 1) ;
		
		return Page.create(listNum, pageNo);
	}
	
	private int ifNull(JsonObject obj, String key, int defaultValue){
		return obj.get(key) == null ? defaultValue : obj.asInt(key) ;
	}
}
