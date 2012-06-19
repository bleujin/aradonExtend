package net.ion.heeya;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import net.ion.framework.db.Page;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MapUtil;
import net.ion.framework.util.StringUtil;

import org.apache.commons.collections.set.ListOrderedSet;

public class TestJava extends TestCase {

	public void testToString() throws Exception {
		Page page = Page.create(10, 1) ;
		Map<String, Object> result = new HashMap<String, Object>() ;
		result.put("listNum", page.getListNum()) ;
		result.put("pageNo", page.getPageNo()) ;
		result.put("screenCount", page.getScreenCount()) ;
		result.put("rowCountOfScreen", 5) ;

		Debug.debug(result);
	}

	
	public void testStringTemplate() throws Exception {

		String str = null ;
		Debug.debug(StringUtil.toString(str)) ;
	}
	
	public void testSet() throws Exception {
		Set<Integer> set = new ListOrderedSet() ;
		set.add(1) ;
		set.add(3) ;
		set.add(4) ;
		
		
		final List<Object> newList = new ArrayList<Object>(set) ;
		Debug.debug(set, newList) ;
	}
	
	
	public void testTry() throws Exception {
		
//		Chain c = getChain() ;
//		
//		
//		while(c.hasNext() != null){
//			Result result = c.handle() ;
//			
//			if (result == STOP) return ;
//			if (result == SKIP) 
//			
//			c = c.getNext() ;
//		}
		
		
		
		try {
			funA() ;
			return ;
		} finally {
			Debug.line() ;
		}
	}


	private Integer funA() {
		return 1;
	}
	
	
	public void testHostName() throws Exception {
		Debug.line(InetAddress.getLocalHost().getHostName()) ;
	}
	
	public void testSerialized() throws Exception {
		SObject so = new SObject() ;
		so.a.put((Object)"Hello", "World") ;
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
		ObjectOutputStream oos = new ObjectOutputStream(bos) ;
		oos.writeObject(so) ;
		
		
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray())) ;
		SObject clone = (SObject) ois.readObject() ;
		Debug.debug(clone.a) ;
		
	}
	
	
	public void testStringFormat() throws Exception {
		Debug.line(String.format("%s %s! ", "Hello", "bleujin")) ;
		
	}
	
	
	
}

class SObject implements Serializable {
	Map a = MapUtil.newMap() ;
	
	
}
