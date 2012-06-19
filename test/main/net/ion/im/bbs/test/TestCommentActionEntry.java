package net.ion.im.bbs.test;

import net.ion.framework.util.Debug;
import net.ion.im.bbs.URLConfig;
import net.ion.im.bbs.URLConfigLoader;
import net.ion.im.bbs.board.ActionResponse;
import net.ion.im.bbs.board.CommentActionEntry;
import net.ion.radon.TestAradonExtend;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;

public class TestCommentActionEntry extends TestAradonExtend {
	private URLConfigLoader loader;
	private URLConfig config;

	private String userId = "airkjh";
	private String password = "kjh5660";
	
	private String configURL = "./plugin/im.bbs/plugin-im.xml";
	
	private CommentActionEntry entry = null;

	public void setUp() throws Exception {
		super.setUp();
		
		loader = new URLConfigLoader("./plugin/im.bbs/board-action-url.properties");
		config = loader.getConfig();
		
		this.entry = new CommentActionEntry(config, userId, password);
	}
	
	public void testAddComment() throws Exception {
		ActionResponse response = entry.addComment("free", "1128", "코멘트 테스트" + System.currentTimeMillis());
		
		if(!response.isSuccess()) {
			response.getResultMessage();
		}
		
		assertTrue(response.isSuccess());
	}
	
	public void testDeletComment() throws Exception {
		ActionResponse response = entry.deleteComment("free", "1128", "5778");
		
		if(!response.isSuccess()) {
			response.getResultMessage();
		}
		
		assertTrue(response.isSuccess());		
	}
	
	public void testCommentGet() throws Exception {
		//1062
		Request request = new Request(Method.GET, "riap://component/im/bbs/free/1062/comment");
		Response response = handle(configURL, request);

		Debug.debug(response.getEntityAsText());
	}
}
