package net.ion.im.bbs.test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.im.bbs.URLConfig;
import net.ion.im.bbs.URLConfigLoader;
import net.ion.im.bbs.board.ActionResponse;
import net.ion.im.bbs.board.BoardActionEntry;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.client.HttpMultipartEntity;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CharacterSet;
import org.restlet.data.Method;
import org.restlet.representation.InputRepresentation;


public class TestBoardActionEntry extends TestAradonExtend {

	private URLConfigLoader loader;
	private URLConfig config;

	private String userId = "airkjh";
	private String password = new String(Base64.encodeBase64("kjh5660".getBytes()));
	
	public void setUp() throws Exception {
		super.setUp();
		
		loader = new URLConfigLoader("./plugin/im.bbs/board-action-url.properties");
		config = loader.getConfig();
	}

	public void testErrorMessageParse() {
		String message = "hahahahahaha<!-- error : error_message -->asdfasdfasd";

		BoardActionEntry entry = new BoardActionEntry(config, userId, password);
		String actual = entry.findErrorMessage(message);

		assertEquals("error_message", actual);
	}

	public void testAddArticle() throws SQLException, ClientProtocolException, IOException {
		BoardActionEntry entry = new BoardActionEntry(config, userId, password);

		String bbsId = "iculture";
		String categoryId = "1";
		String subject = "test case subject";
		String content = "test case contents_" + System.currentTimeMillis();

		ActionResponse result = entry.addArticle(bbsId, categoryId, subject, content, null);

		assertTrue(result.isSuccess());
	}

	public void testAddArticle_withEucKR() throws SQLException, ClientProtocolException, IOException {
		BoardActionEntry entry = new BoardActionEntry(config, userId, password);

		String bbsId = "free";
		String categoryId = "1";
		String subject = "하하하";
		String content = "호호호_" + System.currentTimeMillis();

		ActionResponse result = entry.addArticle(bbsId, categoryId, subject, content, null);

		assertTrue(result.isSuccess());
	}
	
	public void testAddArticle_withFloatSubject() throws SQLException, ClientProtocolException, IOException {
		BoardActionEntry entry = new BoardActionEntry(config, userId, password);

		String bbsId = "free";
		String categoryId = "1";
		String subject = "3.3";
		String content = "호호호_" + System.currentTimeMillis();

		ActionResponse result = entry.addArticle(bbsId, categoryId, subject, content, null);

		assertTrue(result.isSuccess());
	}	
	
	public void testAddArticle_withFile() throws Exception {
		String bbsId = "free";
		String categoryId = "1";
		String subject = "하하하";
		String content = "호호호_" + System.currentTimeMillis();
		String fileName = "C:/Users/airkjh/Pictures/1294792561_tvibocXD_withyou.jpg";
		
		File uploadFile = new File(fileName);
		ContentBody fileBody = new FileBody(uploadFile, "application/octet-stream");
		
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("userId", new StringBody(userId));
		entity.addPart("password", new StringBody(new String(Base64.encodeBase64(password.getBytes()))));
		entity.addPart("userFile", fileBody);
		entity.addPart("bbsId", new StringBody(bbsId));
		entity.addPart("categoryId", new StringBody(categoryId));
		entity.addPart("subject", new StringBody(subject));
		entity.addPart("memo", new StringBody(content));
		
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://localhost:9002/im/bbs/free");
		post.setEntity(entity);

		HttpResponse response = client.execute(post);

		Debug.debug(EntityUtils.toString(response.getEntity(), "euc-kr"));
	}
	
	public void testAddArticle_withFile_Internal() throws Exception {
		String url = "riap://component/im/bbs/free";
		String bbsId = "free";
		String categoryId = "1";
		String subject = "하하하";
		String content = "호호호_" + System.currentTimeMillis();
		String fileName = "C:/Users/airkjh/Pictures/1294792561_tvibocXD_withyou.jpg";
		
		Request request = new Request(Method.POST, url);

		final HttpMultipartEntity mre = new HttpMultipartEntity();
		mre.addParameter("userId", userId) ;
		mre.addParameter("password",  new String(Base64.encodeBase64(password.getBytes()))) ;
		mre.addParameter("bbsId",  bbsId) ;
		mre.addParameter("category",  categoryId) ;
		mre.addParameter("subject",  subject, CharacterSet.UTF_8) ;
		mre.addParameter("subject",  content, CharacterSet.UTF_8) ;
		mre.addParameter("userFile", new File(fileName)) ;
		
		
		request.setEntity(mre.makeRepresentation()) ;
		Response response = handle("resource/config/plugin-im.xml", request);
		
		Debug.debug(response.getEntityAsText());
	}	

	public void testSimplePost() throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost("http://airkjh.i-on.net/zeroboard/test2.php");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("subject", "하하하 "));

		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "euc-kr");
		post.setEntity(entity);
		
		HttpResponse response = client.execute(post);

		Debug.debug(EntityUtils.toString(response.getEntity(), "euc-kr"));
	}

	public void testEditArticle() throws ClientProtocolException, IOException {
		BoardActionEntry entry = new BoardActionEntry(config, userId, password);

		String bbsId = "free";
		String categoryId = "1";
		String contentId = "1069";
		String subject = "test case subject";
		String content = String.format("%s_%s", "test case contents_" + System.currentTimeMillis(), "modified");

		ActionResponse result = entry.updateArticle(bbsId, categoryId, contentId, subject, content);

		assertTrue(result.isSuccess());
	}

	public void testDownloadFile_Internal() throws ClientProtocolException, IOException {
		BoardActionEntry entry = new BoardActionEntry(config, userId, password);

		String bbsId = "free";
		String contentId = "1071";
		String fileNum = "1";
		

		InputRepresentation in = entry.downloadFile(bbsId, contentId, fileNum);
		System.out.println(in.getSize());
	}
	
	public void testDownloadFile_External() throws Exception {
		String configURL = "./plugin/im.bbs/plugin-im.xml";
		String password = new String(Base64.encodeBase64("kjh5660".getBytes()));
		Request request = new Request(Method.GET, "riap://component/im/bbs/free/1178?auth=719ff54d45d7951ceb1e1ff796dae45f?userId=airkjh&password=" + password);
		Response response = handle(configURL, request);
		
		System.out.println(response.getEntity().getSize());
	}
}