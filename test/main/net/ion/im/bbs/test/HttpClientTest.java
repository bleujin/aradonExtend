package net.ion.im.bbs.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.ion.framework.util.Debug;
import net.ion.im.bbs.URLConfig;
import net.ion.im.bbs.URLConfigLoader;
import net.ion.im.bbs.board.BoardActionEntry;
import net.ion.radon.TestAradonExtend;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.tika.io.IOUtils;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

public class HttpClientTest extends TestAradonExtend {

	private URLConfigLoader loader;
	private URLConfig config;

	private String userId = "airkjh";
	private String password = new String(Base64.encodeBase64("kjh5660".getBytes()));
	private String configPath = "./plugin/im.bbs/plugin-im.xml";
	
	public void setUp() throws Exception {
		super.setUp();
		
		loader = new URLConfigLoader("./plugin/im.bbs/board-action-url.properties");
		config = loader.getConfig();
	}
	
	public void testFileDownload_Entry() throws ClientProtocolException, IOException {
		String bbsId = "free";
		String contentId = "1178";
		String fileNum = "1";

		BoardActionEntry entry = new BoardActionEntry(config, userId, fileNum);
		entry.downloadFile(bbsId, contentId, fileNum);
	}
	
	public void testFileDownload_Internal() throws Exception {
		String userId = "airkjh";
		String password = new String(Base64.encodeBase64("kjh5660".getBytes()));
		
		String url = "riap://component/im/download/free/1180/1?auth=719ff54d45d7951ceb1e1ff796dae45f&aradon.result.format=json&userId=" + userId + "&password=" + password;
		Request request = new Request(Method.GET, url);
		
		Response response = super.handle(configPath, request);
		
		InputStream in = response.getEntity().getStream();
		OutputStream out = new FileOutputStream("c:/zeroboard/aaaa.jpg");
		
		IOUtils.copy(in, out);
		
		in.close();
		out.close();
	}
	
	public void testFileDownload_External() {
		Client client = new Client(Protocol.HTTP);
		
		String userId = "airkjh";
		String password = new String(Base64.encodeBase64("kjh5660".getBytes()));
		
		String url = "http://localhost:9002/im/download/free/1180/1?auth=719ff54d45d7951ceb1e1ff796dae45f&aradon.result.format=json&userId=" + userId + "&password=" + password;
		Request request = new Request(Method.GET, url);
		
		Response response = client.handle(request);
		Debug.debug(response.getEntity());
	}
}
