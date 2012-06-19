package net.ion.im.bbs.board;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.im.bbs.URLConfig;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.restlet.data.MediaType;
import org.restlet.engine.util.Base64;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

public class BaseActionEntry {

	protected URLConfig urlConfig;
	protected String userId;
	protected String password;
	
	public BaseActionEntry(URLConfig urlConfig, String userId, String password) {
		this.urlConfig = urlConfig;
		this.userId = userId;
		this.password = password;
	}
	
	protected void authorizeAction(DefaultHttpClient client) throws ClientProtocolException, IOException {
		String password = new String(Base64.decode(this.password));
		
		client.getCredentialsProvider().setCredentials(new AuthScope(urlConfig.get("base"), 80), new UsernamePasswordCredentials(userId, password));
		HttpGet get = new HttpGet(urlConfig.get("auth"));
		HttpResponse response = client.execute(get);
		response.getEntity().consumeContent();
	}
	
	protected ActionResponse handleAction(List<NameValuePair> params, String actionUrl) throws ClientProtocolException, IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		
		try {
			authorizeAction(client);

			HttpPost post = new HttpPost(actionUrl);
			post.addHeader("Referer", urlConfig.get("index"));

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "euc-kr");
			post.setEntity(entity);
			
			HttpResponse response = client.execute(post);

			return getResult(EntityUtils.toString(response.getEntity(), "euc-kr"));			
		} finally {
			client.getConnectionManager().shutdown();
		}
	}
	
	protected Representation handleDownload(String bbsId, String contentId, String fileNum) throws IOException {
		DefaultHttpClient client = new DefaultHttpClient();
		
		try {
			authorizeAction(client);

//			HttpPost post = new HttpPost(urlConfig.get("file_download"));
			HttpPost post = new HttpPost("http://airkjh.i-on.net/zeroboard/download.php");
			post.addHeader("Referer", urlConfig.get("index"));
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", bbsId));
			params.add(new BasicNameValuePair("no", contentId));
			params.add(new BasicNameValuePair("filenum", fileNum));

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "euc-kr");
			post.setEntity(entity);
			
			HttpResponse response = client.execute(post);
			InputStream in = response.getEntity().getContent();
			String mediaType = response.getEntity().getContentType().getValue();
			
			return new InputRepresentation(in, MediaType.valueOf(mediaType));
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		
		return null;
	}
	
	private ActionResponse getResult(String responseBody) {
		Debug.debug(responseBody);

		ActionResponse result = new ActionResponse();
		String errorMessage = findErrorMessage(responseBody);

		if (errorMessage != null) {
			result.setSuccess(false);
			result.setResultMessage(errorMessage);
		}

		return result;
	}

	private final static String ERROR_MESSAGE_HEADER = "<!-- error : ";
	private final static String ERROR_MESSAGE_FOOTER = " -->";

	public String findErrorMessage(String responseBody) {
		// error message format : <!-- error : $message -->
		int headerIndex = responseBody.indexOf(ERROR_MESSAGE_HEADER);

		if (headerIndex > -1) {
			int footerIndex = responseBody.indexOf(ERROR_MESSAGE_FOOTER);
			return responseBody.substring(headerIndex + ERROR_MESSAGE_HEADER.length(), footerIndex);
		}

		return null;
	}	
}
