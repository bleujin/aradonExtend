package net.ion.im.bbs.board;

import java.util.LinkedHashMap;
import java.util.Map;

public class Boards {
	private String id;
	private String name;
	private String uri;
	
	public Boards() {}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("id", id);
		map.put("name", name);
		map.put("uri", uri);
		
		return map;
	}
}
