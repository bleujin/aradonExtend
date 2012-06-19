package net.ion.radon.impl.let.sample;

import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.db.bean.handlers.BeanHandler;
import net.ion.framework.db.procedure.HSQLBean;
import net.ion.framework.db.procedure.IUserProcedure;
import net.ion.framework.db.procedure.ProcedureBean;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.ILocation;
import net.ion.radon.core.representation.BeanToJsonFilter;
import net.ion.radon.core.representation.PlainObjectConverter;
import net.ion.radon.util.AradonTester;

import org.restlet.representation.Representation;

public class TestExmployeeLet extends TestCase{


	private AradonClient ac ;
	private H2DB h2db ;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		HSQLBean bean = createMemHSQLBean();
		this.h2db = new H2DB(bean);
		
		Aradon aradon = AradonTester.create().mergeSection("test")
			.addFilter(ILocation.AFTER, BeanToJsonFilter.create())
			.addLet("/employee", "elist", EmployeeListLet.class)
			.addLet("/employee/{empno}", "emplet", EmployeeLet.class)
			.putAttribute(H2DB.class.getCanonicalName(), h2db) 
			.getAradon() ;
		aradon.getEngine().getRegisteredConverters().add(new PlainObjectConverter()) ;
		aradon.start() ;
		
		h2db.getIDBController().createUserProcedure("emp@dropTable").execUpdate() ;
		h2db.getIDBController().createUserProcedure("emp@createTable").execUpdate() ;
		this.ac = AradonClientFactory.create(aradon) ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		h2db.getIDBController().destroySelf() ;
		super.tearDown();
	}

	public void testGet() throws Exception {
		IUserProcedure upt = h2db.getIDBController().createUserProcedure("emp@addEmp(:empno,:ename,:desc)") ;
		upt.addParam("empno",10).addParam("ename", "bleujin").addParam("desc", "aradon dev").execUpdate() ;
		
		Employee emp = ac.createJsonRequest("/test/employee/10").get(Employee.class) ;
		assertEquals(10, emp.getEmpNo()) ;
		assertEquals("bleujin", emp.getEname()) ;
		assertEquals("aradon dev", emp.getDesc()) ;
	}
	
	public void testList() throws Exception {
		IUserProcedure upt = h2db.getIDBController().createUserProcedure("emp@addEmp(:empno,:ename,:desc)") ;
		upt.addParam("empno",10).addParam("ename", "bleujin").addParam("desc", "aradon dev").execUpdate() ;
		
		List<Employee> emps = ac.createJsonRequest("/test/employee").list(Employee.class) ;
		Employee emp = emps.get(0) ;
		assertEquals(10, emp.getEmpNo()) ;
		assertEquals("bleujin", emp.getEname()) ;
		assertEquals("aradon dev", emp.getDesc()) ;
	}
	
	public void testInsert() throws Exception {
		IAradonRequest request = ac.createRequest("/test/employee/0") ;
		request.addParameter("empNo", "10") ;
		request.addParameter("ename", "bleujin") ;
		request.addParameter("desc", "aradon dev") ;
		
		Representation repr = request.put() ;
		Employee insertedEmp = JsonParser.fromString(repr.getText()).getAsJsonObject().getAsObject(Employee.class) ;
		
		assertEquals(10, insertedEmp.getEmpNo()) ;
		assertEquals("bleujin", insertedEmp.getEname()) ;
	}
	
	public void testDelete() throws Exception {
		IUserProcedure upt = h2db.getIDBController().createUserProcedure("emp@addEmp(:empno,:ename,:desc)") ;
		upt.addParam("empno",10).addParam("ename", "bleujin").addParam("desc", "aradon dev").execUpdate() ;

		IAradonRequest request = ac.createRequest("/test/employee/10") ;
		Representation repr = request.delete() ;
		
		assertEquals("10", repr.getText()) ;
	}
	
	
	public void testUpdate() throws Exception {
		IUserProcedure upt = h2db.getIDBController().createUserProcedure("emp@addEmp(:empno,:ename,:desc)") ;
		upt.addParam("empno",10).addParam("ename", "bleujin").addParam("desc", "aradon dev").execUpdate() ;
		
		IAradonRequest request = ac.createRequest("/test/employee/0") ;
		request.addParameter("empNo", "10") ;
		request.addParameter("ename", "hero") ;
		request.addParameter("desc", "dev") ;
		
		Representation repr = request.post() ;
		Employee insertedEmp = JsonParser.fromString(repr.getText()).getAsJsonObject().getAsObject(Employee.class) ;

		Employee emp = (Employee) h2db.getIDBController().createUserCommand("select * from emp where empno = 10").execQuery().toHandle(new BeanHandler(Employee.class)) ;
		assertEquals("hero", emp.getEname()) ;
	}
	
	
	
	private HSQLBean createMemHSQLBean() throws SQLException {
		HSQLBean bean = new HSQLBean();
		bean.setUserId("sa") ;
		bean.setUserPwd("") ;
		bean.setAddress("jdbc:h2:mem:emp") ;
		
		bean.addProcedure(ProcedureBean.create("emp@dropTable", "drop table if exists emp")) ;
		bean.addProcedure(ProcedureBean.create("emp@createTable", "create table if not exists emp(empno int, ename varchar(40), desc varchar(400))")) ;
		bean.addProcedure(ProcedureBean.create("emp@viewEmp(:empno)", "select * from emp where empno = :empno")) ;
		bean.addProcedure(ProcedureBean.create("emp@listEmp()", "select * from emp")) ;
		bean.addProcedure(ProcedureBean.create("emp@addEmp(:empno,:ename,:desc)", "insert into emp values(:empno,:ename,:desc)")) ;
		bean.addProcedure(ProcedureBean.create("emp@delEmp(:empno)", "delete from emp where empno = :empno")) ;
		bean.addProcedure(ProcedureBean.create("emp@modEmp(:empno,:ename,:desc)", "update emp set ename = :ename, desc = :desc where empno = :empno")) ;
		
		return bean ;
	}
}
