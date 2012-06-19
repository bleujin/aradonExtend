package procedure

import java.util.List;

import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeRows;
import net.ion.framework.db.Rows;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyFamily;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.Session;


public class Dept extends IProcedure {

	private NodeColumns selectColumns = NodeColumns.create("deptno","dname", "loc");
	
	public Dept(){
	}

	protected void initSelf() {
		getSession().changeWorkspace "dept" ;
	}

	public int clearWith(){
		getSession().dropWorkspace() ;
		return 1 ; 
	}
	
	public int createWith(int deptno, String dname, String loc){
		Node node = getSession().newNode();
		node.put("deptno", deptno);
		node.put("dname", dname);
		node.put("loc", loc);
		
		return getSession().commit();
	}

	public int createBatchWith(Object[] deptno, Object[] dname, Object[] loc){
		for (int i = 0; i < loc.size() ; i++) {
			Node node = getSession().newNode(String.valueOf(deptno.getAt(i)));
			node.put("deptno", deptno.getAt(i));
			node.put("dname", dname.getAt(i));
			node.put("loc", loc.getAt(i));
		}
		return 1;
	}


	public int removeWith(int deptno){
		return getSession().remove(PropertyQuery.create("deptno", deptno)) ;
	}
	
	public Rows infoBy(int deptno){
		Node result = findOne(PropertyQuery.create("deptno", deptno));
		return createRows(result, "deptno","dname", "loc");
	}
	
	public Rows listAllBy(){
		NodeCursor ncursor = getSession().find().sort(PropertyFamily.create("deptno", 1));
		def columns = NodeColumns.create("deptno", "dname.444", "loc")
		columns.setScreenColName("cnt") ;

		return super.createRows(ncursor, columns);

	}
	
	public Rows listBy(int listNum, int pageNo, int screenSize){
		NodeScreen screen = getSession().find().sort(PropertyFamily.create("deptno", 1)).screen(PageBean.create(listNum, pageNo, screenSize)) ;
		def columns = NodeColumns.create("deptno", "dname", "loc")
		columns.setScreenColName("cnt") ;

		return super.createRows(screen, columns);
	}
	
	

}