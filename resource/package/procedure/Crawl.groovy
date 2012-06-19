package procedure

import static procedure.GroupConstants.CRAWL;
import static procedure.GroupConstants.CRAWL_LOGIN;
import static procedure.table.CrawlTable.*;

import java.sql.Date;

import net.ion.framework.db.Rows;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.SessionQuery;
import net.ion.radon.repository.myapi.AradonQuery;

class Crawl extends IProcedure{

	private final String REF_LOGIN = "r_login";
	
	public int createWith(String crawlId, String crawlNm, String startUrl, String urlPattern, String exceptExtension, 
		int depth, int interval, String useRobotFilter, String loginId){
		
		Node node = session.newNode().setAradonId(CRAWL, crawlId).put(CrawlId, crawlId).put(CrawlNm, crawlNm)
		.put(StartUrl, startUrl).put(UrlPattern, urlPattern).put(ExceptExtension, exceptExtension).put(Depth, depth)
		.put(Interval, interval).put(UseRobotFilter, useRobotFilter).put(LoginId, loginId).put(RegDate, new Date(System.currentTimeMillis()));
		
		node.addReference(REF_LOGIN,  AradonQuery.newByGroupId(CRAWL_LOGIN, loginId));
		return commit();
	}  
	
	public int updateWith(String crawlId, String crawlNm, String startUrl, String urlPattern, String exceptExtension, 
		int depth, int interval, String useRobotFilter, String loginId) {
	
		Node node = session.createQuery().aradonGroupId(CRAWL, crawlId).findOne();
		if (node == null) return 0;
		
		node.setReference(REF_LOGIN, AradonQuery.newByGroupId(CRAWL_LOGIN, node.getString(LoginId)), AradonQuery.newByGroupId(CRAWL_LOGIN, loginId));
		
		node.put(CrawlNm, crawlNm).put(StartUrl, startUrl).put(UrlPattern, urlPattern).put(ExceptExtension, exceptExtension).put(Depth, depth)
		.put(Interval, interval).put(UseRobotFilter, useRobotFilter).put(LoginId, loginId)
		
		return commit();
	}
		
	public int removeWith(String crawlId) {
		return session.createQuery().aradonGroupId(CRAWL, crawlId).remove();
	}
	
	public Rows infoBy(String crawlId) {
		Node node = session.createQuery().aradonGroupId(CRAWL, crawlId).findOne();
		
		return fromNode(node, CrawlId, CrawlNm, StartUrl, UrlPattern, ExceptExtension, Depth, Interval, 
			UseRobotFilter, LoginId, CRAWL_LOGIN+".detailRule",  "toChar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows listBy(int listNum, int pageNo, int screenCount) {
		NodeScreen ns = session.createQuery().aradonGroup(CRAWL).descending(RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount))
		return fromScreen(ns, "cnt", CrawlId, CrawlNm, StartUrl, UrlPattern, ExceptExtension, Depth, Interval, 
			UseRobotFilter, LoginId, "toChar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	
	public int loginCreateWith(String loginId, String loginNm, String detailRule) {
		session.newNode().setAradonId(CRAWL_LOGIN, loginId)
		.put(LoginId, loginId).put(LoginNm, loginNm).put(DetailRule, detailRule).put(RegDate, new Date(System.currentTimeMillis()));
		
		return commit();
	}
	
	public int loginUpdateWith(String loginId, String loginNm, String detailRule) {
		Node node = session.createQuery().aradonGroupId(CRAWL_LOGIN, loginId).findOne();
		if (node == null) return 0;
		node.put(LoginNm, loginNm).put(DetailRule, detailRule);
		
		return commit();
	}
	
	public int loginRemoveWith(String loginId){
		return session.createQuery().aradonGroupId(CRAWL_LOGIN, loginId).remove();
	}
	
	public Rows loginInfoBy(String loginId) {
		Node node = session.createQuery().aradonGroupId(CRAWL_LOGIN, loginId).findOne();
		return fromNode(node, LoginId, LoginNm, DetailRule, "toChar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows loginListBy(int listNum, int pageNo, int screenCount) {
		NodeScreen screen = session.createQuery().aradonGroup(CRAWL_LOGIN).descending(RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(screen, "cnt", LoginId, LoginNm, DetailRule, "toChar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate")
	}
	
	public Rows treeBy() {
		NodeCursor crawlNc = session.createQuery().aradonGroup(CRAWL).descending(RegDate).find();
		Rows crawls = fromCursor(crawlNc, CrawlId+" catId", "'crawl' upperCatId", CrawlNm+" catNm", "'crawlrule' type");
		NodeCursor loginNc = session.createQuery().aradonGroup(CRAWL_LOGIN).descending(RegDate).find();
		Rows logins = fromCursor(loginNc, LoginId+" catId", "'crawl_rule' upperCatId", LoginNm+" catNm", "'login' type");
		
		return NodeRows.unionAll(crawls, logins);
	}
	
}
