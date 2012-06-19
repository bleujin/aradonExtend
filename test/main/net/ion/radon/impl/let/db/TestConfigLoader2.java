package net.ion.radon.impl.let.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.rest.IResponse;
import net.ion.framework.util.Debug;
import net.ion.radon.param.MyParameter;

import org.apache.commons.io.IOUtils;

public class TestConfigLoader2 extends TestCase {

	private ProcedureGroup pg ;
	
	public void setUp() throws Exception {
		String filePath = "./resource/config/dbconfig.json";
		String str = IOUtils.toString(new FileInputStream(new File(filePath)));

		MyParameter mp = MyParameter.create(str);
		pg =  (ProcedureGroup) mp.toBean(ProcedureGroup.class);
	}
	
	public void testCommandCount() throws FileNotFoundException, IOException  {
		//assertEquals(2, pg.getProcedures().length) ;
	}
	

	public void testCommandGet() throws FileNotFoundException, IOException  {
		assertEquals("selectAll", pg.getProcedures()[0].getId()) ;
		assertEquals("query", pg.getProcedures()[0].getExecType()) ;

		assertEquals(1, pg.getProcedures()[0].getQuerylist().length) ;
		assertEquals("command", pg.getProcedures()[0].getQuerylist()[0].getType()) ;
	}
	
	public void testIResponse() throws Exception {
		Map<String, Object> pmap = new LinkedHashMap<String, Object>() ;
		for (Procedures proc: pg.getProcedures()) {
			pmap.put(proc.getId(), proc.toMap()) ;
		}
		Debug.debug(IResponse.create(pmap).toJSON()) ;
		Debug.debug(IResponse.create(pmap).toXML()) ;
		
	}
	

}
