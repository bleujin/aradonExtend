package net.ion.radon.impl.let.search;

import net.ion.framework.util.Debug;
import net.ion.isearcher.indexer.collect.ICollector;
import net.ion.isearcher.indexer.handler.DocumentHandler;

public class AradonCollector implements ICollector{

	private static DocumentHandler handler = new FormDocumentHandler() ;
	private static String name = AradonCollector.class.getName() ;
	
	
	public void collect() {
		
	}

	public String getCollectName() {
		return name;
	}

	public DocumentHandler getDocumentHandler() {
		return handler;
	}

	public void setDocumentHandler(DocumentHandler documenthandler) {
		this.handler = documenthandler ;
	}

	public void shutdown(String message) {
		Debug.debug("called shutdown") ;
	}

}
