package net.ion.radon.impl.let.core;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.db.Row;
import net.ion.framework.db.Rows;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeEvent;
import net.ion.radon.repository.NodeResult;

public class DataNode implements Serializable {

	private final static String PATH = "path" ;  
	private final static String NAME = "name" ;  
	private final static String ATTRIBUTE_PREFIX = "attribute/" ;
	public static final DataNode ROOT = new DataNode("", "/");  
	
	private String parentPath;
	private String name;
	private Map<String, Object> attributes = MapUtil.newMap();
	
	private DataNode(String parentPath, String name) {
		this.parentPath = parentPath ;
		this.name = name;
	}
	
	public DataNode createChild(String name) {
		final DataNode result = new DataNode(getPath(), name);
		ClientSession.getCurrent().notify(result, NodeEvent.CREATE) ;
		return result ;
	}
	
	public boolean equals(Object obj){
		if (! (obj instanceof DataNode)) return false ;
		
		DataNode that = (DataNode)obj ;
		return this.getPath().equals(that.getPath()) ;
	}
	
	public int hashCode(){
		return getPath().hashCode() ;
	}
	
	public String getPath() {
		if (this == ROOT) return "/" ;
		
		return parentPath.endsWith("/") ? (parentPath + name) : (parentPath + "/" + name);
	}
	
	public String getParentPath(){
		return parentPath;
	}
	
	public String getParentName(){
		if (this == ROOT) return "" ;
		return  "/".equals(getParentPath()) ? "/" : StringUtil.substringAfter(getParentPath(), "/") ;
	}
	
	
	public Map<String, Object> toMap(){
		Map<String, Object> result = MapUtil.newMap() ;
		result.put(PATH, getPath());
		result.put(NAME, name);
		for(Entry<String, Object> entry : attributes.entrySet()){
			result.put(ATTRIBUTE_PREFIX + entry.getKey(), entry.getValue()) ;
		}
		
		return result ;
	}


	public  Map<String, Object> getAttribute() {
		return Collections.unmodifiableMap(attributes);
	}
	
	public void addAttribute(String key, Serializable value){
		attributes.put(key, value) ;
		ClientSession.getCurrent().notify(this, NodeEvent.UPDATE) ;
	}

	public String toString(){
		return "path:" + this.getPath()  + ", attributes:" + attributes.size() ;
	}
	
	
	public NodeResult save() {
		ClientSession.getCurrent().save() ;
		WrapNode.create(this).save();
		
		return null ;
	}

	public List<DataNode> getChildren() {
		return getChildren(PageBean.ALL) ;
	}
	
	public List<DataNode> getChildren(PageBean page) {
		return WrapNode.create(this).getChildren(page);
	}
	
	public DataNode remove() {
		return WrapNode.create(this).remove();
	}
	
	public DataNode getFirstChild() {
		return getChildren().get(0);
	}

	
	static DataNode load(Row row)  {
		return makeDataNode(row.getString("path"), row.getString("name"), row.getString("attribute"));		
	}
	
	static DataNode load(Rows rows) throws SQLException {
		return makeDataNode(rows.getString("path"), rows.getString("name"), rows.getString("attribute"));
	}

	private static DataNode makeDataNode(String fullPath, String name, String attribute) {
		
		String parentPath = StringUtil.removeEnd(fullPath, "/"+name) ;
		if (StringUtil.isBlank(parentPath)) parentPath = "/" ;
		
		DataNode result = new DataNode(parentPath, name) ;
		JsonObject jso = JsonObject.fromString(attribute) ;
		
		result.attributes = jso.toMap() ;
		
		return result ;
	}

	public String getName() {
		return name;
	}

	
	public void append(String key, Serializable val) {
		addAttribute(key, val) ;
	}

	public void clearProp() {
		attributes.clear() ;
	}

}
