package net.ion.im.bbs.meta;

import java.util.HashMap;
import java.util.Map;

public class Columns {

	private static Map<String, Boolean> BOARD_COLUMNS = new HashMap<String, Boolean>();
	private static Map<String, Boolean> COMMENT_COLUMNS = new HashMap<String, Boolean>();
	private static Map<String, Boolean> CATEGORY_COLUMNS = new HashMap<String, Boolean>();
	private static Map<String, Boolean> MEMBER_COLUMNS = new HashMap<String, Boolean>();
	
	static {
		BOARD_COLUMNS.put("headnum", Boolean.FALSE);
		BOARD_COLUMNS.put("homepage", Boolean.FALSE);
		BOARD_COLUMNS.put("reply_mail", Boolean.FALSE);
		BOARD_COLUMNS.put("division", Boolean.FALSE);
		BOARD_COLUMNS.put("next_no", Boolean.FALSE);
		BOARD_COLUMNS.put("download1", Boolean.FALSE);
		BOARD_COLUMNS.put("prev_no", Boolean.FALSE);
		BOARD_COLUMNS.put("sitelink2", Boolean.FALSE);
		BOARD_COLUMNS.put("ip", Boolean.FALSE);
		BOARD_COLUMNS.put("islevel", Boolean.FALSE);
		BOARD_COLUMNS.put("is_secret", Boolean.FALSE);
		BOARD_COLUMNS.put("password", Boolean.FALSE);
		BOARD_COLUMNS.put("vote", Boolean.FALSE);
		BOARD_COLUMNS.put("no", Boolean.FALSE);
		BOARD_COLUMNS.put("ismember", Boolean.FALSE);
		BOARD_COLUMNS.put("depth", Boolean.FALSE);
		BOARD_COLUMNS.put("category", Boolean.FALSE);
		BOARD_COLUMNS.put("father", Boolean.FALSE);
		BOARD_COLUMNS.put("sitelink1", Boolean.FALSE);
		BOARD_COLUMNS.put("child", Boolean.FALSE);
		BOARD_COLUMNS.put("use_html", Boolean.FALSE);
		BOARD_COLUMNS.put("download2", Boolean.FALSE);
		BOARD_COLUMNS.put("arrangenum", Boolean.FALSE);
		BOARD_COLUMNS.put("hit", Boolean.FALSE);
		BOARD_COLUMNS.put("total_comment", Boolean.FALSE);
		BOARD_COLUMNS.put("y", Boolean.FALSE);
		BOARD_COLUMNS.put("reg_date", Boolean.FALSE);
		BOARD_COLUMNS.put("email", Boolean.FALSE);
		BOARD_COLUMNS.put("reg_date", Boolean.FALSE);
		BOARD_COLUMNS.put("x", Boolean.FALSE);
		BOARD_COLUMNS.put("name", Boolean.TRUE);
		BOARD_COLUMNS.put("memo", Boolean.TRUE);
		BOARD_COLUMNS.put("subject", Boolean.TRUE);
		BOARD_COLUMNS.put("s_file_name1", Boolean.TRUE);
		BOARD_COLUMNS.put("s_file_name2", Boolean.TRUE);
		BOARD_COLUMNS.put("file_name1", Boolean.TRUE);
		BOARD_COLUMNS.put("file_name2", Boolean.TRUE);
		
		COMMENT_COLUMNS.put("no", Boolean.FALSE);
		COMMENT_COLUMNS.put("parent", Boolean.FALSE);
		COMMENT_COLUMNS.put("ismember", Boolean.FALSE);
		COMMENT_COLUMNS.put("name", Boolean.TRUE);
		COMMENT_COLUMNS.put("password", Boolean.FALSE);
		COMMENT_COLUMNS.put("memo", Boolean.TRUE);
		COMMENT_COLUMNS.put("ip", Boolean.FALSE);
		COMMENT_COLUMNS.put("reg_date", Boolean.FALSE);
		
		CATEGORY_COLUMNS.put("no", Boolean.FALSE);
		CATEGORY_COLUMNS.put("num", Boolean.FALSE);
		CATEGORY_COLUMNS.put("name", Boolean.TRUE);
		
		//no, group_no, user_id, password, board_name, name, level, email, homepage, msn, comment, point1, point2, hobby, picture
		MEMBER_COLUMNS.put("no", Boolean.FALSE);
		MEMBER_COLUMNS.put("group_no", Boolean.FALSE);
		MEMBER_COLUMNS.put("user_id", Boolean.FALSE);
		MEMBER_COLUMNS.put("password", Boolean.FALSE);
		MEMBER_COLUMNS.put("board_name", Boolean.FALSE);
		MEMBER_COLUMNS.put("name", Boolean.TRUE);
		MEMBER_COLUMNS.put("level", Boolean.FALSE);
		MEMBER_COLUMNS.put("email", Boolean.FALSE);
		MEMBER_COLUMNS.put("homepage", Boolean.FALSE);
		MEMBER_COLUMNS.put("msn", Boolean.FALSE);
		MEMBER_COLUMNS.put("comment", Boolean.TRUE);
		MEMBER_COLUMNS.put("point1", Boolean.FALSE);
		MEMBER_COLUMNS.put("point2", Boolean.FALSE);
		MEMBER_COLUMNS.put("hobby", Boolean.TRUE);
		MEMBER_COLUMNS.put("picture", Boolean.FALSE);
		
	}
	
	public static Map<String, Boolean> getBoardColumns() {
		return BOARD_COLUMNS;
	}
	
	public static Map<String, Boolean> getCommentColumns() {
		return COMMENT_COLUMNS;
	}
	
	public static Map<String, Boolean> getCategoryColumns() {
		return CATEGORY_COLUMNS;
	}
	
	public static Map<String, Boolean> getMemberColumns() {
		return MEMBER_COLUMNS;
	}
}
