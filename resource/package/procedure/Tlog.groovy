package procedure

import static procedure.GroupConstants.*;
import static procedure.ProcedureHelper.*;
import static procedure.table.TlogTable.*;
import static procedure.table.RankingTable.*;
import static procedure.Autokeyword.AutoId;

import java.sql.Date;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.swing.text.DefaultEditorKit.CutAction;

import net.ion.framework.db.IONXmlWriter;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.IPropertyFamily;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.PropertyComparator;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;


class Tlog extends IProcedure{

	public int insertLogWith(String keyword, String date, int searchCnt, String position, String siteId, int page,
		                     String address, String repoId, String userId){
	
	    session.newNode().setAradonId(LOG, getNextId(session, LOG_ID)).put(Keyword, keyword).put(QueryDay, DateUtil.calendarToDayString(Calendar.getInstance()))
		.put(SearchCnt, searchCnt).put(Position, position).put(SiteId, siteId).put(QueryDate, DateUtil.calendarToString(Calendar.getInstance(), "yyyyMMddHHmmss"))
		.put(Page, page).put(Address, address).put(RepoId, repoId).put(UserId, userId)
		
		return commit();
	}
	
	 
	 public int logSearchTaskWith(CharSequence[] querys, String[] sorts, Integer[] resultCounts, String[] positions, String[] siteIds, Integer[] pages,
		 String[] addresses, String[] repoIds, String[] userIds, String[] queryDays){ 
		 
		 int insertCount = 0;
		 querys.eachWithIndex { it, i ->
			 session.newNode().setAradonId(LOG, getNextId(session, LOG_ID)).put(Keyword, it).put(Sort, sorts[i]).put(Position, positions[i]).put(SiteId, siteIds[i])
			 .put(Page, pages[i]).put(Address, addresses[i]).put(RepoId, StringUtil.defaultIfEmpty(repoIds[i], "N/A")).put(UserId, userIds[i]).put(QueryDate, queryDays[i])
			 .put(QueryDay, StringUtil.substring(queryDays[i], 0, 8));
			 insertCount += session.commit();  
		}
		return insertCount;
	}
		
	public Rows keywordListByRanking(String rankingId, String startDay, String endDay, int count){
		PropertyQuery conds = PropertyQuery.createByAradon(LOG);
		conds.in(RepoId, getStringArray(session.createQuery().aradonGroup(RANKING_REPOSITORY).eq(RankingId, rankingId).find(), RepoId));
		conds.nin(Keyword, getStringArray(session.createQuery().aradonGroup(RANKING_CUTWORD).eq(RankingId, rankingId).find() ,"cutword"));
		List<Node> list = session.group(PropertyQuery.create(Keyword, true), conds, PropertyQuery.create("cnt", 0), "function(obj, prev){prev.cnt += obj.searchcnt;}");
		Collections.sort(list, PropertyComparator.newDescending("cnt")) ;
		
		List<String>  fixedKeywords = Arrays.asList(getStringArray(session.createQuery().aradonGroup(RANKING).eq(RankingId, rankingId).eq("fixed", "T").find(), Keyword));
		List<Node> unFixedNodes = ListUtil.newList();
		int index = 1;
		for(Node node : list){
			if(fixedKeywords.contains(node.getString(Keyword)))
				continue;
			node.put("cnt", (int) NumberUtil.toDouble(StringUtil.toString(node.get("cnt"))));
			node.put("ranking", StringUtil.leftPad(String.valueOf(index++), 3, "0") +".1");
			unFixedNodes.add(node);
		}
		
		Rows noFixedRows = fromList(unFixedNodes , "ranking", Keyword, "cnt searchCnt", "'F' fixed");

		Rows fixedRows = fromCursor(session.createQuery().aradonGroup(RANKING).eq(RankingId, rankingId).eq("fixed", "T").find(),
				"append(lpad(orderNo, 3, '0'), '.0') ranking", Keyword, "searchCnt", "fixed");
		
		//NodeRows.unionAll(count, fixedRows, noFixedRows)
		return NodeRows.unionAll(fixedRows, noFixedRows);
	}	 
	
	
	public Rows keywordListByAuto(String autoId, int extractCount){
		List<Node> list = session.group(PropertyQuery.create(Keyword, true),
			PropertyQuery.createByAradon(LOG).in(RepoId, getStringArray(session.createQuery().aradonGroup(AUTO_REPOSITORY).eq(AutoId, autoId).find(), RepoId)),
			PropertyQuery.create("total", 0),
			"function(obj, prev) { prev.total= prev.total+1;}");

		List<Node> results = list.subList(0, (extractCount >= list.size())? list.size(): extractCount);
		
		return fromList(results, Keyword, "total" );
	}
	
	public int renderingBySearch(){
		String startDay = getStartDay();
		String endDay = DateUtil.currentDateToString("yyyyMMdd");
		
		session.createQuery().aradonGroup(RENDERING_TIME).remove();
		insertRenderingTime(startDay, endDay);
		
		session.createQuery().aradonGroup(STAT_TIME).remove();
		session.createQuery().aradonGroup(STAT_DAY).remove();
		session.createQuery().aradonGroup(STAT_TIME_KEYWORD).remove();
		session.createQuery().aradonGroup(STAT_DAY_KEYWORD).remove();
		session.createQuery().aradonGroup(STAT_TIME_ADDRESS).remove();
		session.createQuery().aradonGroup(STAT_DAY_ADDRESS).remove();
		
		int insertCount=0;
		insertCount+= insertStatTime(startDay, endDay);
		insertCount+= insertStatDay(startDay, endDay);
		insertCount+= insertStatTimeKeyword(startDay, endDay);
		insertCount+= insertStatDayKeyword(startDay, endDay);
		insertCount+= insertStatTimeAddress(startDay, endDay);
		insertCount+= insertStatDayAddress(startDay, endDay);
		return insertCount;
	} 
	
	private int insertStatDayAddress(String startDay, String endDay)  throws Exception {
		List<Node> result = session.group(
				PropertyQuery.create(QueryDay, true).put(SiteId, true).put(Address, true),
				getRenderConditionQuery(startDay, endDay),
				PropertyQuery.create("resultCnt", 0).put("maxCnt", -1),
				"function(obj, prev) { prev.resultcnt = prev.resultcnt+ 1; if(prev.maxcnt < 0) prev.maxcnt = obj.searchcnt;  if(obj.searchcnt > prev.maxcnt){prev.maxcnt = obj.searchcnt;}  }");
				
		for(Node node : result){
			newNode(node, STAT_DAY_ADDRESS, getUID(node.getString(QueryDay), node.getString(SiteId), node.getString(Address)));
		}
		return result.size();
	}

	private int insertStatTimeAddress(String startDay, String endDay) throws Exception {
		List<Node> result = session.group(
				PropertyQuery.create(QueryDay, true).put(QueryHour, true).put(SiteId, true).put(Address, true),
				getRenderConditionQuery(startDay, endDay),
				PropertyQuery.create("resultCnt", 0),
				"function(obj, prev) { prev.resultcnt = prev.resultcnt+ 1;}");
		for(Node node : result){
			int serNo =  getIncrementNo("serNo", session.createQuery().aradonGroup(STAT_TIME_ADDRESS));
			node.put("serNo", serNo);
			newNode(node, STAT_TIME_ADDRESS, serNo);
		}
		return result.size();
	}

	private int insertStatDayKeyword(String startDay, String endDay) throws Exception {
		List<Node> result = session.group(
			PropertyQuery.create(QueryDay, true).put(SiteId, true).put(Keyword, true).put(Page, true).put(Position, true),
			getRenderConditionQuery(startDay, endDay),
			PropertyQuery.create("resultCnt", 0).put("maxCnt", -1),
			"function(obj, prev) { prev.resultcnt = prev.resultcnt+ 1; if(prev.maxcnt < 0) prev.maxcnt = obj.searchcnt;  if(obj.searchcnt > prev.maxcnt){prev.maxcnt = obj.searchcnt;}  }");
			
		for(Node node : result){
			int serNo =  getIncrementNo("serNo", session.createQuery().aradonGroup(STAT_DAY_KEYWORD));
			node.put("serNo", serNo);
			newNode(node, STAT_DAY_KEYWORD, serNo);
		}
		return result.size();
	}
	

	private int insertStatTimeKeyword(String startDay, String endDay) throws Exception {
		List<Node> result = session.group(PropertyQuery.create(QueryDay, true).put(QueryHour, true).put(SiteId, true).put(Keyword, true).put(Page, true).put(Position, true),
				getRenderConditionQuery(startDay, endDay),
				PropertyQuery.create("resultCnt", 0).put("maxCnt", -1),
				"function(obj, prev) { prev.resultcnt = prev.resultcnt+ 1; if(prev.maxcnt < 0) prev.maxcnt = obj.searchcnt;  if(obj.searchcnt > prev.maxcnt){prev.maxcnt = obj.searchcnt;}  }");
	
		for(Node node : result){
			int serNo =  getIncrementNo("serNo", session.createQuery().aradonGroup(STAT_TIME_KEYWORD));
			node.put("serNo", serNo);
			newNode(node, STAT_TIME_KEYWORD, serNo);
		}
		return result.size();
	}

	private int insertStatDay(String startDay, String endDay) throws Exception {
		List<Node> result = session.group(PropertyQuery.create(QueryDay, true).put(Address, true).put(SiteId, true),
				getRenderConditionQuery(startDay, endDay),
				PropertyQuery.create("resultcnt", 0).put("averagecnt", 0),
				"function(obj, prev) { prev.resultcnt = prev.resultcnt+ 1; prev.averagecnt = prev.resultcnt;  }");
		
		for(Node node : result) {
			newNode(node, STAT_DAY, getUID(node.getString(QueryDay), node.getString(SiteId)));
		}
		return result.size();
	}
	
	private int insertStatTime(String startDay, String endDay) throws Exception {
		List<Node> result = session.group(
					PropertyQuery.create(QueryDay, true).put(QueryHour, true).put(Address, true).put(SiteId, true),
					getRenderConditionQuery(startDay, endDay),
					PropertyQuery.create("resultcnt", 0).put("averagecnt", 0),
					"function(obj, prev) { prev.resultcnt = prev.resultcnt+ 1; prev.averagecnt = prev.resultcnt;  }");
		
		
		for (Node node : result) {
			newNode(node, STAT_TIME, getUID(node.getString(QueryDay), node.getString(QueryHour), node.getString(SiteId)));
		}
		return result.size();
	}
	private IPropertyFamily getRenderConditionQuery(String startDay, String endDay){
		return PropertyQuery.create().between(QueryDate, startDay+"000000", endDay+"235959")
		.between(QueryDay, startDay+"000000", endDay+"235959")
		.in(RepoId, getStringArray(session.createQuery().aradonGroup(REPOSITORY).find(), RepoId));
	}
	private void newNode(Node node, String groupId, Object uid) {
		session.newNode().setAradonId(groupId, uid).putAll(node.toPropertyMap());
		commit();
	}


	private void insertRenderingTime(String startDay, String endDay) {
		session.newNode().setAradonId(RENDERING_TIME, getUID(startDay, endDay)).put(StartTime, startDay).put(EndTime, endDay);
		session.commit();
	}

	private String getStartDay() {
		Node node = session.createQuery().aradonGroup(RENDERING_TIME).lt(EndTime, "99991231").descending(EndTime).findOne();
		if (node == null) return "20010101";
		return node.getString(EndTime);
	}
	
	public Rows searchStreamBy(String queryDay) {
		ICursor cursor = session.createQuery().aradonGroup(LOG)
		.eq(QueryDay, queryDay).gt(QueryDay, queryDay)
		.in(RepoId, getStringArray(session.createQuery().aradonGroup(REPOSITORY).find(), RepoId))
		.eq(Page, 1).find();
		
		return fromCursor(cursor, QueryDate, Keyword, SiteId, Address);
	}
	
	public int createSearchStream(String queryDay, String siteId, String keyword, int resultCnt, int depth){
		int serNo = getIncrementNo("serNo", session.createQuery().aradonGroup(STAT_SEARCHSTREAM));
		session.newNode().setAradonId(STAT_SEARCHSTREAM, serNo).put("serNo", serNo).put(QueryDay, queryDay)
		.put(SiteId, siteId).put(Keyword, keyword).put(ResultCnt, resultCnt).put(Depth, depth);
		
		return commit();
	}
	
	public int cleanSearchStream(String queryDay){
		return session.createQuery().aradonGroup(STAT_SEARCHSTREAM).eq(QueryDay, queryDay).remove();
	}
	
	public int traceCreateWith(String userId, String typeCd, String trcCont, String connIp) {
		int traceId = getIncrementNo(TraceId, session.createQuery().aradonGroup(TRACE));
		Node node = session.newNode().setAradonId(TRACE, traceId).put(TraceId, traceId).put(UserId, userId).put(TypeCd, typeCd).put(RegDate, new Date(System.currentTimeMillis()))
		.put(RegDay, DateUtil.currentDateToString("yyyyMMdd")).put(TraceCont, trcCont).put(ConnIp, connIp);
		
		node.addReference("r_traceCode", AradonQuery.newByGroupId(TRACECODE, typeCd));
		node.addReference("r_user", AradonQuery.newByGroupId(ICSSUSER, userId));
		
		return commit();
	}
	
	public Rows traceCodeListBy(){
		return fromCursor(session.createQuery().aradonGroup(TRACECODE).find(), "codeId", "cdNm");
	}
	
	public Rows traceListBy(String typeCd, String userId, String beginDay, String endDay) {
		NodeCursor nc = session.createQuery().aradonGroup(TRACE).between(RegDay, beginDay, endDay + "235959")
		.eq(TypeCd, typeCd).eq(UserId, userId).descending(RegDay).find();
		
		return fromCursor(nc, TraceId, TypeCd, UserId, 
			"decode(sign(minus(length(trcCont),30)), 1, append(substr(trcCont, 1, 30), '....'), trcCont) shortTrcCont",
			TraceCont, "tochar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate", RegDay, ConnIp, TypeCd + " typeId", TRACECODE + ".cdNm typeNm", ICSSUSER + ".userNm");
	}
	
	public int traceRemoveWith(String range) {
		Date endDate = decode(range, "year", getPreviousDate(Calendar.YEAR), "month", getPreviousDate(Calendar.MONTH), "week", getPreviousDate(Calendar.WEEK_OF_MONTH), "day", getPreviousDate(Calendar.DATE));
		return session.createQuery().aradonGroup(TRACE).lt("regDate", endDate).remove();
	}
	
	
}
