package org.jminix.console.application;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.parse.gson.JsonPrimitive;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.Options;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.AradonServer;
import net.ion.radon.core.config.ConnectorConfig;
import net.ion.radon.core.config.ConnectorConfiguration;
import net.ion.radon.core.config.XMLConfig;

import org.restlet.Response;
import org.restlet.data.Method;

public class TestThreadInfo extends TestCase {

	public void testRun() throws Exception {
		Options options = new Options(new String[] { "-config:./resource/config/aradon-config.xml", "-port:9040" });
		AradonServer as = new AradonServer(options);

		Aradon aradon = as.getAradon() ;
		
		
		Debug.line(aradon.getChildren()) ;
		
		AradonClient ac = AradonClientFactory.create(aradon) ;

		Response res = ac.createRequest("/plugin.jminix/js/dojotoolkit/dojox/grid/resources/Grid.css", "bleujin", "redf1").handle(Method.GET) ;
		assertEquals(200, res.getStatus().getCode()) ; 

		as.start() ;
		new InfinityThread().startNJoin() ;
	}
	
	public void testThreadInfo() throws Exception {

		Aradon aradon = Aradon.create() ;
		
		aradon.startServer(ConnectorConfiguration.makeSimpleHTTPConfig(9000));

		AradonClient ac = AradonClientFactory.create("http://127.0.0.1:9000");
		IAradonRequest request = ac.createRequest("/plugin.jminix/", "bleujin", "redf1");
		assertEquals(200, request.handle(Method.GET).getStatus().getCode()) ;
		
//		request = ac.createRequest("/plugin.jminix/js/dojotoolkit/dojo/dojo.js", "bleujin", "redf1");
//		assertEquals(200, request.handle(Method.GET).getStatus().getCode()) ;
		
		
		new InfinityThread().startNJoin() ;
		Debug.line(request.get().getText());
		ac.stop(); 
		aradon.stop();
		// System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(dump).toString()) ;
	}

	
	public void testDump() throws Exception {

		JsonArray dump = new JsonArray();

		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threads = threadMXBean.getThreadInfo(threadMXBean.getAllThreadIds(), 15);
		for (ThreadInfo tinfo : threads) {

			JsonObject tinfoJson = JsonParser.fromObject(tinfo).getAsJsonObject();
			if (tinfo.getLockOwnerId() > -1) {
				tinfoJson.put("lockname", tinfo.getLockName());
				tinfoJson.put("LockOwnerName", tinfo.getLockOwnerName());
			}
			JsonArray starray = new JsonArray();
			for (StackTraceElement strace : tinfo.getStackTrace()) {
				String trString;
				if (strace.getLineNumber() < 0) {
					trString = "at " + strace.getClassName() + "." + strace.getMethodName();
				} else {
					trString = "at " + strace.getClassName() + "." + strace.getMethodName() + "(" + strace.getFileName() + ":" + strace.getLineNumber() + ")";
				}
				starray.add(new JsonPrimitive(trString));
			}
			tinfoJson.add("stackTrace", starray);

			dump.add(tinfoJson);
		}
	}
}
