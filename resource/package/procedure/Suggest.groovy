package procedure

import static procedure.GroupConstants.*;
import static procedure.table.SuggestTable.*;
import static procedure.ProcedureHelper.*;
import java.sql.Date;

import net.ion.framework.db.Rows;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.IPropertyFamily;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyComparator;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;

class Suggest extends IProcedure{
	
	private final String REF_SUGGEST = "r_suggest"; 
	private final String REF_KEYWORD = "r_keyword";
	

	public int createWith(String suggId, String suggNm, String suggExp){
		Node node = session.newNode().setAradonId(SUGGEST, suggId).put(SuggId, suggId).put(SuggNm, suggNm).put(SuggExp, suggExp);
		node.put(RegDate, new Date(System.currentTimeMillis()));
		return commit();
	}
	
	public int updateWith(String suggId, String suggNm, String suggExp){
		Node node = session.createQuery().aradonGroupId(SUGGEST, suggId).findOne();
		if(node == null) return 0;
		
		node.put(SuggNm, suggNm).put(SuggExp, suggExp);
		return commit();
	}
	
	public int removeWith(String suggId){
		int removeCount = session.createQuery().aradonGroup(SUGGEST_CONTENT).eq(SuggId, suggId).remove();
		removeCount += session.createQuery().aradonGroup(SUGGEST_KEYWORD).eq(SuggId, suggId).remove();
		removeCount += session.createQuery().aradonGroupId(SUGGEST, suggId).remove();
		return removeCount;
	}
	
	public Rows infoBy(String suggId){
		Node node = session.createQuery().aradonGroupId(SUGGEST, suggId).findOne();
		return fromNode(node, SuggId, SuggNm, SuggExp, "tochar(regdate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows listBy(int listNum, int pageNo, int screenCount){
		NodeScreen screen = session.createQuery().aradonGroup(SUGGEST).descending(RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount));
		
		return fromScreen(screen,"cnt", SuggId, SuggNm, SuggExp, "tochar(regdate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	
	// suggetContent
	public int createContentWith(String suggId, int keyId, String subject, String content, String url,
		                          String thumbnail, String useFlg){
		def contentId = getNextId(session, SUGGEST_CONTENT_ID);
		Node node = session.newNode().setAradonId(SUGGEST_CONTENT, contentId).put(ContentId, contentId).put(SuggId, suggId);
		node.put(KeyId, keyId).put(Subject, subject).put(Content, content).put(Url, url).put(Thumbnail, thumbnail);
		node.put(OrderNo, contentId).put(UseFlg, useFlg).put(RegDate, new Date(System.currentTimeMillis()));
		
		node.addReference(REF_SUGGEST, AradonQuery.newByGroupId(SUGGEST, suggId));
		node.addReference(REF_KEYWORD, AradonQuery.newByGroupId(SUGGEST_KEYWORD, ProcedureHelper.getUID(suggId, keyId)));
		return session.commit();
	}

	
	public int updateContentWith(int contentId, String subject, String content, String url, String thumbnail, String useFlg){
		Node node =  session.createQuery().aradonGroupId(SUGGEST_CONTENT, contentId).findOne();
		if(node == null) return 0;
		
		node.put(Subject, subject).put(Content, content).put(Url, url).put(Thumbnail, thumbnail).put(UseFlg, useFlg);
		return commit();
	}
	
	public int modifyContentWith(Integer[] contentIds, Integer[] orderNos){
		int modifyCount = 0;
		contentIds.eachWithIndex { it , i ->
			Node node =  session.createQuery().aradonGroupId(SUGGEST_CONTENT, it).findOne();
			if(node != null){
				node.put(OrderNo, orderNos[i]);
				modifyCount += session.commit();
			} 
		}
		return modifyCount;
	}
	
	public int removeContentWith(int contentId){
		return session.createQuery().aradonGroupId(SUGGEST_CONTENT, contentId).remove();  
	}
	
	public Rows infoContentBy(int contentId){
		Node node = session.createQuery().aradonGroupId(SUGGEST_CONTENT, contentId).findOne();
		NodeColumns columns = NodeColumns.create(SuggId, ContentId, KeyId, SUGGEST_KEYWORD + ".keyword", Subject, Content, Url,
												Thumbnail+" thumbnailPath", OrderNo, UseFlg, "tochar(regdate, 'yyyy-MM-dd HH:mm:ss') regDate")
		return fromNode(node, columns);	
	}
	
	public Rows findContentBy(String suggId, String keyword){
		NodeColumns columns = NodeColumns.create(SuggId, ContentId, KeyId, SUGGEST_KEYWORD +".keyword", Subject, 
            Content, Url, Thumbnail+" thumbnailPath", OrderNo, UseFlg, "tochar(regdate, 'yyyy-MM-dd HH:mm:ss') regDate");
		
		Node keyNode = session.createQuery().aradonGroup(SUGGEST_KEYWORD).eq(SuggId, suggId).eq(Keyword, keyword).findOne();
		if(keyNode == null)	return fromNode(keyNode, columns);
				
		NodeCursor nc = session.createQuery().aradonGroup(SUGGEST_CONTENT).eq(SuggId, suggId).eq(KeyId, keyNode.getAsInt(KeyId)).ascending(OrderNo).find();
		return fromCursor(nc, columns);
	}
	
	public Rows listContentBy(String suggId, int keyId){
		NodeCursor nc = session.createQuery().aradonGroup(SUGGEST_CONTENT).eq(SuggId, suggId).eq(KeyId, keyId).descending(SuggId, OrderNo).find();
		return fromCursor(nc, ContentId, KeyId, SUGGEST_KEYWORD +".keyword", Subject, OrderNo, UseFlg, "tochar(regdate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows listContentByPage(String suggId, int keyId, int listNum, int pageNo, int screenCount){
		NodeScreen ns = session.createQuery().aradonGroup(SUGGEST_CONTENT).eq(SuggId, suggId).eq(KeyId, keyId).descending(SuggId, OrderNo).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt", ContentId, KeyId, SUGGEST_KEYWORD +".keyword", Subject, OrderNo, UseFlg, "tochar(regdate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	//suggestKeyword
	public int createKeywordWith(String suggId, String keyword){
		Node suggNode = session.createQuery().aradonGroupId(SUGGEST, suggId).findOne();
		if(suggNode == null) return 0;
		
		def keyId = ProcedureHelper.getIncrementNo(KeyId, session.createQuery().aradonGroup(SUGGEST_KEYWORD).eq(SuggId, suggId));
		Node node = suggNode.createChild(keyword).setAradonId(SUGGEST_KEYWORD, ProcedureHelper.getUID(suggId, keyId)).put(KeyId, keyId).put(SuggId, suggId);
		node.put(Keyword, Keyword).put(RegDate, new Date(System.currentTimeMillis()));
		return commit();
	}	
	
	public int updateKeywordWith(String suggId, int keyId, String keyword){
		Node keyNode = session.createQuery().aradonGroupId(SUGGEST_KEYWORD, ProcedureHelper.getUID(suggId, keyId)).findOne();
		if(keyNode == null) return 0;
		
		keyNode.put(Keyword, keyword);
		return commit();
	}
	
	public int removeKeywordWith(String suggId, int keyId){
		int removeCount = session.createQuery().aradonGroup(SUGGEST_CONTENT).eq(SuggId, suggId).eq(KeyId, keyId).remove();
		removeCount += session.createQuery().aradonGroupId(SUGGEST_KEYWORD, ProcedureHelper.getUID(suggId, keyId)).remove();
		return removeCount;
	}
	
	public Rows infoKeywordBy(String suggId, int keyId){
		Node node = session.createQuery().aradonGroupId(SUGGEST_KEYWORD, ProcedureHelper.getUID(suggId, keyId)).findOne();
		return fromNode(node, KeyId, Keyword, "tochar(regdate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows listKeywordBy(String suggId, int listNum, int pageNo, int screenCount){
		NodeScreen screen = session.createQuery().aradonGroup(SUGGEST_KEYWORD).eq(SuggId, suggId).descending(KeyId, RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(screen, "cnt", KeyId, Keyword, "tochar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	
}
