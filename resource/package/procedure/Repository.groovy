package procedure

import java.sql.Date;

import net.ion.framework.db.IONXmlWriter;
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
import net.ion.radon.repository.SessionQuery;
import static procedure.GroupConstants.*;
import static procedure.ProcedureHelper.getStringArray;
import static procedure.table.RepositoryTable.*;
import static procedure.table.ScheduleTable.*;
import static procedure.table.RankingTable.RankingId;
import static procedure.table.DicTable.Synonymity;
import static procedure.Autokeyword.AutoId;

public class Repository extends IProcedure {
	
	private final String REF_ANALYZER = "r_analyzer";
	private final String REF_LOGLEVEL = "r_loglevel";
	private final String REF_SYNONYMITY= "r_synonymity";
	private final String REF_STOPWORD = "r_stopword";
	private final String REF_ENDWORD = "r_endword";
	
	public int createWith(String repoId, String repoNm, String repoExp, String analyzerCd, String logLvlCd,
		String synonymity, String stopWord, String endWord){

		Node node = getSession().newNode();
		node.setAradonId(REPOSITORY, repoId);
		node.put(RepoId, repoId).put(RepoNm, repoNm).put(RepoExp, repoExp).put(AnalyzerCd, analyzerCd);
		node.put(LogLvlCd, logLvlCd).put(Synonymity, synonymity).put(StopWord, stopWord).put(EndWord, endWord);
		node.put(RegDate, new Date(System.currentTimeMillis()));
		
		node.addReference(REF_ANALYZER, AradonQuery.newByGroupId(ANALYZER, analyzerCd));
		node.addReference(REF_LOGLEVEL, AradonQuery.newByGroupId(LOGLEVEL, logLvlCd));
		if(StringUtil.isNotEmpty(synonymity)) node.addReference(REF_SYNONYMITY, getDicQuery(synonymity));
		if(StringUtil.isNotEmpty(stopWord)) node.addReference(REF_STOPWORD, getDicQuery(stopWord));
		if(StringUtil.isNotEmpty(endWord)) node.addReference(REF_ENDWORD, getDicQuery(endWord));
		return commit();
	}
							
	public int updateWith(String repoId, String repoNm, String repoExp, String analyzerCd, String logLvlCd,
		String synonymity, String stopWord, String endWord){
		Node repoNode = session.createQuery().aradonGroupId(REPOSITORY, repoId).findOne();
		
		if(StringUtil.isNotEmpty(synonymity)) repoNode.setReference(REF_SYNONYMITY, getDicQuery(repoNode.getString(Synonymity)), getDicQuery(synonymity));
		if(StringUtil.isNotEmpty(stopWord)) repoNode.setReference(REF_SYNONYMITY, getDicQuery(repoNode.getString(StopWord)), getDicQuery(stopWord));
		if(StringUtil.isNotEmpty(endWord)) repoNode.setReference(REF_SYNONYMITY, getDicQuery(repoNode.getString(EndWord)), getDicQuery(endWord));
		
		repoNode.put(RepoId, repoId).put(RepoNm, repoNm).put(RepoExp, repoExp).put(AnalyzerCd, analyzerCd);
		repoNode.put(LogLvlCd, logLvlCd).put(Synonymity, synonymity).put(StopWord, stopWord).put(EndWord, endWord);
		
		return commit();
	}
	
	private AradonQuery getDicQuery(String value){
		return AradonQuery.newByGroupId(DICTIONARY, value);
	}
	
	public int removeWith(String repoId){
		return session.createQuery().aradonGroupId(REPOSITORY, repoId).remove() ;
	}
	
	public Rows listBy(int listNum, int pageNo, int screenSize){
		NodeScreen screen = session.createQuery().aradonGroup(REPOSITORY).descending(RegDate).find().screen(PageBean.create(listNum, pageNo, screenSize));

		return fromScreen(screen, "cnt", RepoId, RepoNm, AnalyzerCd, ANALYZER+".cdNm analyzerNm", LogLvlCd,
								 LOGLEVEL + ".cdNm loglvlNm", RepoExp, "tochar(regDate,'yyyy-MM-dd HH:mm:ss') regDate" );
	}
	
	public Rows infoBy(String repoId){
		Node result =  session.createQuery().aradonGroupId(REPOSITORY, repoId).findOne();
		return fromNode(result, RepoId, RepoNm, AnalyzerCd, ANALYZER + ".cdnm analyzerNm",
			LogLvlCd, RepoExp, "tochar(regDate,'yyyy-MM-dd HH:mm:ss') regDate",  Synonymity, "r:"+REF_SYNONYMITY+".dicNm synonymityNm", 
			StopWord, "r:"+REF_STOPWORD+".dicNm stopWordNm", EndWord, "r:"+REF_ENDWORD+".dicNm endWordNm");
	}
	
	public Rows analyzerCodeListBy(){
		NodeCursor cursor = session.createQuery().aradonGroup(ANALYZER).find();
		return fromCursor(cursor, "codeId", "codeNm");
	}
	
	public Rows treeBy(){
		NodeCursor  ctexCursor = session.createQuery().aradonGroup(CONTEXT).descending(RepoId, RegDate).find(); 
		Rows ctexRows = fromCursor(ctexCursor, "ctexId catId", "repoId upperCatId", "ctexNm catNm", "ctexType type");
		
		
		NodeCursor repoCursor = session.createQuery().aradonGroup(REPOSITORY).descending(RegDate).find();
		Rows repoRows = fromCursor(repoCursor, "repoId catId", "'rep' upperCatId", "repoNm catNm", "'rep' type");
		
		return NodeRows.unionAll(repoRows, ctexRows);
	}
	
	public Rows logLevelCodeListBy(){
		 return fromCursor(session.createQuery().aradonGroup(LOGLEVEL).find(), "codeId", "cdNm codeNm"); 
	}
	
	public Rows cacheListBy(){
		return fromCursor(session.createQuery().aradonGroup(REPOSITORY).find(), 
			RepoId, RepoNm, AnalyzerCd, Synonymity, StopWord, EndWord);
	}
	
	public Rows useListBy(String repoId) {
		NodeCursor autoCursor = session.createQuery().aradonGroup(AUTO_REPOSITORY).eq(RepoId, repoId).find();
		NodeCursor scheAutoCursor= session.createQuery().aradonGroup(SCHEDULE).in(TargetId, getStringArray(autoCursor, AutoId)).eq(ScheType, "auto").find();
		Rows autoRows = NodeRows.createByCursor(Queryable.Fake, scheAutoCursor, TargetId, ScheNm, ScheType);
		
		NodeCursor rankCursor = session.createQuery().aradonGroup(RANKING_REPOSITORY).eq(RepoId, repoId).find();
		NodeCursor scheRankCursor = session.createQuery().aradonGroup(SCHEDULE).in(TargetId, getStringArray(rankCursor, RankingId)).eq(ScheType, "ranking").find();
		Rows rankRows = NodeRows.createByCursor(Queryable.Fake, scheRankCursor, TargetId, ScheNm, ScheType);
		
		return NodeRows.unionAll(autoRows, rankRows);
	} 
}
