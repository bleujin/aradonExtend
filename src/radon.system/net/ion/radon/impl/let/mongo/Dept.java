package net.ion.radon.impl.let.mongo;

import java.sql.Types;

import net.ion.framework.db.FakeRows;
import net.ion.framework.db.Rows;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;

public class Dept  {

	private Session session ;
	public Dept(Session session){
		this.session = session ;
		this.session.changeWorkspace("dept") ;
	}
	
	public int createWith(int deptno, String dname, String loc){
		
		Node node = session.newNode(String.valueOf(deptno));
		node.put("deptno", deptno);
		node.put("dname", dname);
		node.put("loc", loc);
		return session.commit();
	}

	public int removeWith(int deptno){
		return session.createQuery().eq("deptno", deptno).remove() ;
	}
	
	public Rows infoBy(int deptno){
		Node result = session.createQuery().eq("deptno", deptno).findOne();
		
		FakeRows rows = new FakeRows() ;
		rows.addColumn("deptno", Types.INTEGER) ;
		rows.addColumn("dname", Types.VARCHAR) ;
		rows.addColumn("loc", Types.VARCHAR) ;
		rows.addRow(result.toMap()) ;
		
		return rows ;
	}

}
