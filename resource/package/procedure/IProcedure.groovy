package procedure

import javax.swing.DebugGraphics;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.IPropertyFamily;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;
import net.ion.radon.repository.myapi.ICursor;
import net.ion.radon.repository.Node;
import net.ion.framework.util.ObjectUtil;

abstract class IProcedure {
	
	private static final String WORKSPACE = "icsstest";
	private Session session ;
	private Queryable query ;
	
	public final void init(Session session, Queryable query){
		this.session = session ;
		this.query = query ;
		this.session.changeWorkspace(WORKSPACE);
		initSelf() ;
	}
	
	protected void initSelf() {
	}
	

	protected Session getSession(){
		return session ;
	}
	
	protected Queryable getQuery(){
		return query;
	}
	
	public int dropWorkspace(){
		session.dropWorkspace();
		return 1;
	}
	

	protected Node findOne(IPropertyFamily props){
		return session.findOne(props) ;
	}
	
	protected Rows fromCursor(ICursor cursor, String... columns){
		return fromCursor (cursor, NodeColumns.create(columns)) ;
	}
	
	protected Rows fromNode(Node node, String... columns){
		return fromNode (node, NodeColumns.create(columns)) ;
	}
	
	protected Rows fromScreen(NodeScreen screen, String screenColName, String... columns){
		NodeColumns cols = NodeColumns.create(columns);
		cols.setScreenColName(screenColName);
		return fromScreen(screen, cols) ;
	}
	
	protected Rows fromList(List<Node> list,String... columns){
		return fromList(list,  NodeColumns.create(columns)) ;
	}
	
	protected Rows fromCursor(ICursor cursor, NodeColumns columns){
		return NodeRows.createByCursor (query, cursor, columns) ;
	}
	
	protected Rows fromNode(Node node, NodeColumns columns){
		return NodeRows.createByNode (query, node, columns) ;
	}
	
	protected Rows fromScreen(NodeScreen screen, NodeColumns columns){
		return NodeRows.createByScreen(query, screen, columns) ;
	}
	
	protected Rows fromList(List<Node> list, NodeColumns columns){
		return NodeRows.createByList(query, list, columns) ;
	}
	
	protected Rows blankRows(NodeColumns columns){
		return NodeRows.makeBlankRows(query, columns);	
	}

	
	protected Node loadBigBoy(String bigBoyName){
		Node found = getSession().createQuery().findByPath(bigBoyName) ;
		if (found == null){
			found = getSession().newNode(bigBoyName) ;
			found.setAradonId ("bigboy", bigBoyName) ;
		}
		return found ;
	}	
	
	protected int commit(){
		return getSession().commit() ;
	}
	

}
