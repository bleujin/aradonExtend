package net.ion.im.bbs.test;

import junit.framework.TestCase;
import net.ion.framework.db.DBController;
import net.ion.framework.db.mysql.MySQLDBManager;
import net.ion.framework.db.servant.StdOutServant;

public class DBTestCase extends TestCase {
	public DBTestCase() {
	}

	protected void setUp() throws Exception {
		super.setUp();
		net.ion.framework.db.manager.DBManager dbm = new MySQLDBManager(
				"jdbc:mysql://61.250.201.66:3306/im", "root","apmsetup");
		dc = new DBController("MySQL", dbm);
		dc.addServant(new StdOutServant());
		dc.initSelf();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		dc.destroySelf();
	}

	protected static DBController dc = null;
}
