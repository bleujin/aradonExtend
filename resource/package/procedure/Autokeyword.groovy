package procedure

import java.util.List;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Session;
import net.ion.framework.db.Rows;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.SessionQuery;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;
import static procedure.GroupConstants.*;
import static procedure.table.RepositoryTable.*;

class Autokeyword extends IProcedure{
	
	public final static String AutoId = "autoId";
	public final static String KeyType = "keyType";
	public final static String Keyword = "keyword";
	private final String REF_AUTO_REPOSITORY = "r_auto_repository";
		
	public int createWith(CharSequence[] autoIds, String[] keyTypes, String[] keywords){
		int insertCount = 0;
		autoIds.eachWithIndex { it, i ->
			insertCount += insertAutoKeyword(session, it, keyTypes[i], keywords[i]);
		}
		
		return insertCount;
	}
	
	public int mergeWith(CharSequence[] autoIds, String[] keyTypes, String[] keywords){
		autoIds.eachWithIndex { it, i ->
			Node node = session.createQuery().aradonGroupId(AUTOKEYWORD, ProcedureHelper.getUID(it, 'auto', keywords[i])).findOne(); 
			if(node != null){
				node.put(KeyType, keyTypes[i]); 
				session.commit();
			}else{
				insertAutoKeyword(session, it, keyTypes[i], keywords[i]);
			}
		}
		return 1;
	}
	
	public int removeAllWith(String autoId, String keyType){
		return session.createQuery().aradonGroup(AUTOKEYWORD).eq(AutoId, autoId).eq(KeyType, keyType).remove();
	}
	
	public int removeIDWith(String autoId){
		return session.createQuery().aradonGroup(AUTOKEYWORD).eq(AutoId, autoId).remove();
	}

	private static int insertAutoKeyword(Session session, String autoId, String keyType, String keyword){
		Node node = session.newNode().setAradonId(AUTOKEYWORD , ProcedureHelper.getUID(autoId, keyType, keyword))
		            .put(AutoId, autoId).put(KeyType, keyType).put(Keyword, keyword).put("lkeyword", keyword.toLowerCase());
		return session.commit();
	}
	
	public Rows listBy(String autoId){
		List<Node> list = session.createQuery().aradonGroup(AUTOKEYWORD).ne(KeyType, "cutoff").ascending(AutoId, KeyType, Keyword).find().toList(PageBean.ALL);

		List<Node> result = ListUtil.newList();
		list.each { it ->
			if(!existCutOffNode(session, it)) result.add(it);
		}
		return fromList(result, AutoId, Keyword);
	}
	
	public Rows useListBy(String autoId, int listNum, int pageNo, int screenCount){
		List<Node> list = session.createQuery().aradonGroup(AUTOKEYWORD).ne(KeyType, "cutoff").ascending(AutoId, KeyType, Keyword).find().toList(PageBean.ALL);
		
		List<Node> result = ListUtil.newList();
		list.each { it ->
			if(!existCutOffNode(session, it)) result.add(it);
		}
		PageBean page = PageBean.create(listNum, pageNo, screenCount);
		return fromScreen(NodeScreen.create(result.size(),  page.subList(result), page), "cnt",  AutoId, Keyword);
	}
	
	public Rows listBy(String autoId, String keyType, int listNum, int pageNo, int screenCount){
		NodeCursor cursor = session.createQuery().aradonGroup(AUTOKEYWORD).eq(AutoId, autoId).eq(KeyType, keyType).ascending(Keyword).find();
		NodeScreen screen  = cursor.screen(PageBean.create(listNum, pageNo, screenCount));
		
		return fromScreen(screen, "cnt", AutoId, KeyType, Keyword);
	}
	
	public Rows searchBy(String autoId, String keyType, String searchKey, int listNum, int pageNo, int screenCount){
		SessionQuery query = session.createQuery().aradonGroup(AUTOKEYWORD).eq(AutoId, autoId).eq(KeyType, keyType);
		if(StringUtil.isNotEmpty(searchKey)){
			query.eq("lkeyword", searchKey.toLowerCase());
		}

		NodeScreen screen = query.ascending(Keyword).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(screen, "cnt", AutoId, KeyType, Keyword);
	}
	
	private static boolean existCutOffNode(Session session, Node node){
		return session.createQuery().aradonGroupId(AUTOKEYWORD,ProcedureHelper.getUID(node.getString(AutoId), "cutoff", node.getString(Keyword))).existNode();
	}
	
	public Rows repoListBy(String autoId){
		ICursor cursor = session.createQuery().aradonGroup(AUTO_REPOSITORY).eq(AutoId, autoId).ascending(AutoId, RepoId).find();
		
		return fromCursor(cursor, AutoId, RepoId, REPOSITORY+"."+ RepoNm);
	}
	
	public int repoCreateWith(CharSequence[] autoIds, String[] repoIds){
		int insertCount = 0;
		autoIds.eachWithIndex { it, i ->
			Node node = session.newNode().setAradonId(AUTO_REPOSITORY, ProcedureHelper.getUID(it, repoIds[i])).put(AutoId, it).put("repoId", repoIds[i]);
			node.addReference(REF_AUTO_REPOSITORY, AradonQuery.newByGroupId(REPOSITORY, repoIds[i])); 
		 	insertCount += session.commit();
		}
		return insertCount;
	}
	
	public int repoRemoveWith(String autoId){
		 return session.createQuery().aradonGroup(AUTO_REPOSITORY).eq(AutoId, autoId).remove();
	}
}	
