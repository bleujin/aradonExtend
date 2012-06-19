package net.ion.radon.impl.let.mongo;

import java.util.Date;

import net.ion.framework.util.Debug;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.TestBaseRepository;

public class TestInternal extends TestBaseRepository{

	public void testInfoIntenal() throws Exception {
		
		session.newNode("kor").put("analyzerCd", "kor").put("analyzerNm", "Korea").setAradonId("analyzerGroup", "kor");
		session.newNode("end").put("analyzerCd", "eng").put("analyzerNm", "English").setAradonId("analyzerGroup", "eng");
		session.commit();
		
		Node node = session.newNode("test");
		node.put("repoId", "test") ;
		node.put("repoNm", "testRepoNm") ;
		node.put("repoExp", "description") ;
		node.put("analyzerCd", "kor") ;
		node.put("logLvlCd", "nor") ;
		node.put("synonymityId", "") ;
		node.put("stopWordId", "") ;
		node.put("endWordId", "") ;
		node.put("regDate", new Date()) ;
		
		session.commit() ;
		
		Node result = session.createQuery().aradonGroupId("repoid", "test").findOne();
		Debug.debug(result);
		
	}
}
