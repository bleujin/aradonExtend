package net.ion.heeya;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.IUserProcedure;
import net.ion.framework.util.Debug;

public class TestWrongTypeProcedure extends DBTestCaseHeeya{


	public void testView() throws Exception {
		IUserProcedure upt = dc.createUserProcedure("Sample@listEmployeeBy(?)");
		upt.addParam(10);
		Rows rows = upt.execQuery();
		Debug.line(rows.getRowCount());
	}
	
	
	public void testname() throws Exception {
		IUserProcedure upt = dc.createUserProcedure("Sample@addEmployeeWith(?,?,?,?,?,?,?,?)") ;
		upt.addParam(7879);
		upt.addParam("heeya");
		upt.addParam("job");
		upt.addParam(7654);
		upt.addParam(20101010);
		upt.addParam(100);
		upt.addParam(200);
		upt.addParam(10);
		int result = upt.execUpdate();
		Debug.line(result, result) ;
 	}
	
}
