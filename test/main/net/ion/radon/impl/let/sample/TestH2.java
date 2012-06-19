package net.ion.radon.impl.let.sample;

import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;
import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.procedure.H2EmbedDBManager;
import net.ion.framework.db.procedure.HSQLBean;
import net.ion.framework.db.procedure.IUserCommand;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;

import org.h2.jdbcx.JdbcConnectionPool;

public class TestH2 extends TestCase{

	public void testConnect() throws Exception {
		IDBController dc = createIDBController();
		
		dc.createUserCommand("drop table if exists board").execUpdate() ;
		IUserCommand cmd = dc.createUserCommand("create table board (groupid varchar(40), seqno int, subject varchar(400), creuserid varchar(20), credate date, attribute varchar(20000))") ;
		cmd.execUpdate() ;
		
		// dc.createUserCommand("insert into board values(1, 'bleujin')").execUpdate() ;
		dc.destroySelf() ;
	}
	
	
	public void testSelect() throws Exception {
		IDBController dc = createIDBController();
		
		dc.createUserCommand("insert into board(groupid, subject) values('111', '222')").execUpdate() ;
		Rows rows = dc.createUserCommand("select * from board").execQuery() ;
		
		Debug.line(rows) ;
		dc.destroySelf() ;
	}
	
	

	private IDBController createIDBController() throws SQLException {
		HSQLBean bean = new HSQLBean();
		bean.setUserId("sa") ;
		bean.setUserPwd("") ;
		bean.setAddress("jdbc:h2:/aradon/data/db") ;
		DBManager dbm = new H2EmbedDBManager(bean) ;
		IDBController dc = new DBController(dbm) ;
		dc.initSelf() ;
		return dc ;
	}
	
	
	public void xtestJDBCConnect() throws Exception {
		JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:mem:test", "sa", "sa");
		for (int i : ListUtil.rangeNum(10)) {
			Connection conn = cp.getConnection();
			
			
			conn.close();
		}
		cp.dispose();
	}

}
