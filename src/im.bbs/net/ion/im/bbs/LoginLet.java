package net.ion.im.bbs;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;

import org.restlet.engine.util.Base64;
import org.restlet.representation.Representation;

public class LoginLet extends IMAbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		return null;
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		String userId = getInnerRequest().getParameter("userId");
		String password = new String(Base64.decode(getInnerRequest().getParameter("password")));
		
		String sql = "select no, group_no, user_id, board_name, name, level, email, homepage, msn, comment, point1, point2, picture, reg_date from zetyx_member_table where user_id = :userId and password = password(:password)";
		IDBController dc = getDBController();
		IUserCommand command = dc.createUserCommand(sql);
		
		command.addParam("userId", userId);
		command.addParam("password", password);
		
		Rows rows = command.execQuery();
		return rowsToRepresentation(rows);
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return null;
	}
}
