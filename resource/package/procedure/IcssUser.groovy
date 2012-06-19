package procedure

import java.sql.Date;

import net.ion.framework.db.Rows;
import net.ion.framework.db.procedure.Queryable;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.CipherUtil;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.PropertyQuery;
import net.ion.radon.repository.SessionQuery; 
import net.ion.radon.repository.myapi.AradonQuery;
import static procedure.GroupConstants.*;
import static procedure.table.UserTable.* ;
import net.ion.radon.repository.NodeCursor;

class IcssUser extends IProcedure{

	public int createWith(String userId, String userPwd, String userNm, String userExp, String langCd, String email,
		                  String mobileTelNo, String companyTelNo, String rduty, String acceptMailFlg, String isUse){
						  
		Node node = session.newNode().setAradonId(ICSSUSER, userId) ;
		node.put(UserId, userId.toLowerCase()).putEncrypt(UserPwd, userPwd).put(UserNm, userNm).put(UserExp, userExp)
		    .put(LangCd, langCd).put(Email, email).put(MobileTelNo, mobileTelNo).put(CompanyTelNo, companyTelNo)
			.put(RegDate, new Date(System.currentTimeMillis())).put(RDuty, rduty).put(AcceptMailFlg, acceptMailFlg).put(IsUse, isUse) ;

		return commit() ;
	}
	
	public int updateWith(String userId, String userPwd, String userNm, String userExp, String langCd, String email, 
		                  String mobileTelNo, String companyTelNo, String rduty, String acceptMailFlg, String isUse){
		Node node = session.createQuery().aradonGroupId(ICSSUSER, userId).findOne();
		if(node == null) return 0;
		
		node.putEncrypt(UserPwd, userPwd).put(UserNm, userNm).put(UserExp, userExp)
		    .put(LangCd, langCd).put(Email, email).put(MobileTelNo, mobileTelNo).put(CompanyTelNo, companyTelNo)
			.put(RegDate, new Date()).put(RDuty, rduty).put(AcceptMailFlg, acceptMailFlg).put(IsUse, isUse) ;
		return commit();
    }
	
	public int approveWith(Object[] userids){
		NodeCursor cursor = session.createQuery().aradonGroup(ICSSUSER).in(UserId, userids).find() ;
		while(cursor.hasNext()){
			cursor.next().put(IsUse, "T") ;
		}
		return commit() ;
	}
	
	public int removeWith(Object[] userids){
		return session.createQuery().aradonGroup(ICSSUSER).in(UserId, userids).remove();
	}
	
	public Rows infoBy(String userId){
		Node node = session.createQuery().aradonGroupId(ICSSUSER, userId).findOne() ;
		return fromNode(node, UserId, UserNm,  "'' userPwd", UserExp, LangCd, Email, MobileTelNo, CompanyTelNo, "toChar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate", RDuty, AcceptMailFlg, IsUse) ;
	}
	
	public Rows listBy(int listNum, int pageNo, int screenCount){
		NodeScreen ns = session.createQuery().aradonGroup(ICSSUSER).eq(IsUse, "T").descending(RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt", UserId, UserNm, Email, RDuty, MobileTelNo, CompanyTelNo, IsUse); 
	}
	
	public Rows standListBy(int listNum, int pageNo, int screenCount){
		NodeScreen ns  =session.createQuery().aradonGroup(ICSSUSER).eq(IsUse, "F").descending(RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt",  UserId, UserNm, Email, RDuty, MobileTelNo, CompanyTelNo, IsUse); 
	}
	
	public Rows searchListBy(String searchKey, int listNum, int pageNo, int screenCount){
		NodeScreen ns  = session.createQuery().aradonGroup(ICSSUSER).eq(IsUse, "T")
					     .or(PropertyQuery.create(UserId, searchKey.toLowerCase()), PropertyQuery.create(UserNm, searchKey)).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt", UserId, UserNm, Email, RDuty, MobileTelNo, CompanyTelNo, IsUse);
		
	}
	
	public Rows logInBy(String userId, String passWd, String targetUrl){
		Node node = session.createQuery().aradonGroupId(ICSSUSER, userId).findOne();
		node.put("targetUrl", targetUrl);
		node.put("isPwdMatch", CipherUtil.isMatch(passWd, (byte[])node.get(UserPwd)) ? "T" : "F" );
		node.get("isPwdMatch"); 
		node.put("connDate", new Date(System.currentTimeMillis()));
		
		return fromNode(node, 
					"nvl(decode(isPwdMatch, 'T' , decode(isUse, 'T', nvl(targetUrl, 'init'), 'noapproved'),'nopasswd'),'noid') targetUrl",
					UserNm, LangCd, Email, "tochar(connDate, 'yyyy-MM-dd HH:mm:ss') connDate");
	}
}
