package net.ion.radon.impl.let.sample;

import java.math.BigDecimal;

public class Employee {

	private int empNo;
	private String ename;
	private String desc;
	private int mgr;
	private String hireDate;
	private BigDecimal sal;
	private int comm;
	private int deptNo;

	public static Employee create(int empNo, String ename, String desc) {
		Employee newEmp = new Employee() ;
		newEmp.setEmpNo(empNo) ;
		newEmp.setEname(ename) ;
		newEmp.setDesc(desc) ;
		return newEmp;
	}

	
	public int getEmpNo() {
		return empNo;
	}

	public void setEmpNo(int empNo) {
		this.empNo = empNo;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getMgr() {
		return mgr;
	}

	public void setMgr(int mgr) {
		this.mgr = mgr;
	}

	public String getHireDate() {
		return hireDate;
	}

	public void setHireDate(String hireDate) {
		this.hireDate = hireDate;
	}

	public BigDecimal getSal() {
		return sal;
	}

	public void setSal(BigDecimal sal) {
		this.sal = sal;
	}

	public int getComm() {
		return comm;
	}

	public void setComm(int comm) {
		this.comm = comm;
	}

	public int getDeptNo() {
		return deptNo;
	}

	public void setDeptNo(int deptNo) {
		this.deptNo = deptNo;
	}


}
