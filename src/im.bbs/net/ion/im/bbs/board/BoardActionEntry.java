package net.ion.im.bbs.board;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ion.im.bbs.URLConfig;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.restlet.representation.InputRepresentation;

public class BoardActionEntry extends BaseActionEntry {

	public BoardActionEntry(URLConfig config, String userId, String password) {
		super(config, userId, password);
	}

	public ActionResponse updateArticle(String bbsId, String categoryId, String contentId, String subject, String memo) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mode", "modify"));
		params.add(new BasicNameValuePair("id", bbsId));
		params.add(new BasicNameValuePair("no", contentId));
		params.add(new BasicNameValuePair("subject", subject));
		params.add(new BasicNameValuePair("memo", memo));
		params.add(new BasicNameValuePair("category", categoryId));
		
		return handleAction(params, urlConfig.get("article_write"));
	}

	public ActionResponse addArticle(String bbsId, String categoryId, String subject, String memo, UploadFile[] uploadFiles) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mode", "write"));
		params.add(new BasicNameValuePair("id", bbsId));
		params.add(new BasicNameValuePair("subject", subject));
		params.add(new BasicNameValuePair("memo", memo));
		params.add(new BasicNameValuePair("category", categoryId));
		
		if(uploadFiles != null && uploadFiles.length > 0) {
			for(int i = 0; i < uploadFiles.length; i++) {
				String fullPathParam = String.format("file_name%s", i + 1);
				String fileNameParam = String.format("s_file_name%s", i + 1);
				UploadFile uploadFile = uploadFiles[i];
				
				params.add(new BasicNameValuePair(fullPathParam, uploadFile.getFullPath()));
				params.add(new BasicNameValuePair(fileNameParam, uploadFile.getFileName()));
			}
		}
		
		return handleAction(params, urlConfig.get("article_write"));
	}
	
	public ActionResponse deleteArticle(String bbsId, String contentId) throws ClientProtocolException, IOException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", bbsId));
		params.add(new BasicNameValuePair("no", contentId));
		
		return handleAction(params, urlConfig.get("article_delete"));
	}
	
	public InputRepresentation downloadFile(String bbsId, String contentId, String fileNum) throws IOException {
		return (InputRepresentation)handleDownload(bbsId, contentId, fileNum);
	}
}
