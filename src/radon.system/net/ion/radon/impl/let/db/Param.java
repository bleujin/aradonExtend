package net.ion.radon.impl.let.db;

import java.util.LinkedHashMap;
import java.util.Map;

public class Param {

	private String name;
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private Map<String, Object> toMap(){
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("name", name);
		map.put("type", type);
		return map;
	}
	
	public String toString(){
		return toMap().toString() ;
	}
}
