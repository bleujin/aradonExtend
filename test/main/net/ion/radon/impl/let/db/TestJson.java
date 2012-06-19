package net.ion.radon.impl.let.db;

import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;

public class TestJson extends TestCase{


	public void testSQLDate() throws Exception {
		Map row = MapUtil.create("mydate", new java.util.Date().getTime()) ;
		JsonParser.fromObject(ListUtil.toList(row)) ;
		
		
	}
}
