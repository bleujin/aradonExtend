package procedure

import net.ion.framework.db.Rows;
import net.ion.framework.db.RowsImpl;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Column;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.ReferenceQuery;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;
import static procedure.GroupConstants.*;
import static procedure.table.ContextTable.*;
import static procedure.table.RepositoryTable.RepoId;
import static procedure.table.AdminTable.ConnId;

class Context extends IProcedure{
	
	private final String REF_REPOSITORY = "r_repository";
	private final String REF_CTEXTYPE = "r_ctextype";
	private final String REF_CONNECTION = "r_connection";
	private final String REF_SECTION = "r_section";
	
	public Context(){
	}

	public void initSelf() {
	}
	
	public int createWith(String ctexId, String ctexNm, String ctexExp, String ctexType, String repoId,
						 String connId, String defaultSecId, String useFlg, String config){
		Node node = session.newNode(ctexId).setAradonId(CONTEXT, ctexId);
		node.put(CtexId, ctexId).put(CtexNm, ctexNm).put(CtexExp, ctexExp).put(CtexType, ctexType).put(RepoId, repoId)
		    .put(ConnId, connId).put(DefaultSecId, defaultSecId).put(UseFlg, useFlg).put(Config, config)
		    .put(RegDate, DateUtil.currentDateToString(DateUtil.DEFAULT_FORMAT));
		//node.put(IncrementKey, "").put("lastindexeddate","");
		
		node.addReference(REF_REPOSITORY, AradonQuery.newByGroupId(REPOSITORY, repoId));
		node.addReference(REF_CONNECTION, AradonQuery.newByGroupId(CONNECTION, connId));
		node.addReference(REF_CTEXTYPE, AradonQuery.newByGroupId(CONTEXTTYPE, ctexType));
		if(StringUtil.isNotEmpty(defaultSecId)) node.addReference(REF_SECTION, AradonQuery.newByGroupId(SECTION, ProcedureHelper.getUID(repoId, defaultSecId)));
		
		return commit();
	}
	
	public int updateWith(String ctexId, String ctexNm, String ctexExp, String ctexType, String repoId,
		String connId, String defaultSecId, String useFlg, String config){
		
		Node ctexNode = session.createQuery().aradonGroupId(CONTEXT, ctexId).findOne();
		if(ctexNode == null) throw new ResourceException("not found context : " + ctexId);
				
		ctexNode.setReference(REF_REPOSITORY, AradonQuery.newByGroupId(REPOSITORY, ctexNode.getString(RepoId)),AradonQuery.newByGroupId(REPOSITORY, repoId));
		ctexNode.setReference(REF_CTEXTYPE, AradonQuery.newByGroupId(CONTEXTTYPE, ctexNode.getString(CtexType)),AradonQuery.newByGroupId(CONTEXTTYPE, ctexType));
		if(StringUtil.isNotEmpty(defaultSecId))
		ctexNode.setReference(REF_SECTION, AradonQuery.newByGroupId(SECTION, ProcedureHelper.getUID(repoId, ctexNode.getString(DefaultSecId))),
				  		 AradonQuery.newByGroupId(SECTION, ProcedureHelper.getUID(repoId, defaultSecId)));

		ctexNode.put(CtexNm, ctexNm).put(CtexExp, ctexExp).put(CtexType, ctexType).put(RepoId, repoId);
		ctexNode.put(ConnId, connId).put(DefaultSecId, defaultSecId).put(UseFlg, useFlg).put(Config, config);
		
		return commit();
	}
	
	public int removeWith(String ctexId){
		return session.createQuery().aradonGroupId(CONTEXT, ctexId).remove() ;
	}
	
	public Rows listBy(String repoId){
		//repoNode.getReferencedNodes(CONTEXT)
		NodeCursor cursor = session.createQuery().aradonGroup(CONTEXT).eq(RepoId, repoId).descending(RegDate).find();
		return fromCursor(cursor, CtexId, CtexNm, CtexExp, RepoId, ConnId, DefaultSecId,  UseFlg, Config, CtexType, 
			                      CONTEXTTYPE+".cdNm ctexTypeNm", "tochar(regDate,'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	
	public Rows infoBy(String ctexId){
		Node ctexNode = session.createQuery().aradonGroupId(CONTEXT, ctexId).findOne();
		return fromNode(ctexNode , CtexId, CtexNm, CtexExp, CtexType,  CONTEXTTYPE + ".cdNm ctexTypeNm", 
								   RepoId,  REPOSITORY+".repoNm", REPOSITORY+".analyzerCd", ConnId, 
								   CONNECTION +".connNm", "userFlg", Config, IncrementKey, DefaultSecId, 
								   SECTION+".secNm defaultSecNm", "tochar(regDate,'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public int logLastIndexedDateWith(String ctexId, String startDay){
		Node ctexNode = session.createQuery().aradonGroupId(CONTEXT, ctexId).findOne();
		if(ctexNode == null) throw new ResourceException("not found context : " + ctexId);
		
		ctexNode.put("lastIndexedDate", DateUtil.stringToDate(startDay, "yyyyMMdd-HHmmss"));
		return session.commit();
	}
	
	public Rows lastIndexedDateBy(String ctexId){
		Node ctexNode = session.createQuery().aradonGroupId(CONTEXT, ctexId).findOne();
		return fromNode(ctexNode, "tochar(lastIndexedDate, 'yyyyMMdd-HHmmss') lastIndexedDay");		
	}
	
	public Rows typeListBy(){
		ICursor cursor = session.createQuery().aradonGroup(CONTEXTTYPE).descending("codeId").find();
		return fromCursor(cursor, "codeId", "cdNm codeNm");
	}
	
	public Rows keyTypeListBy(){
		ICursor cursor = session.createQuery().aradonGroup(DBKEY).descending("codeId").find();
		return fromCursor(cursor, "codeId", "cdNm codeNm");
	}
	
	public Rows dataTypeListBy(){
		ICursor cursor = session.createQuery().aradonGroup(CONTEXTDATA).ascending("codeId").find();
		return fromCursor(cursor, "codeId", "cdNm codeNm");
	}   
	
	public Rows listByConnection (String connId){
		ICursor cursor = session.createQuery().aradonGroup(CONTEXT).eq(ConnId, connId).descending(RegDate).find();
		return fromCursor(cursor, CtexId, CtexNm, CtexExp, CtexType, RepoId, ConnId, DefaultSecId,
								  UseFlg, Config, "tochar(regDate, 'yyyyMMdd-HHmmss') regDate");
	}
	
	public Rows listByContextType (String ctexType){
		ICursor cursor = session.createQuery().aradonGroup(CONTEXT).eq(CtexType, ctexType).descending(RegDate).find();
		return fromCursor(cursor, CtexId, CtexNm, CtexExp, CtexType, RepoId, ConnId, DefaultSecId,
  								  UseFlg, Config, "tochar(regDate, 'yyyyMMdd-HHmmss') regDate");
	}
	
	public int modifyWith (String repoId, String ctexId, String useFlg){
		Node node = session.createQuery().aradonGroupId(CONTEXT, ctexId).eq(RepoId, repoId).findOne();
		if(node == null) return 0;
		node.put(UseFlg, useFlg);
		return commit();
	}
	 	
}
