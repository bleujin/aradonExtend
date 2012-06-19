package net.ion.radon.impl.let.core;

import java.util.Collection;
import java.util.Map;

import net.ion.framework.util.MapUtil;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.ISerialRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.repository.NodeEvent;

public class ClientSession {

	private AradonClient client;
	private String prepath;
	private static ThreadLocal<ClientSession> CURRENT = new ThreadLocal<ClientSession>();

	private Map<String, DataNode> modified = MapUtil.newMap() ;

	private ClientSession(Aradon aradon, String prepath) {
		client = AradonClientFactory.create(aradon);
		this.prepath = prepath;
	}

	public static ClientSession create(Aradon aradon, String prepath) {
		final ClientSession result = new ClientSession(aradon, prepath);
		CURRENT.set(result);
		return result;
	}

	public DataNode getRootNode() {
		return DataNode.ROOT;
	}

	public DataNode get(String path) {
		ISerialRequest request = client.createSerialRequest(getAbsolutePath(path));
		return request.get(WrapNode.class).getDataNode();
	}

	private String getAbsolutePath(String path) {
		return path.startsWith("/") ? prepath + path.substring(1) : prepath + path;
	}

	public static ClientSession getCurrent() {
		return CURRENT.get();
	}

	public ISerialRequest createRequest(String path) {
		return client.createSerialRequest(getAbsolutePath(path));
	}

	public int save() {

		int index = modified.size();
		for (DataNode dnode : modified.values()) {
			dnode.save();
		}
		return index;

	}

	void notify(DataNode target, NodeEvent event) {
		modified.put(target.getPath(), target);
	}

	Collection<DataNode> getModifiedNode() {
		return modified.values();
	}

}
