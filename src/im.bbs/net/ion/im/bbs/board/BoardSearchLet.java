package net.ion.im.bbs.board;

import java.net.URLDecoder;

import net.ion.framework.db.DBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.im.bbs.IMAbstractLet;
import net.ion.im.bbs.IMConstants;

import org.restlet.representation.Representation;

public class BoardSearchLet extends IMAbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		String bbsId = getInnerRequest().getAttribute("bbsId");
		String searchText = "%" + URLDecoder.decode(getInnerRequest().getAttribute("searchText"), "utf-8") + "%";
		
		String tableName = String.format("%s_board_%s", IMConstants.TABLE_PREFIX, bbsId);
		String queryTemplate = "select ismember, no, memo, name, homepage, email, subject, use_html, reply_mail, category, is_secret, sitelink1, sitelink2, file_name1, file_name2, s_file_name1, s_file_name2, download1, download2, reg_date, hit, total_comment from %s where ( name like '%s' or subject like '%s' or memo like '%s' ) order by headnum asc";
		String query = String.format(queryTemplate, tableName, searchText, searchText, searchText);
		
		DBController dc = (DBController)getContext().getAttributeObject("connection.mysql.im");
		IUserCommand command = dc.createUserCommand(query);
		
		Rows rows = command.execQuery();
		return rowsToRepresentation(rows);
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		return null;
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
