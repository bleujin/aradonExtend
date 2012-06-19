package net.ion.im.bbs.board;

import net.ion.framework.db.DBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.im.bbs.IMAbstractLet;
import net.ion.im.bbs.IMConstants;

import org.restlet.representation.Representation;

public class CategoryListLet extends IMAbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		String bbsId = getInnerRequest().getAttribute("bbsId");
		DBController dc = (DBController)getContext().getAttributeObject("connection.mysql.im");

		String tableName = String.format("%s_board_category_%s", IMConstants.TABLE_PREFIX, bbsId);
		String sql = String.format("select * from %s where exists(select 1 from zetyx_admin_table where name = '%s' and use_category = '1')", tableName, bbsId);
		IUserCommand command = dc.createUserCommand(sql);
		
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
