package net.ion.radon.param;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.impl.let.db.ProcedureGroup;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class TestConfigParser extends TestCase {
	
	String parameter = "{query:\"ion\", color:[\"red\",\"white\",\"blue\"], param:{query:3, siteid:\"scat\"}}";
	public void testParse() throws Exception {
		TestBean b = ConfigParser.parse(parameter, TestBean.class) ;
		Debug.debug(b) ;
	}
	
	
	public void testProcedureGroup() throws Exception {
		String str = IOUtils.toString(new FileInputStream(new File("./plugin/radon.system.rdb/dbconfig.json")), "UTF-8");
		ProcedureGroup pg = ConfigParser.parse(str, ProcedureGroup.class) ;
		
		Debug.debug(ToStringBuilder.reflectionToString(pg)) ;
		
		
	}

}
