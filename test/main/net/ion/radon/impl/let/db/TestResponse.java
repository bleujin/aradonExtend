package net.ion.radon.impl.let.db;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.Debug;

public class TestResponse extends TestCase{

	public void testCreate() throws Exception {
		Map<String, Object> outer = new HashMap<String, Object>() ;
		Map<String, Object> inner = new HashMap<String, Object>() ;
		inner.put("x", 1) ;
		inner.put("y", 2) ;
		outer.put("loc", inner) ;
		
		
		IResponse res = IResponse.create(outer) ;
		Debug.debug(res.toJSON()) ;
		
	}
}
