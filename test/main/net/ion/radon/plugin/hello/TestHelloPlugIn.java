package net.ion.radon.plugin.hello;

import java.io.File;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.Options;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.AradonServer;
import net.ion.radon.core.config.Configuration;

import org.restlet.Response;
import org.restlet.data.Method;
 
public class TestHelloPlugIn extends TestCase{
	
	public void testRun() throws Exception {
		Options options = new Options(new String[] { "-config:./resource/config/aradon-config.xml", "-port:9040" });
		AradonServer as = new AradonServer(options);

		Aradon aradon = as.getAradon() ;
		
		
		Debug.line(aradon.getConfig().sections().sections(), aradon.getChildren()) ;
		
		AradonClient ac = AradonClientFactory.create(aradon) ;
		Response res = ac.createRequest("/plugin.hello/hello").handle(Method.GET) ;
		assertEquals(200, res.getStatus().getCode()) ; 
		
		as.start() ;
		new InfinityThread().startNJoin() ;
	}
	
	
	public void testHomeDirSetting() throws Exception {
		Options options = new Options(new String[] { "-config:./resource/config/aradon-config.xml", "-port:9040" });
		AradonServer as = new AradonServer(options);

		Aradon aradon = as.getAradon() ;
		Configuration aconfig = aradon.getGlobalConfig();
		assertEquals("emanon", aconfig.server().id()) ;
		assertEquals(new File(".").getCanonicalPath(), System.getProperty("aradon.emanon.home.dir")) ;
		
		assertEquals(new File("./plugin/hello").getCanonicalPath(), System.getProperty("aradon.emanon[net.bleujin.sample.hello].home.dir")) ;
		
		
		File libDirFile = aradon.getGlobalConfig().plugin().findPlugInFile("net.bleujin.sample.hello", "lib") ;
		assertEquals(new File("./plugin/hello/lib").getCanonicalPath(), libDirFile.getCanonicalPath()) ;
	}
	
	
}
