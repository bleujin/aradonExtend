package net.ion.radon.impl.let.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.radon.client.ISerialRequest;
import net.ion.radon.core.PageBean;

public class WrapNode implements Serializable{

	private DataNode dnode ;
	private Map<String, Serializable> extra = MapUtil.newMap() ;
	
	private WrapNode(DataNode dnode){
		this.dnode = dnode ;
	}
	
	static WrapNode create(DataNode dnode) {
		return new WrapNode(dnode);
	}

	public List<DataNode> getChildren(PageBean page) {
		this.extra.put(PageBean.class.getCanonicalName(), page) ;
		return createRequest().post(this, ArrayList.class);
	}

	private ISerialRequest createRequest() {
		ClientSession client = ClientSession.getCurrent();
		return client.createRequest(this.dnode.getPath().substring(1)) ; // exclude '/'
	}

	DataNode getDataNode() {
		return dnode;
	}

	public <T> T getExtra(Class<? extends T> clz) {
		return (T) extra.get(clz.getCanonicalName());
	}

	public DataNode remove() {
		return createRequest().delete(WrapNode.class).getDataNode() ;
	}

	public void save() {
		DataNode self = createRequest().put(this, WrapNode.class).getDataNode() ;
	}
}
