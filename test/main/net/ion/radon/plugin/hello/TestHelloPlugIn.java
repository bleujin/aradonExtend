package net.ion.radon.plugin.hello;

import java.io.File;

import junit.framework.TestCase;
import net.ion.radon.Options;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.AradonConfig;
import net.ion.radon.core.AradonServer;
import net.ion.radon.core.config.Configuration;

import org.restlet.Response;
import org.restlet.data.Method;
 
public class TestHelloPlugIn extends TestCase{
	
	public void testRun() throws Exception {
		Options options = new Options(new String[] { "-config:./resource/config/aradon-config.xml", "-port:9040" });
		AradonServer as = new AradonServer(options);

		Aradon aradon = as.getAradon() ;
		AradonClient ac = AradonClientFactory.create(aradon) ;
		Response res = ac.createRequest("/sample/hello").handle(Method.GET) ;
		assertEquals(200, res.getStatus().getCode()) ; 
	}
	
	
	public void testHomeDirSetting() throws Exception {
		Options options = new Options(new String[] { "-config:./resource/config/aradon-config.xml", "-port:9040" });
		AradonServer as = new AradonServer(options);

		Aradon aradon = as.getAradon() ;
		Configuration aconfig = aradon.getGlobalConfig();
		assertEquals("mercury", aconfig.server().id()) ;
		assertEquals(new File(".").getCanonicalPath(), System.getProperty("aradon.mercury.home.dir")) ;
		
		assertEquals(new File("./plugin/hello").getCanonicalPath(), System.getProperty("aradon.mercury[net.bleujin.sample.hello].home.dir")) ;
		
		
		File libDirFile = aradon.getGlobalConfig().plugin().findPlugInFile("net.bleujin.sample.hello", "lib") ;
		assertEquals(new File("./plugin/hello/lib").getCanonicalPath(), libDirFile.getCanonicalPath()) ;
	}
	
	
}
