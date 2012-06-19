package procedure

import java.sql.Date;
import net.ion.framework.db.Rows;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;
import net.ion.radon.repository.PropertyComparator;
import static procedure.GroupConstants.*;
import static procedure.table.AdminTable.*;
import static procedure.table.SuggestTable.SuggId;
import static procedure.table.SuggestTable.SuggNm;
import static procedure.table.ScheduleTable.*;


class Admin extends IProcedure{
	public Admin(){}
	public void initSelf() {}
	
	public Rows adminTreeBy(){
		NodeCursor  suggCursor = session.createQuery().aradonGroup(SUGGEST).descending(RegDate).find();
		Rows suggRows = fromCursor(suggCursor, SuggId+" "+ CatId , "'suggest' "+UpperCatId, SuggNm+" "+CatNm, "'suggest' "+Type);
		
		NodeCursor scheCursor = session.createQuery().aradonGroup(SCHEDULE).descending(ScheType, TargetId, RegDate).find();
		Rows scheRows = fromCursor(scheCursor, "tochar(scheId) "+CatId, "'auto_grp' "+ UpperCatId, ScheNm+" "+CatNm, "'auto' "+ Type);
		
		return NodeRows.unionAll(scheRows, suggRows);
	}
	
	public int connCreateWith(String connId, String connNm, String connCont, String connExp){

		Node node = session.newNode(connId).setAradonId(CONNECTION, connId);
		node.put(ConnId, connId).put(ConnNm, connNm).put(ConnCont, connCont).put(ConnExp, connExp).put(RegDate, new Date(System.currentTimeMillis()));
		return session.commit();
	}
	
	public int connUpdateWith(String connId, String connNm, String connCont, String connExp){
		Node connNode = session.createQuery().aradonGroupId(CONNECTION, connId).findOne();
		if(connNode == null) return 0;
		
		connNode.put(ConnNm, connNm).put(ConnCont, connCont).put(ConnExp, connExp);
		return session.commit();
	}
	
	public int connRemoveWith(String connId){
		return session.createQuery().aradonGroupId(CONNECTION, connId).remove();
	}
	
	public Rows connListBy(int listNum, int pageNo, int screenSize){
		NodeScreen screen = session.createQuery().aradonGroup(CONNECTION).descending(RegDate).find().screen(PageBean.create(listNum, pageNo, screenSize));
		return fromScreen(screen, "cnt", ConnId, ConnNm, ConnCont, ConnExp, "tochar(regDate,'yyyy-MM-dd HH:mm:ss') regDate" );
	}
	
	public Rows connInfoBy(String connId){
		Node connNode = session.createQuery().aradonGroupId(CONNECTION, connId).findOne();
		return fromNode(connNode, ConnId, ConnNm, ConnCont, ConnExp, "tochar(regDate,'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public int fgroupCreateWith(String fgroupId, String fgroupNm){
		Node node = session.newNode().setAradonId(FGROUP, fgroupId).put(FGroupId, fgroupId).put(FGroupNm, fgroupNm).put(ConnExp, new Date(System.currentTimeMillis()));
		return session.commit();
	}
	
	public int fgroupUpdateWith(String fgroupId, String fgroupNm){
		Node fNode = session.createQuery().aradonGroupId(FGROUP, fgroupId).findOne(); 
		if(fNode == null) return 0;
		
		fNode.put(FGroupNm, fgroupNm);
		return session.commit();
	}
	
	public int fgroupRemoveWith(String fgroupId){
		return session.createQuery().aradonGroupId(FGROUP, fgroupId).remove();
	}
	
	public Rows fgroupListBy(){
		ICursor cursor = session.createQuery().aradonGroup(FGROUP).descending(RegDate).find();
		return fromCursor(cursor, FGroupId, FGroupNm, "tochar(regDate, 'yyyy-MM-dd HH:mm:ss')");
	}
	
	public Rows fgroupRetrieveBy(String fgroupId){
		Node node = session.createQuery().aradonGroupId(FGROUP, fgroupId).findOne();
		return fromNode(node, FGroupId, FGroupNm, "tochar(regDate, 'yyyy-MM-dd HH:mm:ss')");
	}
	
	public int fieldCreateWith(CharSequence[] fgroupIds, String[] fieldIds){
		int insertCount = 0;
		fgroupIds.eachWithIndex { it, i ->
			Node fgNode = session.createQuery().aradonGroupId(FGROUP, it).findOne();
			if(fgNode != null){
				Node node = fgNode.createChild(fieldIds[i]).setAradonId(FGROUP_DETAIL , fieldIds[i])
				node.put(FGroupId, it).put(FieldId, fieldIds[i]).put(SerNo, fgNode.getChild().count()+1);
				insertCount += session.commit();
			}
		}
		return insertCount;
	}
	
	public int fieldRemoveWith(String fgroupId){
		Node fgNode = session.createQuery().aradonGroupId(FGROUP, fgroupId).findOne();
		if(fgNode == null) return 0;
		
		return fgNode.removeChild().size();
	}
	
	public Rows fieldListBy(String fgroupId){
		Node fgNode = session.createQuery().aradonGroupId(FGROUP, fgroupId).findOne();
		if(fgNode == null) return fromNode(fgNode, FGroupId, FieldId, SerNo);
		
		List<Node> list = fgNode.getChild().toList(PageBean.ALL, PropertyComparator.newDescending(SerNo));
		return fromList(list, FGroupId, FieldId, SerNo);
	}
	
	public Rows cacheListBy(){
		ICursor cursor =  session.createQuery().aradonGroup(FGROUP_DETAIL).descending(FGroupId, SerNo).find();
		return fromCursor(cursor, FGroupId, FieldId);
	}

}
