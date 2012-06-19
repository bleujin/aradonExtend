package net.ion.im.bbs.board;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.im.bbs.IMAbstractLet;

import org.restlet.representation.Representation;

public class BoardListLet extends IMAbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		String bbsId = getInnerRequest().getAttribute("bbsId");
		int listNum = getInnerRequest().getAttributeAsInteger("listNum", 10);
		int pageNo = getInnerRequest().getAttributeAsInteger("pageNo", 1);

		String sql = String.format("select t1.prev_no, t1.next_no, case 'zetyx_board_%s' when 'zetyx_board_memonote' then t1.memo else '' end as memo, case t2.open_picture when '1' then t2.picture else '' end as picture, ismember, t1.no, t1.name, t1.homepage, t1.email, subject, use_html, reply_mail, category, is_secret, sitelink1, sitelink2, file_name1, file_name2, s_file_name1, s_file_name2, download1, download2, t1.reg_date, hit, total_comment from zetyx_board_%s t1 left join zetyx_member_table t2 on t1.ismember = t2.no order by headnum,arrangenum limit :startNum, :listNum", bbsId, bbsId);
		IDBController dc = (IDBController) getContext().getAttributeObject("connection.mysql.im");
		IUserCommand command = dc.createUserCommand(sql);
		
		command.addParam("startNum", (pageNo - 1) * listNum);
		command.addParam("listNum", listNum);
		
		Rows rows = command.execQuery();
		return rowsToRepresentation(rows);
	}
	
	@Override
	protected Representation myPost(Representation entity) throws Exception {
		return null;
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return null;
	}

}
