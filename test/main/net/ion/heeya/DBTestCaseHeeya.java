package net.ion.heeya;

import junit.framework.TestCase;
import net.ion.framework.db.DBController;
import net.ion.framework.db.manager.OracleDBManager;
import net.ion.framework.db.servant.StdOutServant;

public class DBTestCaseHeeya extends TestCase{
	public DBTestCaseHeeya() {
	}

	protected void setUp() throws Exception {
		super.setUp();
		net.ion.framework.db.manager.DBManager dbm = new OracleDBManager(
				"jdbc:oracle:thin:@dev-test.i-on.net:1521:devTest", "bleu","redf");
		dc = new DBController("Default", dbm);
		dc.addServant(new StdOutServant());
		dc.initSelf();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dc.destroySelf();
	}

	protected static DBController dc = null;
}
