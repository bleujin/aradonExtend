package net.ion.im.bbs.calendar;

import net.ion.framework.db.DBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.im.bbs.IMAbstractLet;
import net.ion.im.bbs.util.IMCalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

public class CalendarListLet extends IMAbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		return new EmptyRepresentation();
	}

	@Override
	protected Representation myGet() throws Exception {
		String bbsId = getInnerRequest().getAttribute("bbsId");
		String date = getInnerRequest().getAttribute("date");
		
		String searchDate = (StringUtils.isEmpty(date) ? IMCalendarUtils.getCurrentMonth() : date);
		String sql = String.format("select ismember, no, memo, name, homepage, email, subject, use_html, reply_mail, category, is_secret, sitelink1, sitelink2, file_name1, file_name2, s_file_name1, s_file_name2, download1, download2, reg_date, hit, total_comment from zetyx_board_%s where memo like :startDate order by reg_date asc", bbsId);
		
		DBController dc = (DBController) getContext().getAttributeObject("connection.mysql.im");
		IUserCommand command = dc.createUserCommand(sql);
		
		command.addParam("startDate", IMCalendarUtils.getIMCalendarDateFormat(searchDate));
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
