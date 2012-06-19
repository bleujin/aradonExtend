package net.ion.im.bbs.board;

import java.io.File;
import java.io.FileInputStream;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Row;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.im.bbs.IMAbstractLet;

import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;

public class DownloadLet extends IMAbstractLet {

	@Override
	protected Representation myDelete() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Representation myGet() throws Exception {
		// /download/{bbsId}/{contentId}/1 or 2
		String bbsId = getInnerRequest().getAttribute("bbsId");
		String contentId = getInnerRequest().getAttribute("contentId");
		String fileNum = getInnerRequest().getAttribute("fileNum");
		String fileStore = (String)getContext().getAttributeObject("let.upload.path.physical");
		
		IDBController dc = getDBController();
		String downloadCountColumn = String.format("download%s", fileNum);
		
		String tableName = String.format("zetyx_board_%s", bbsId);
		String updateSQL = String.format("update %s set %s = %s + 1 where no = :contentId", tableName, downloadCountColumn, downloadCountColumn);
		IUserCommand updateCommand = dc.createUserCommand(updateSQL);
		updateCommand.addParam("contentId", contentId);
		updateCommand.execUpdate();
		
		String sql = String.format("select file_name1, file_name2, s_file_name1, s_file_name2 from %s where no = :contentId", tableName);
		IUserCommand command = dc.createUserCommand(sql);
		command.addParam("contentId", contentId);
		Rows rows = command.execQuery();
		
		if(rows.first()) {
			Row row = rows.firstRow();
			String path = String.format("file_name%s", fileNum);
			
			String pathValue = row.getString(path);
			String fullPath = fileStore + pathValue;
			
			return new InputRepresentation(new FileInputStream(new File(fullPath)), MediaType.APPLICATION_OCTET_STREAM);
		} else {
			return null;
		}
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
