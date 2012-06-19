package procedure

import net.ion.framework.db.Rows;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;
import static procedure.GroupConstants.SECTION;
import static procedure.table.SectionTable.*;

class Section extends IProcedure{
	
	public final static String REF_REPO_SECTION = "r_repo_section";

	public Section(){}
	public void initSelf() {}
	
	
	
	public int createWith(CharSequence[] repoIds, String[] secIds, String[] secNms){
		int insertCount = 0;
		repoIds.eachWithIndex { it, i ->
			Node node = session.newNode().setAradonId(SECTION , ProcedureHelper.getUID(it, secIds[i]));
			node.put(RepoId, it).put(SecId, secIds[i]).put(SecNm, secNms[i]);
			node.put(OrderNo, ProcedureHelper.getIncrementNo("orderNo", session.createQuery().aradonGroup(SECTION).eq(RepoId,it))) ;
			insertCount += session.commit();
		}
		return insertCount;
	}

	public int removeWith(String repoId){
		return session.createQuery().aradonGroup(SECTION).eq(RepoId, repoId).remove();
	}
	
	public Rows listBy(String repoId){
		NodeCursor nc = session.createQuery().aradonGroup(SECTION).eq(RepoId, repoId).ascending(RepoId, OrderNo).find();
		return fromCursor(nc, SecId, SecNm, OrderNo, Sort, RepoId);
	}
	
	public Rows sortListBy(String repoId, String secId){
		Node node = session.createQuery().aradonGroupId(SECTION, ProcedureHelper.getUID(repoId, secId)).findOne();
		if(node == null) return blankRows(Sort);
		
		return fromNode(node, Sort);
	}
	
	public int modifySortWith(String repoId, String secId, String sort){
		Node node = session.createQuery().aradonGroupId(SECTION, ProcedureHelper.getUID(repoId, secId)).findOne();
		if(node == null) return 0;
		
		node.put(Sort, sort);
		return commit();
	}
	
	public Rows allListBy(){
		NodeCursor nc = session.createQuery().aradonGroup(SECTION).ascending(RepoId, OrderNo).find();
		return fromCursor(nc, RepoId, SecId, SecNm, OrderNo, Sort);
	}
}
