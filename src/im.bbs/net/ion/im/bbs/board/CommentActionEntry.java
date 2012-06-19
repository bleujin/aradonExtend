package net.ion.im.bbs.board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ion.im.bbs.URLConfig;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

public class CommentActionEntry extends BaseActionEntry {

	public CommentActionEntry(URLConfig urlConfig, String userId, String password) {
		super(urlConfig, userId, password);
	}

	public ActionResponse addComment(String bbsId, String contentId, String memo) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", bbsId));
		params.add(new BasicNameValuePair("no", contentId));
		params.add(new BasicNameValuePair("memo", memo));
		
		return handleAction(params, urlConfig.get("comment_write"));
	}
	
	public ActionResponse deleteComment(String bbsId, String contentId, String commentId) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", bbsId));
		params.add(new BasicNameValuePair("no", contentId));
		params.add(new BasicNameValuePair("c_no", commentId));
		
		return handleAction(params, urlConfig.get("comment_delete"));
	}
}
