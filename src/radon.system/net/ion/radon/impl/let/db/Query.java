package net.ion.radon.impl.let.db;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.procedure.IBatchQueryable;
import net.ion.framework.db.procedure.IParameterQueryable;
import net.ion.framework.util.ListUtil;

public class Query {
	
	private String sql;
	private String type;				//command, commandbatch, procedure, procedurebatch
	private List<Param> params;

	public String getSql() {
		return sql;
	}

	public void setSql(String name) {
		this.sql = name;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setParameter(Param[] params) {
		this.params = Arrays.asList(params);
	}

	public Param[] getParameter() {
		return params.toArray(new Param[0]);
	}
	
	
	private Map<String, Object> toMap(){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", sql);
		map.put("type", type);
		map.put("param", params);
		return map;
	}
	
	public String toString(){
		return toMap().toString() ; 
	}
	//procedure, command, commandbatch procedurebatch
	IParameterQueryable createParameterQuery(IDBController dc, Map<String, Object> uparams) {
		if(getType().endsWith("batch")){
			return createBatchQueryable(dc, uparams) ;
		}else{
			return createQueryable(dc, uparams);
		}
	}

	public IParameterQueryable createBatchQueryable(IDBController dc, Map<String, Object> uparams) {
		IBatchQueryable upt = dc.createBatchParameterQuery(getSql()) ;
		for(Param param : getParameter()){
			Object object =  uparams.get(param.getName());
			upt.addBatchParameter(param.getName(), ListUtil.toArray(object) , ProcedureHelper.getType(param.getType()));
		}
		return upt;
	}
	
	private IParameterQueryable createQueryable(IDBController dc, Map<String, Object> userParams){
		IParameterQueryable  query = dc.createParameterQuery(getSql());
		for (Param param : getParameter()) {
			query.addParameter(param.getName(), userParams.get(param.getName()), ProcedureHelper.getType(param.getType()));
		}
		return query;
	}
}
