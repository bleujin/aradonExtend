package net.ion.radon.impl.let.mongo;

import java.sql.Date;

import net.ion.framework.util.ChainMap;
import net.ion.framework.util.MapUtil;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;

public class Repository {

	private Session session ;
	
	public Repository(Session session){
		this.session = session ;
		this.session.changeWorkspace("repository") ;
	}
	
	public int clearWith(){
		session.dropWorkspace() ;
		return 1 ; 
	}
	
	public int createWith(String repoId, String repoNm, String repoExp, String analyzerCd, String logLvlCd,
							String synonymityId, String stopWordId, String endWordId){
		Node node = session.newNode(repoId);
		
		ChainMap<String, ? extends Object> props = MapUtil.<String, Object>chainMap().put("repoId", repoId).put("repoNm", repoNm).put("repoExp", repoNm).put("analyzerCd", repoNm).
			put("repoNm", repoNm).put("logLvlCd", repoNm).put("synonymityId", repoNm).put("stopWordId", repoNm).put("endWordId", repoNm).
			put("regDate", new Date(System.currentTimeMillis()));
		node.putAll(props);
		
		return session.commit();
	}
	
	public int updateWith(String repoId, String repoNm, String repoExp, String analyzerCd, String logLvlCd,
						String synonymityId, String stopWordId, String endWordId){
						
		Node found = session.createQuery().path(repoId).findOne() ;
		if (found == null) return 0 ;
		found.put("repoId", repoId);
		found.put("repoNm", repoNm);
		found.put("repoExp", repoNm);
		found.put("analyzerCd", repoNm);
		found.put("repoNm", repoNm);
		found.put("logLvlCd", repoNm);
		found.put("synonymityId", repoNm);
		found.put("stopWordId", repoNm);
		found.put("endWordId", repoNm);

		return session.commit();
	}
	
	public int removeWith(String repoId){
		return session.createQuery().eq("repoId", repoId).remove() ;
	}
	
	
	

}
