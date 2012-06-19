package procedure

import net.ion.radon.repository.PropertyQuery;
import java.sql.Date;
import java.util.List;

import net.ion.radon.repository.PropertyQuery;
import net.ion.framework.db.Rows
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.HashFunction;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeResult;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.myapi.ICursor;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.SessionQuery;
import static procedure.GroupConstants.*;
import static procedure.table.DicTable.*;

class Dic extends IProcedure{
	
	public int createWith(String dicId, String dicNm, String dicExp, String dicType){
		Node node = session.newNode(dicId).setAradonId(DICTIONARY, dicId)
		            .put(DicId, dicId).put(DicNm, dicNm).put(DicExp, dicExp).put(DicType, dicType)
		            .put(RegDate,  new Date(System.currentTimeMillis()));
		return commit();
	}
	  
	public int updateWith(String dicId, String dicNm, String dicExp){
		Map<String, Object> map = MapUtil.newMap();
		map.put(DicId, dicId);
		map.put(DicNm, dicNm);
		map.put(DicExp, dicExp);
		
		session.createQuery().aradonGroupId(DICTIONARY, dicId).updateOne(map);
		return commit();
	}
	
	public int removeWith(String dicId){
		return session.createQuery().aradonGroupId(DICTIONARY, dicId).remove() ;
	}
	
	public Rows infoBy(String dicId){
		NodeCursor nc = session.createQuery().aradonGroupId(DICTIONARY, dicId).find() ;
		return fromCursor(nc, NodeColumns.create(DicId, DicNm, DicExp, DicType, RegDate));
	}
	
	public Rows listBy(String dicType){
		NodeCursor nc = session.createQuery().aradonGroup(DICTIONARY).eq(DicType, dicType).descending(RegDate).find() ;
		return fromCursor(nc, NodeColumns.create(DicId, DicNm, DicExp, DicType,  RegDate));
	}

	public int wordCreateWith(String dicId, String word, String synonymity, String both){
		Node node = session.newNode().setAradonId(DIC_WORD , HashFunction.hashGeneral(ProcedureHelper.getUID(word, synonymity)))
		            .put(Word, word).put(Synonymity, synonymity).put(DicId, dicId).put(LWord, word.toLowerCase()).put(LSynonymity, synonymity.toLowerCase());
		
		if( "F".equalsIgnoreCase(both) || existWordNode(dicId, synonymity, word)) return getSession().commit();
		Node otherNode = getSession().newNode().setAradonId(DIC_WORD , HashFunction.hashGeneral(ProcedureHelper.getUID(synonymity, word)))
		             .put(Word, synonymity).put(Synonymity, word).put(DicId, dicId).put(LWord, synonymity.toLowerCase()).put(LSynonymity, word.toLowerCase());;
		return getSession().commit();
	}

	private boolean existWordNode(String dicId, String word, String synonymity) {
		NodeCursor nc = session.createQuery().aradonGroupId(DIC_WORD, HashFunction.hashGeneral(ProcedureHelper.getUID(word, synonymity))).find();
		return nc.count()>0? true : false;
	}
	
	public int wordRemoveWith(String dicId, String word, String synonymity){
		return session.createQuery().aradonGroupId(DIC_WORD, HashFunction.hashGeneral(ProcedureHelper.getUID(word, synonymity))).remove();
	}
	
	public int wordRemoveAllWith(String dicId){
		return session.createQuery().aradonGroup(DIC_WORD).eq(DicId, dicId).remove();
	}
	
	public Rows wordAllListBy(String dicId){
		NodeCursor cursor = session.createQuery().aradonGroup(DIC_WORD).eq(DicId, dicId).ascending(Word).find();
		return fromCursor(cursor, Word, Synonymity);
	}
	
	public Rows wordListBy(String dicId, int listNum, int pageNo, int screenCount){
		NodeScreen screen =  session.createQuery().aradonGroup(DIC_WORD).eq(DicId, dicId).ascending(Word).find().screen(PageBean.create(listNum, pageNo, screenCount)) ;
		return fromScreen(screen, "cnt", Word, Synonymity);
	}
	
	public Rows wordSearchBy(String dicId, String searchKey, int listNum, int pageNo, int screenCount){
		NodeCursor cursor = session.createQuery().aradonGroup(DIC_WORD).eq(DicId, dicId).eq(LWord, searchKey.toLowerCase()).ascending(Word).find();
		NodeScreen screen =  cursor.screen(PageBean.create(listNum, pageNo, screenCount));

		return fromScreen(screen, "cnt", Word, Synonymity);
	}
	
	public Rows synonymitySearchBy(String repoId, String searchKey){
		String dicId = getDicId(repoId, Synonymity);
		if(StringUtil.isBlank(dicId)){
			return blankRows(Word);
		}
		
		ICursor cursor = session.createQuery().aradonGroup(DIC_WORD).eq(DicId, dicId).eq(LWord, searchKey.toLowerCase()).find();
		return fromCursor(cursor, Word, Synonymity);
	}
	
	public Rows stopWordListBy(String repoId){
		String dicId = getDicId(repoId, "stopword");
		if(StringUtil.isBlank(dicId)){
			return blankRows(Word);	
		}
		
		ICursor cursor = session.createQuery().aradonGroup(DIC_WORD).eq(DicId, dicId).ascending(Word).find();
		return fromCursor(cursor, Word);
	}
	
	public Rows endWordListBy(String repoId){
		String dicId = getDicId(repoId, "endword");
		if(StringUtil.isBlank(dicId)){
			return blankRows(Word);	
		}
		
		ICursor cursor = session.createQuery().aradonGroup(DIC_WORD).eq(DicId, dicId).ascending(Word).find();
		return fromCursor(cursor, Word);
	}
	
	private String getDicId(String repoId, String dicType){
		Node repo = session.createQuery().aradonGroupId(REPOSITORY, repoId).findOne();
		if(repo == null) return "";
		
		String dicId = repo.getString(dicType);
		return dicId;
	}
	
	public Rows synonymityCacheListBy(){
		ICursor cursor = session.createQuery().aradonGroup(DICTIONARY).eq(DicType, Synonymity).ascending(DicId).find();

		List<PropertyQuery> querys = ListUtil.newList();
		while(cursor.hasNext()){
			querys.add(PropertyQuery.createByAradon(DIC_WORD).put(DicId, cursor.next().getString(DicId)));
		}
		
		ICursor result = session.createQuery().or( querys.toArray(new PropertyQuery[0])).ascending(DicId, Word, Synonymity).find();
		return fromCursor(result,DicId, Word, Synonymity);
	}
}
