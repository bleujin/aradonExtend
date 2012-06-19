package net.ion.radon.impl.let.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.ion.framework.util.ListUtil;
import net.ion.radon.core.PageBean;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;


//for MDL (insert, update, delete)
//
//<request>... request form param </request>
//<response><property name="rowcount">1</property></response> 
//<nodes></nodes> 
//
//
//
//for SELECT
//
//<request><subject></subject>... <ardon.page.listnum></ardon.page.listnum></request>
//<nodes><node><property...> </node>... </nodes>
//<response></response>


public class RamDataStore extends AbstractResource {
	
	
	private static Map<String, DataNode> STORE = null ;
	@Get
	public WrapNode myGet() throws Exception {
		WrapNode result = WrapNode.create(STORE.get(getPath())) ;
		return result ;
	}
	
	@Override
	protected void doFirstInit() {
		super.doFirstInit() ;
		STORE = new ConcurrentHashMap<String, DataNode>(new LinkedHashMap());
		
		
		
	}
	
	private String getPath() {
		return "/" + getRequest().getResourceRef().getRemainingPart();
	}

	
	@org.restlet.resource.List
	public List<DataNode> toList(WrapNode parent){
		
		PageBean page = parent.getExtra(PageBean.class) ;
		List<DataNode> result = ListUtil.newList();
		
		int index = -1 ;
		for (Entry<String, DataNode> entry : STORE.entrySet()) {
			if (isChildren(parent, entry)  ) {
				index++ ;
				if (index < page.getStartLoc()){
					continue ;
				}
				if (index >= page.getEndLoc()) {
					break ;
				}
				
				result.add(entry.getValue());
			}
			
		}
		return result;
	}

	private boolean isChildren(WrapNode parent, Entry<String, DataNode> entry) {
		
		return entry.getValue().getParentPath().equals(parent.getDataNode().getPath());
	}
	
	
	@Delete
	public WrapNode myDelete() throws Exception {
		WrapNode result = WrapNode.create(STORE.remove(getPath())) ;
		removeDecentant();
		
		return result ;
	}

	private void removeDecentant() {
		List<String> forRemove = ListUtil.newList();
		for (Entry<String, DataNode> entry : STORE.entrySet()) {
			if (entry.getKey().startsWith(getPath())) {
				forRemove.add(entry.getKey());
			}
		}
		
		for(String key : forRemove){
			STORE.remove(key);
		}
	}

	@Put
	public WrapNode myPut(WrapNode wnode) throws Exception {
		STORE.put(getPath(), wnode.getDataNode()) ;
		
		return wnode ;
	}

	@Post
	public WrapNode myPost(WrapNode wnode) throws Exception {
		STORE.put(getPath(), wnode.getDataNode()) ;
		return wnode ;
	}
	

}
