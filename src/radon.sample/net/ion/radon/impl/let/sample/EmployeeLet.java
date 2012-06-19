package net.ion.radon.impl.let.sample;

import java.sql.SQLException;
import java.util.Map;

import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.handlers.BeanHandler;
import net.ion.framework.db.procedure.IQueryable;
import net.ion.framework.db.procedure.IUserProcedure;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.param.MyParameter;

import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;

public class EmployeeLet extends AbstractServerResource {
	
	@Get
	public Employee getEmployee() throws SQLException{

		int empno = Integer.parseInt(getInnerRequest().getAttribute("empno"));
		IDBController dc = getDBController();
		Rows rows = dc.createUserProcedure("emp@viewEmp(:empno)").addParam("empno", empno).execQuery() ;
		return (Employee)rows.toHandle(new BeanHandler(Employee.class)) ;
	}
	
	@Put
	protected Employee insEmployee() throws SQLException{
		Employee emp = getEmployeeBean();
		IDBController dc = getDBController();
		IUserProcedure upt = dc.createUserProcedure("emp@addEmp(:empno,:ename,:desc)");
		upt.addParam("empno", emp.getEmpNo()).addParam("ename", emp.getEname()).addParam("desc", emp.getDesc()) ;
		
		upt.execUpdate() ;
		return emp ;
	}

	@Delete
	protected int delEmployee() throws SQLException {
		int empno = Integer.parseInt(getInnerRequest().getAttribute("empno"));
		
		IDBController dc = getDBController();
		IQueryable upt = dc.createUserProcedure("emp@delEmp(:empno)").addParam("empno", empno);
		upt.execUpdate() ;
		
		return empno ;
	}

	@Post
	protected Employee modEmployee() throws ResourceException, SQLException {

		Employee emp = getEmployeeBean();
		IDBController dc = getDBController();
		IUserProcedure upt = dc.createUserProcedure("emp@modEmp(:empno,:ename,:desc)");
		upt.addParam("empno", emp.getEmpNo()).addParam("ename", emp.getEname()).addParam("desc", emp.getDesc()).execUpdate() ;
		
		return emp ;
	}

	private Employee getEmployeeBean() {
		Map<String, Object> params = getInnerRequest().getGeneralParameter() ;
		return MyParameter.create(params).toBean(Employee.class) ;
	}

	private IDBController getDBController() {
		return getContext().getAttributeObject(H2DB.class.getCanonicalName(), H2DB.class).getIDBController();
	}

	public String toString() {
		return this.getClass().getCanonicalName();
	}

}
