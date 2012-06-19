package net.ion.radon.impl.let.search;

import java.io.IOException;
import java.util.Map;

import net.ion.framework.util.ListUtil;
import net.ion.radon.impl.let.system.MongoDefaultLet;
import net.ion.radon.repository.PropertyFamily;

import org.restlet.representation.Representation;

public class IndexLet extends MongoDefaultLet {

	public final static String ARADON_GROUP = "aradon.group" ;
	public final static String ARADON_UID = "aradon.uid" ;

	@Override 
	protected Representation myGet() throws Exception {
		return toRepresentation(ListUtil.create(getEngine().getInfo())) ;
	}

	@Override 
	protected Representation myPut(Representation entity) throws Exception {
		Map<String, Object> params = getInnerRequest().getGeneralParameter();
		getEngine().updateIndex(params, getGroupId(), getDocId()) ;
		
		return toRepresentation(PropertyFamily.createByMap(params));
	}
	
	
	// create
	@Override 
	protected Representation myPost(Representation entity) throws Exception {
		Map<String, Object> params = getInnerRequest().getGeneralParameter();
		getEngine().addIndex(params, getGroupId(), getDocId()) ;

		return toRepresentation(PropertyFamily.createByMap(params));
	}

	
	@Override 
	protected Representation myDelete() throws Exception {
		Map<String, Object> params = getInnerRequest().getGeneralParameter();
		getEngine().deleteIndex(params, getGroupId(), getDocId()) ;
		
		return toRepresentation(PropertyFamily.createByMap(params));
	}


	private EngineEntry getEngine() throws IOException {
		final String engineName = getContext().getAttributeObject("let.index.name", "aradon.search.engine", String.class);
		final EngineEntry engine = getContext().getAttributeObject(engineName, EngineEntry.class);
		if (engine == null) throw new IllegalStateException("index engine not found : " + engineName) ;
		return engine;
	}
	
	private String getDocId() {
		return getInnerRequest().getAttribute("docid");
	}

	private String getGroupId() {
		return getInnerRequest().getAttribute("groupid");
	}

}
