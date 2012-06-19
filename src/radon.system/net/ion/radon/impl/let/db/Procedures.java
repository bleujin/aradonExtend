package net.ion.radon.impl.let.db;

import java.util.LinkedHashMap;
import java.util.Map;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.procedure.IQueryable;
import net.ion.framework.db.procedure.IUserProcedures;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Procedures {

	public static final String COMMAND_TYPE_QUERY = "query";
	public static final String COMMAND_TYPE_UPDATE = "update";

	private String id;
	private String exectype; // query, update
	
	public enum ExecType {
		query, update ; 
		
		public boolean isQuery(){
			return this == query ;
		}
	}
	
	private Query[] querys = new Query[0];

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getExecType(){
		return exectype;
	}
	
	public void setExecType(String exectype) {
		this.exectype = exectype;
	}

   
	public Query[] getQuerylist() {
		return querys;
	}

	public void setQuerylist(Query[] querys) {
		this.querys = querys;
	}

	public Map<String, Object> toMap(){
		Map<String , Object> map = new LinkedHashMap<String, Object>();
		map.put("id", id);
		map.put("execType", exectype);
		
		map.put("querylist", ToStringBuilder.reflectionToString(querys, ToStringStyle.SIMPLE_STYLE));
		return map;
	}

	public IQueryable createQuery(IDBController dc, Map<String, Object> uparams) {
		IUserProcedures upts = dc.createUserProcedures(getId());
		for(Query proc : getQuerylist()){
			upts.add(proc.createParameterQuery(dc, uparams));
		}
		return upts ;
	}

}
