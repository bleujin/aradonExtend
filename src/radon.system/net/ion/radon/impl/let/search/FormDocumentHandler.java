package net.ion.radon.impl.let.search;

import java.io.IOException;

import net.ion.isearcher.common.MyDocument;
import net.ion.isearcher.events.CollectorEvent;
import net.ion.isearcher.indexer.handler.DocumentHandler;

public class FormDocumentHandler implements DocumentHandler{

	public MyDocument[] makeDocument(CollectorEvent _event) throws IOException {
		if (! (_event instanceof AradonContentEvent)) return new MyDocument[0] ;
		
		AradonContentEvent aevent = (AradonContentEvent)_event ;
		
		
		
		return null;
	}

}
