package net.ion.radon.impl.let.sample;

import java.sql.SQLException;
import java.util.List;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.handlers.BeanListHandler;
import net.ion.framework.db.procedure.IUserProcedure;
import net.ion.radon.core.PageBean;
import net.ion.radon.core.let.AbstractServerResource;

import org.restlet.resource.Get;

public class EmployeeListLet  extends AbstractServerResource {

	@Get
	public List<Employee> list() throws SQLException{
		IDBController dc = getDBController();
		PageBean page = getInnerRequest().getAradonPage() ;
		IUserProcedure upt = dc.createUserProcedure("emp@listEmp()");
		upt.setPage(page.toPage()) ;
		
		Rows rows = upt.execQuery() ;
		return (List<Employee>)rows.toHandle(new BeanListHandler(Employee.class)) ;
	}
	
	
	private IDBController getDBController() {
		return getContext().getAttributeObject(H2DB.class.getCanonicalName(), H2DB.class).getIDBController();
	}
}
