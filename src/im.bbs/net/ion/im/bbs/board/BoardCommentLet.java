package net.ion.im.bbs.board;

import net.ion.framework.db.DBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.im.bbs.IMAbstractLet;
import net.ion.im.bbs.URLConfigLoader;

import org.restlet.representation.Representation;

public class BoardCommentLet extends IMAbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		URLConfigLoader configLoader = (URLConfigLoader)getContext().getAttributeObject("boardActionUrl.config");
		
		String userId = getInnerRequest().getParameter("userId");
		String password = getInnerRequest().getParameter("password");
		
		String bbsId = getInnerRequest().getAttribute("bbsId");
		String contentId = getInnerRequest().getAttribute("contentId");
		String commentId = getInnerRequest().getAttribute("commentId");
		
		CommentActionEntry entry = new CommentActionEntry(configLoader.getConfig(), userId, password);
		ActionResponse result = entry.deleteComment(bbsId, contentId, commentId);
		return toRepresentation(result.toRepresentaion());
	}

	@Override
	protected Representation myGet() throws Exception {
		String bbsId = getInnerRequest().getAttribute("bbsId");
		String contentId = getInnerRequest().getAttribute("contentId");
		
		String sql = "select case t2.open_picture when '1' then t2.picture else '' end as picture, ismember, t1.no, parent, t1.name, memo, t1.reg_date from zetyx_board_comment_%s t1 left join zetyx_member_table t2 on t1.ismember = t2.no where parent = :contentId order by no";
		String query = String.format(sql, bbsId);
		
		DBController dc = (DBController) getContext().getAttributeObject("connection.mysql.im");
		IUserCommand command = dc.createUserCommand(query);
		command.addParam("contentId", Integer.valueOf(contentId));
		
		Rows rows = command.execQuery();
		return rowsToRepresentation(rows);
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		URLConfigLoader configLoader = (URLConfigLoader)getContext().getAttributeObject("boardActionUrl.config");
		
		String userId = getInnerRequest().getParameter("userId");
		String password = getInnerRequest().getParameter("password");
		
		String bbsId = getInnerRequest().getAttribute("bbsId");
		String contentId = getInnerRequest().getAttribute("contentId");
		String memo = getInnerRequest().getParameter("memo");
		
		CommentActionEntry entry = new CommentActionEntry(configLoader.getConfig(), userId, password);
		ActionResponse result = entry.addComment(bbsId, contentId, memo);
		return toRepresentation(result.toRepresentaion());
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return null;
	}

}
