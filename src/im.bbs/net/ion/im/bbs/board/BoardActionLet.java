package net.ion.im.bbs.board;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.im.bbs.IMAbstractLet;
import net.ion.im.bbs.IMConstants;
import net.ion.im.bbs.URLConfigLoader;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;

public class BoardActionLet extends IMAbstractLet {
	
	@Override
	protected Representation myDelete() throws Exception {
		URLConfigLoader configLoader = (URLConfigLoader)getContext().getAttributeObject("boardActionUrl.config");
		
		String userId = getInnerRequest().getParameter("userId");
		String password = getInnerRequest().getParameter("password");
		
		String contentId = getInnerRequest().getAttribute("contentId");
		String bbsId = getInnerRequest().getAttribute("bbsId");
		
		BoardActionEntry entry = new BoardActionEntry(configLoader.getConfig(), userId, password);
		ActionResponse result = entry.deleteArticle(bbsId, contentId);
		
		return toRepresentation(result.toRepresentaion());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Representation myGet() throws Exception {
		String bbsId = getInnerRequest().getAttribute("bbsId");
		String contentId = getInnerRequest().getAttribute("contentId");
		
		String tableName = String.format("%s_board_%s", IMConstants.TABLE_PREFIX, bbsId);
		IDBController dc = getDBController();
		
		String updateViewCountSQL = String.format("update %s set hit = hit + 1 where no = :contentId", tableName);
		IUserCommand updateCommand = dc.createUserCommand(updateViewCountSQL);
		updateCommand.addParam("contentId", contentId);
		updateCommand.execUpdate();
				
		
		String getSQL = String.format("select t1.memo, case t2.open_picture when '1' then t2.picture else '' end as picture, ismember, t1.no, t1.name, t1.homepage, t1.email, subject, use_html, reply_mail, category, is_secret, sitelink1, sitelink2, file_name1, file_name2, s_file_name1, s_file_name2, download1, download2, t1.reg_date, hit, total_comment from %s t1 left join zetyx_member_table t2 on t1.ismember = t2.no where t1.no = :contentId", tableName);
		IUserCommand command = dc.createUserCommand(getSQL);
		command.addParam("contentId", Integer.valueOf(contentId));
		
		Rows rows = command.execQuery();
		return rowsToRepresentation(rows);
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		URLConfigLoader configLoader = (URLConfigLoader)getContext().getAttributeObject("boardActionUrl.config");
		String logicalBasePath = getContext().getAttributeObject("let.upload.path.logical", String.class);
		
		String userId = getInnerRequest().getParameter("userId");
		String password = getInnerRequest().getParameter("password");
		
		String bbsId = getInnerRequest().getAttribute("bbsId");
		String subject = getInnerRequest().getParameter("subject");
		String content = getInnerRequest().getParameter("memo");
		String category = getInnerRequest().getParameter("category");
		
		List<UploadFile> uploadFiles = handleUploadFile(entity, logicalBasePath, bbsId);
		
		BoardActionEntry entry = new BoardActionEntry(configLoader.getConfig(), userId, password);
		ActionResponse result = entry.addArticle(bbsId, category, subject, content, uploadFiles.toArray(new UploadFile[0]));
		
		return toRepresentation(result.toRepresentaion());
	}
	
	private List<UploadFile> handleUploadFile(Representation entity, String logicalBasePath, String bbsId) throws Exception {
		List<UploadFile> uploadFiles = new ArrayList<UploadFile>();
		String baseFilePath = getContext().getAttributeObject("let.upload.path.physical", String.class);
		
		if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)){ // put mutation
			for (Entry entry : getInnerRequest().getFormParameter().entrySet()) {
				if(entry.getValue() instanceof FileItem) {
					FileItem fileItem = (FileItem) entry.getValue() ;
					String logicalPath = String.format("%s/%s", logicalBasePath, bbsId);
					
					UploadFile uploadFile = saveFile(baseFilePath, logicalPath, fileItem);
					uploadFiles.add(uploadFile);
				}
			}
		}
		return uploadFiles;
	}

	private UploadFile saveFile(String basePath, String logicalPath, FileItem fileItem) throws Exception {
		File uploadFile = new File(String.format("%s/%s/%s", basePath , logicalPath, fileItem.getName()));
		File parent = new File(uploadFile.getParent());
		parent.mkdirs();
		
		fileItem.write(uploadFile);
		
		return new UploadFile(String.format("%s/%s", logicalPath, fileItem.getName()));
	}	
	
	@Override
	protected Representation myPut(Representation entity) throws Exception {
		URLConfigLoader configLoader = (URLConfigLoader)getContext().getAttributeObject("boardActionUrl.config");
		
		String contentId = getInnerRequest().getAttribute("contentId");
		String bbsId = getInnerRequest().getAttribute("bbsId");
		
		String userId = getInnerRequest().getParameter("userId");
		String password = getInnerRequest().getParameter("password");
		String subject = getInnerRequest().getParameter("subject");
		String content = getInnerRequest().getParameter("memo");
		String category = getInnerRequest().getParameter("category");
		
		BoardActionEntry entry = new BoardActionEntry(configLoader.getConfig(), userId, password);
		ActionResponse result = entry.updateArticle(bbsId, category, contentId, subject, content);
		return toRepresentation(result.toRepresentaion());
	}
}
