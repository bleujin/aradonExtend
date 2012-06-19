package net.ion.radon.impl.let.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.rest.IRequest;
import net.ion.framework.rest.IResponse;
import net.ion.framework.rest.JSONFormater;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.RandomUtil;

import org.restlet.representation.Representation;

public class TestIndexEntry extends TestCase{

	
	private String[] names = new String[]{"heor","bleu","jin"} ;
	public void testLoad() throws Exception {
		EngineEntry ie = new EngineEntry("c:/temp/itest") ;
		
		for (int i = 0; i < 10; i++) {
			Map<String, Object> values = createMapValue(i) ;
			ie.addIndex(values, "text.index", RandomUtil.nextRandomString(10)) ;
		}


		Thread.sleep(1000) ;
		
		
		
		
		
		Thread.sleep(100000) ;
	}

	private Map<String, Object> createMapValue(int i) {
		Map<String, Object> result = new HashMap<String, Object>() ;
		result.put("seq", i) ;
		result.put("name", names[RandomUtil.nextInt(2)]) ;
		
		return result;
	}
	
	
	public void testJSONFormater() throws Exception {
		Map<String, ?> info = new EngineEntry("c:/temp/itest").getInfo() ;
		final List<Map<String, ?>> datas = ListUtil.create(info);
		
		Representation r = new JSONFormater().toRepresentation(IRequest.EMPTY_REQUEST, datas, IResponse.EMPTY_RESPONSE) ;
		
		
	}
}
