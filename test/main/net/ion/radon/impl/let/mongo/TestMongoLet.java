package net.ion.radon.impl.let.mongo;

import net.ion.framework.db.AradonDBManager;
import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.IONXmlWriter;
import net.ion.framework.db.Page;
import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserProcedure;
import net.ion.framework.db.procedure.IUserProcedureBatch;
import net.ion.framework.db.procedure.IUserProcedures;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.Options;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.AradonServer;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.impl.section.PathInfo;
import net.ion.radon.repository.RepositoryCentral;

public class TestMongoLet extends TestAradonExtend {
	
	protected IDBController remoteDc;

	public void setUp() throws Exception {
		super.setUp();
		initAradon();

		init(aradon);
	}
	
	public void init(Aradon aradon) throws Exception{
		this.remoteDc = new DBController(AradonDBManager.create(aradon));
		remoteDc.initSelf();

		SectionService ss = aradon.attach("rdb", XMLConfig.BLANK);
		ss.getServiceContext().putAttribute("my.mongodb.id", RepositoryCentral.create("61.250.201.157", 27017));
		ss.attach(PathInfo.create("query", "/query", QueryLet.class));
		ss.attach(PathInfo.create("update", "/update", UpdateLet.class));

		remoteDc.createUserProcedure("dept@clearWith()").execUpdate();
	}
	
	public void xtestStartAradon() throws Exception {
		AradonServer server = new AradonServer(new Options(new String[]{"-config:resource/config/readonly-config.xml"})) ;
		
		Aradon aradon = server.start() ;
		init(aradon) ;
		aradon.startServer(9002) ;
		new InfinityThread().startNJoin() ;
	}
	
	
	public void testUpdate() throws Exception {

		IUserProcedure upt = remoteDc.createUserProcedure("dept@createWith(?,?,?)");
		upt.addParam(10).addParam("dev").addParam("seoul");

		int result = upt.execUpdate();
		assertEquals(1, result);
	}

	public void testQuery() throws Exception {
		IUserProcedure upt = remoteDc.createUserProcedure("dept@createWith(?,?,?)");
		upt.addParam(10).addParam("dev").addParam("seoul");

		int result = upt.execUpdate();

		Rows node = (Rows) remoteDc.createUserProcedure("dept@infoBy(?)").addParam(10).execQuery();

		node.first();
		assertEquals(10, node.getObject("deptno"));
	}
	
	
	public void testNextPage() throws Exception {
		IUserProcedureBatch upts = remoteDc.createUserProcedureBatch("dept@createBatchWith(?,?,?)");
		for (int i = 0; i < 10; i++) {
			upts.addBatchParam(0, i * 10);
			upts.addBatchParam(1, RandomUtil.nextRandomString(10));
			upts.addBatchParam(2, RandomUtil.nextRandomString(10));
		}
		int result = upts.execUpdate();

		final IUserProcedure proc = remoteDc.createUserProcedure("dept@listAllBy()");
		proc.setPage(Page.create(5, 1)) ;
		
		Rows rows = (Rows) proc.execQuery();
		rows.setXmlWriter(new IONXmlWriter()) ;
		Debug.debug(rows) ;
		
	}
	
	public void testPageQuery() throws Exception {
		IUserProcedureBatch upts = remoteDc.createUserProcedureBatch("dept@createBatchWith(?,?,?)");
		for (int i = 0; i < 10; i++) {
			upts.addBatchParam(0, i * 10);
			upts.addBatchParam(1, RandomUtil.nextRandomString(10));
			upts.addBatchParam(2, RandomUtil.nextRandomString(10));
		}
		int result = upts.execUpdate();

		Rows rows = (Rows) remoteDc.createUserProcedure("dept@listBy(?,?,?)").addParam(2).addParam(2).addParam(2).execQuery();

		assertEquals(2, rows.getRowCount());
		assertEquals(5, rows.firstRow().getInt("cnt"));
		assertEquals(20, rows.firstRow().getInt("deptno"));

		remoteDc.createUserProcedure("dept@clearWith()").execUpdate();

	}

	public void testSpeed() throws Exception {
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			IUserProcedure upt = remoteDc.createUserProcedure("dept@createWith(?,?,?)");
			upt.addParam(i).addParam("dev").addParam("seoul");
			int result = upt.execUpdate();
		}
		Debug.line(System.currentTimeMillis() - start);

		start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			remoteDc.createUserProcedure("dept@infoBy(?)").addParam(10).execQuery();
		}
		Debug.line(System.currentTimeMillis() - start);
	}

	public void testUpdateProceudres() throws Exception {
		IUserProcedures upts = remoteDc.createUserProcedures("upts") ;
		
		upts.add(remoteDc.createUserProcedure("dept@clearWith()"));
		upts.add(remoteDc.createUserProcedure("dept@createWith(?,?,?)").addParam(10).addParam("dev").addParam("seoul"));

		int result = upts.execUpdate();
		assertEquals(2, result);
	}

	public void testQuerysProceudres() throws Exception {
		
		remoteDc.createUserProcedure("dept@createWith(?,?,?)").addParam(10).addParam("dev").addParam("seoul").execUpdate() ;
		
		IUserProcedures upts = remoteDc.createUserProcedures("upts") ;
		upts.add(remoteDc.createUserProcedure("dept@infoBy(?)").addParam(10));
		upts.add(remoteDc.createUserProcedure("dept@infoBy(?)").addParam(10));
		
		Rows rows = upts.execQuery();
		assertEquals(1, rows.getRowCount());
		assertEquals(1, rows.getNextRows().getRowCount());
	}
	
	public void testQuerysProceudres2() throws Exception {
		remoteDc.createUserProcedure("dept@createWith(?,?,?)").addParam(10).addParam("dev").addParam("seoul").execUpdate() ;
		
		IUserProcedures inner = remoteDc.createUserProcedures("upts inner") ;
		inner.add(remoteDc.createUserProcedure("dept@infoBy(?)").addParam(10));
		
		IUserProcedures outer = remoteDc.createUserProcedures("upts outer") ;
		outer.add(remoteDc.createUserProcedure("dept@infoBy(?)").addParam(10));
		outer.add(inner);
		
		Rows rows = outer.execQuery();
		assertEquals(1, rows.getRowCount());
		assertEquals(1, rows.getNextRows().getRowCount());
	}
	
	
	

}
