package org.jminix.server;

import java.util.List;

import javax.management.MBeanServerConnection;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;

public abstract class AbstractListServerConnectionProvider implements ServerConnectionProvider {

	public List<String> getConnectionKeys() {
		List<String> result = ListUtil.newList();
		for (int i : ListUtil.rangeNum(getConnections().size())) {
			result.add(Integer.toString(i));
		}
		return result;
	}

	public MBeanServerConnection getConnection(String name) {
		int i = NumberUtil.toInt(name, 0);
		return getConnections().get(i);
	}

}
