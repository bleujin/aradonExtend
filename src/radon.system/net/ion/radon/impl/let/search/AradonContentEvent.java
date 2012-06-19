package net.ion.radon.impl.let.search;

import java.io.IOException;
import java.util.Map;

import net.ion.isearcher.crawler.util.HashFunction;
import net.ion.isearcher.events.CollectorEvent;
import net.ion.isearcher.indexer.collect.ICollector;

public class AradonContentEvent extends CollectorEvent{

	private String docId ;
	private Map<String, Object> params ;
	private AradonContentEvent(String docId, Map<String, Object> params) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ICollector getCollector() {
		return null;
	}

	public String getCollectorName() {
		return getCollector().getCollectName();
	}

	public long getEventBody() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getEventId() throws IOException {
		return HashFunction.hashGeneral(docId);
	}

	public static CollectorEvent create(String docId, Map<String, Object> params) {
		return new AradonContentEvent(docId, params);
	}

}
