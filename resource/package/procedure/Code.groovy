package procedure

import net.ion.framework.db.Rows;
import net.ion.framework.db.RowsImpl;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.ReferenceQuery;
import net.ion.radon.repository.myapi.AradonQuery;
import static procedure.GroupConstants.*;

class Code extends IProcedure{
	public Code(){}
	public void initSelf() {
		//getSession().changeWorkspace("code");
	}
	
	public int connectionTest(){
		session.newNode().put("connection", "success");
		return session.commit();
	}
	
	public int initCode(){
		initAnalyzerCode();
		initLogLevelCode();
		initContextTypeCode();
		//initDBKeyCode();
		//initContextDataCode();
		initScheduleTypeCode();
		initTraceTypeCode();
		return 1;
	}
	
	private void putNode(String group, String codeId, String codeNm){
		getSession().newNode().setAradonId(group, codeId).put("codeId",codeId).put("cdNm", codeNm);
	}
	
	public void initAnalyzerCode(){
		putNode(ANALYZER, "cjk", "CJKAnalyzer");
		putNode(ANALYZER, "kor", "KoreaAnalyzer");
		putNode(ANALYZER, "eng", "EnglishAnalyzer");
//		putNode(ANALYZER, "chn", "Chinesenalyzer");
//		putNode(ANALYZER, "jpn", "JapaneseAnalyzer");
//		putNode(ANALYZER, "esp", "SpanishAnalyzer");
//		putNode(ANALYZER, "ptg", "PortugueseAnalyzer");
//		putNode(ANALYZER, "fre", "FrenchAnalyzer");
//		putNode(ANALYZER, "pol", "PolishAnalyzer");
//		putNode(ANALYZER, "ita", "ItalianAnalyzer");
//		putNode(ANALYZER, "dut", "DutchAnalyzer");
//		putNode(ANALYZER, "ger", "GermanAnalyzer");
//		putNode(ANALYZER, "rus", "RussianAnalyzer");
//		putNode(ANALYZER, "bra", "BraziliznAnalyzer");
//		putNode(ANALYZER, "gre", "GreekAnalyzer");
//		putNode(ANALYZER, "std", "StandardAnalyzer");

		getSession().commit();
	}
	
	public void initLogLevelCode(){
		putNode(LOGLEVEL, "high", "High");
		putNode(LOGLEVEL, "normal", "Normal");
		putNode(LOGLEVEL, "low", "Low");
		getSession().commit();
	}
	
	public void initContextTypeCode(){
		putNode(CONTEXTTYPE, "ics", "ICS");
		putNode(CONTEXTTYPE, "db", "DB");
		putNode(CONTEXTTYPE, "crawl", "Crawl");
		getSession().commit();
	}
	
	public void initDBKeyCode(){
		putNode(DBKEY, "date", "date");
		putNode(DBKEY, "seq", "seq");
		putNode(DBKEY, "string", "string");
		getSession().commit();
	}
	
	public void initContextDataCode(){
		putNode(CONTEXTDATA, "data", "Data");
		putNode(CONTEXTDATA, "file", "File");
		putNode(CONTEXTDATA, "all", "Data & File");
		getSession().commit();
	}
	
	public void initScheduleTypeCode(){
		putNode(SCHEDULETYPE, "index", "page.scheduler.type.index");
		putNode(SCHEDULETYPE, "ranking", "page.scheduler.type.ranking");
		putNode(SCHEDULETYPE, "auto", "page.scheduler.type.auto");
		getSession().commit();
	}

	public void initTraceTypeCode(){
		putNode(TRACECODE, "userLogin", "trace.user.login");
		putNode(TRACECODE, "userIns", "trace.user.create");
		putNode(TRACECODE, "userMod", "trace.user.modify");
		putNode(TRACECODE, "userDel", "trace.user.delete");
		
		/*putNode(TRACECODE, "repoIns", "trace.repository.create");
		putNode(TRACECODE, "repoMod", "trace.repository.modify");
		putNode(TRACECODE, "repoDel", "trace.repository.delete");
		putNode(TRACECODE, "repoIndex", "trace.repository.index");
		
		putNode(TRACECODE, "ctexIndex", "trace.context.index");
		putNode(TRACECODE, "ctexIns", "trace.context.create");
		putNode(TRACECODE, "ctexMod", "trace.context.modify");
		putNode(TRACECODE, "ctexDel", "trace.context.delete");
		putNode(TRACECODE, "ctexUseMod", "trace.context.use.modify");
		
		putNode(TRACECODE, "connIns", "trace.connection.create");
		putNode(TRACECODE, "connMod", "trace.connection.modify");
		putNode(TRACECODE, "connDel", "trace.connection.delete");
		
		putNode(TRACECODE, "fgroupIns", "trace.fgroup.create");
		putNode(TRACECODE, "fgroupMod", "trace.fgroup.modify");
		putNode(TRACECODE, "fgroupDel", "trace.fgroup.delete");
		
		putNode(TRACECODE, "sugIns", "trace.suggest.create");
		putNode(TRACECODE, "sugMod", "trace.suggest.modify");
		putNode(TRACECODE, "sugDel", "trace.suggest.delete");
		putNode(TRACECODE, "sugKeyIns", "trace.suggest.key.create");
		putNode(TRACECODE, "sugKeyMod", "trace.suggest.key.modify");
		putNode(TRACECODE, "sugKeyDel", "trace.suggest.key.delete");
		putNode(TRACECODE, "sugContIns", "trace.suggest.content.create");
		putNode(TRACECODE, "sugContMod", "trace.suggest.content.modify");
		putNode(TRACECODE, "sugContDel", "trace.suggest.content.delete");
		
		putNode(TRACECODE, "scheIns", "trace.schedule.create");
		putNode(TRACECODE, "scheMod", "trace.schedule.modify");
		putNode(TRACECODE, "scheDel", "trace.schedule.delete");
		
		putNode(TRACECODE, "dicIns", "trace.dictionary.create");
		putNode(TRACECODE, "dicMod", "trace.dictionary.modify");
		putNode(TRACECODE, "diceDel", "trace.dictionary.delete");
		
		putNode(TRACECODE, "crawlIns", "trace.crawl.create");
		putNode(TRACECODE, "crawlMod", "trace.crawl.modify");
		putNode(TRACECODE, "crawlDel", "trace.crawl.delete");
		
		putNode(TRACECODE, "crawlLIns", "trace.crawl.login.create");
		putNode(TRACECODE, "crawlLMod", "trace.crawl.login.modify");
		putNode(TRACECODE, "crawlLDel", "trace.crawl.login.delete");*/
		getSession().commit();
	}
}
