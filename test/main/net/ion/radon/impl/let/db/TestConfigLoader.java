package net.ion.radon.impl.let.db;

import java.io.File;
import java.io.FileReader;

import junit.framework.TestCase;
import net.ion.framework.parse.html.HTag;
import net.ion.framework.util.Debug;
import net.ion.radon.core.PageBean;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.param.MyParameter;

public class TestConfigLoader extends TestCase{
	
	
	String param = "{procedures:[{" + 
    "	id:'employee_viewdept', execType='query', " + 
    "	querylist:[ " +
	"		{	 " +
	"        	sql:'Sample@viewEmployeeByDept(:empno,:deptno)', " + 
    "			type:'procedure', " +
    "			parameter:[ " +
    "				{ name:'empno', type:'INTEGER' }, " +
    "				{ name:'deptno', type:'INTEGER' } " +
    "			] " +
    "		} " +
    "	] " +
    "}]}" ;
	
	public void testXMLParsing() throws Exception {
		XMLConfig config = XMLConfig.create(new File("./resource/imsi/tables.xml")) ;
		Debug.debug(config.firstChild("database").firstChild("tables").firstChild("table").getAttributeValue("tableType")) ;
	}

	
	public void testHTagXMLParsing() throws Exception {
		HTag tag = HTag.createGeneral(new FileReader(new File("./resource/imsi/tables.xml")), "root") ;
		Debug.debug(tag.getChild("/database/tables[0]/table[0]").getAttributeValue("tableType")) ;
		
	}
	
	
	public void testJSON() throws Exception {
		String parameter = "{query:\"¾ÆÀÌ¿Â\", sort:\"\", requestfilter:\"key:value AND range:[200 TO 300]\", page:{pageNo:3, listNum:10, screenCount:20}, color:[\"red\",\"white\",\"blue\"], param:{query:3, siteid:\"scat\"}}";
		
		MyParameter mp = MyParameter.create(parameter) ;
		Debug.debug(mp.getParam("page.pageNo")) ;
		Debug.debug(mp.getParams("color")[0]) ;
		
		PageBean pb = (PageBean)mp.childParameter("page").toBean(PageBean.class) ;
		
		Debug.debug(pb) ;
	}

	public void testReflect() throws Exception {
		MyParameter mp = MyParameter.create(param) ;
		Query pb = (Query) mp.toBean(Query.class) ;
		Param p1 = pb.getParameter()[0] ;
		//Debug.debug(pb.getId(), pb.getName(), pb.getParameter()[0], p1.getName(), p1.getType()) ;
	}
	
	public void testJSONObject() throws Exception {
		MyParameter mp = MyParameter.create(param);
		ProcedureGroup gp =  (ProcedureGroup) mp.toBean(ProcedureGroup.class);
		//Procedure pb = gp.getProcedures()[0];
		//.debug(pb.getId(), pb.getName(), pb.getParameter());

//		JSONObject jo = new JSONObject(param);
//		JSONArray array = jo.getJSONArray("params");
//		Debug.debug(array.length(), array);
	}

	 
	
}

