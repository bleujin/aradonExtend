package net.ion.radon.core.security;

import net.ion.framework.db.DBTestCase;

public class TestJDBCVerifier extends DBTestCase {

	public void testJDBCVerifier() throws Exception {
		String query = "select ename password from emp_sample where empno = :userid";
		JDBCVerifier v = new JDBCVerifier(dc, query);

		assertTrue(v.verify("7566", "JONES".toCharArray()) != 0);
	}
}
