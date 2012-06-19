 package net.ion.radon.impl.let.mongo.icss.test;

import java.util.Comparator;

import net.ion.radon.repository.Node;

public class NodePropertyComparator implements Comparator<Node>{

	private String[] keys;
	public NodePropertyComparator(String... key) {
		this.keys = key;
	}
	
	public int compare(Node o1, Node o2) {
		//typeº°....
		for(String k : keys){
			if(!o1.get(k).equals(o2.get(k)))
				return o1.getString(k).compareTo(o2.getString(k));
		}
		return 0;
	}
}
