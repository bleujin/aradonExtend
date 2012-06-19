package net.ion.radon.impl.let.board;

import junit.framework.TestCase;
import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.handlers.JSONHandler;
import net.ion.framework.db.manager.OracleDBManager;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.ISerialRequest;
import net.ion.radon.util.AradonTester;

public class TestQueryLet extends TestCase{

	
	private IDBController dc = null ; 
	public void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		dc = new DBController(new OracleDBManager("jdbc:oracle:thin:@dev-sql.i-on.net:1521:devSql", "bleu", "redf")) ;
		dc.initSelf() ;
	}

	@Override
	protected void tearDown() throws Exception {
		dc.destroySelf() ;
		super.tearDown();
	}
	
	public void testBasicQuery() throws Exception {
		AradonTester at = AradonTester.create().register("", "/query", QueryLet.class) ;
		at.getAradon().getServiceContext().putAttribute(IDBController.class.getCanonicalName(), dc) ;
		at.startServer(9000) ;
		
		IUserCommand cmd = dc.createUserCommand("select 1 from dual") ;
		
		ISerialRequest req =  AradonClientFactory.create("http://127.0.0.1:9000").createSerialRequest("/query") ;
		
		Rows rows = req.post(cmd, Rows.class) ;
		
		assertEquals(1, rows.getRowCount()) ;
		assertEquals(1, rows.firstRow().getInt(1)) ;
	}
	

	
	public void testAradonQuery() throws Exception {
		AradonTester at = AradonTester.create().register("", "/query", QueryLet.class) ;
		at.getAradon().getServiceContext().putAttribute(IDBController.class.getCanonicalName(), dc) ;
		
		IUserCommand cmd = dc.createUserCommand("select 1 from dual") ;
		ISerialRequest req =  AradonClientFactory.create(at.getAradon()).createSerialRequest("/query") ;
		
		Rows rows = req.post(cmd, Rows.class) ;
		
		assertEquals(1, rows.getRowCount()) ;
		assertEquals(1, rows.firstRow().getInt(1)) ;
	}
	
	
	public void testJSONHandler() throws Exception {
		Rows rows = dc.createUserProcedure("sample@selectEmpBy()").execQuery() ;
		JsonObject jso = (JsonObject) rows.toHandle(new JSONHandler()) ;
		Debug.debug(jso) ;
	}

}
