package procedure

import static procedure.GroupConstants.*;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.framework.db.Rows;
import net.ion.framework.util.Debug;
import static procedure.table.RankingTable.*;
import static procedure.table.ScheduleTable.*;
import static procedure.table.RepositoryTable.RepoId;

class Ranking extends IProcedure{
	
	private final String REF_REPOSITORY = "r_repository";
	
	public int createWith(CharSequence[] rankingIds, String[] keywords, String[] fixeds, Integer[] searchCnts){
		int insertCount = 0;
		rankingIds.eachWithIndex { it, i ->
			int orderNo = ProcedureHelper.getIncrementNo(OrderNo, session.createQuery().aradonGroup(RANKING).eq(RankingId, it));
			Node node = session.newNode().setAradonId(RANKING, ProcedureHelper.getUID(it, orderNo, keywords[i]));
			node.put(RankingId, it).put(OrderNo, orderNo).put(Keyword, keywords[i]).put(Fixed, fixeds[i]).put(SearchCnt, searchCnts[i]);
			insertCount += session.commit();
		}
		return insertCount;
	}
	
	public Rows listBy(String rankingId, int listNum, int pageNo, int screenCount){
		NodeScreen ns = session.createQuery().aradonGroup(RANKING).ascending(OrderNo, Keyword).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt", RankingId, OrderNo, Keyword, Fixed, SearchCnt);
	}
	
	public int removeWith(String rankingId){
		return session.createQuery().aradonGroup(RANKING).eq(RankingId, rankingId).remove();
	}
	
	public Rows infoBy(String rankingId){
		Node node = session.createQuery().aradonGroup(SCHEDULE).eq(ScheType, "ranking").eq(TargetId, rankingId).findOne();
		
		NodeColumns columns = NodeColumns.create(ScheId, ScheNm, TargetId, BeginDay, EndDay, RepeatTypeCd, DayOfRepeat, MonToSunBit,
				FrqTypeOfDaily, StartTimeOfOnce, StartTimeOfRepeat, EndTimeOfRepeat, MinuteOfRepeat, OptionBit, 
				LastExecDate, RegDate, RecursiveFlg, TargetId+" "+RankingId, ItemCount+" keywordCnt", TargetValue,
				"decode(mod(floor(divide(monToSunBit, power(2,0))),2), 1, 'checked', '') monToSunBitMon",
				"decode(mod(floor(divide(monToSunBit, power(2,1))),2), 1, 'checked', '') monToSunBitTue",
				"decode(mod(floor(divide(monToSunBit, power(2,2))),2), 1, 'checked', '') monToSunBitWed",
				"decode(mod(floor(divide(monToSunBit, power(2,3))),2), 1, 'checked', '') monToSunBitThu",
				"decode(mod(floor(divide(monToSunBit, power(2,4))),2), 1, 'checked', '') monToSunBitFri",
				"decode(mod(floor(divide(monToSunBit, power(2,5))),2), 1, 'checked', '') monToSunBitSat",
				"decode(mod(floor(divide(monToSunBit, power(2,6))),2), 1, 'checked', '') monToSunBitSun",
				"decode(mod(floor(divide(optionBit, power(2,1))),2), 1, 'checked', '') optionBit_a",
				"decode(mod(floor(divide(optionBit, power(2,2))),2), 1, 'checked', '') optionBit_c",
				"decode(mod(floor(divide(optionBit, power(2,3))),2), 1, 'checked', '') optionBit_m",
				"decode(mod(floor(divide(optionBit, power(2,5))),2), 1, 'checked', '') optionBit_l");
		return fromNode(node, columns);
	}
	
	public int repositoryCreateWith(String[] rankingIds, String[] repoIds){
		int insertCount = 0;
		rankingIds.eachWithIndex { it, i ->
			Node node = session.newNode().setAradonId(RANKING_REPOSITORY, ProcedureHelper.getUID(it, repoIds[i])).put(RankingId, it).put(RepoId, repoIds[i]);
			node.addReference(REF_REPOSITORY, AradonQuery.newByGroupId(REPOSITORY, repoIds[i]));
			insertCount += session.commit();
		}
		return insertCount;
	}
	
	public Rows repositoryListBy(String rankingId){
		NodeCursor nc = session.createQuery().aradonGroup(RANKING_REPOSITORY).eq(RankingId, rankingId).ascending(RankingId, RepoId).find();
		return fromCursor(nc, RankingId, RepoId, REPOSITORY+".repoNm");
	}
	
	public int repositoryRemoveWith(String rankingId){
		return session.createQuery().aradonGroup(RANKING_REPOSITORY).eq(RankingId, rankingId).remove();
	}
	
	public int cutwordCreateWith(String[] rankingIds, String[] cutWords){
		int insertCount =0;
		rankingIds.eachWithIndex {it, i ->
			session.newNode().setAradonId(RANKING_CUTWORD, ProcedureHelper.getUID(it, cutWords[i])).put(RankingId, it).put("cutword", cutWords[i]);
			insertCount += session.commit();
		}
		return insertCount;
	}	
	
	public int cutwordRemoveWith(String rankingId){
		return session.createQuery().aradonGroup(RANKING_CUTWORD).eq(RankingId, rankingId).remove();
	}
	
	public Rows cutwordListBy(String rankingId){
		NodeCursor nc = session.createQuery().aradonGroup(RANKING_CUTWORD).eq(RankingId, rankingId).ascending(RankingId, "cutword").find();
		return fromCursor(nc, RankingId, "cutword");
	}	
	
	public Rows cacheListBy(){
		NodeCursor nc = session.createQuery().aradonGroup(RANKING_CUTWORD).ascending(RankingId, "cutword").find();
		return fromCursor(nc, RankingId, "cutword");
	}
}
