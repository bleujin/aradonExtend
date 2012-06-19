package net.ion.radon.impl.let.board;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;

public class TestGeneric extends TestCase {

	public void testSerial() throws Exception {

		Map<String, Serializable> map = new HashMap<String, Serializable>();
		map.put("123", "123");

		Map<String, Object> nmap = (Map<String, Object>) (Object) map;
		Debug.debug(nmap);
	}

	public void testSerial2() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("123", 222);
		map.put("124", "124");

		print(map);
	}

	private void print(Map<String, ? extends Object> map) {
		print(map);
	}

	public void testGeneric() throws Exception {
		Map<String, ? extends Object> map = create("123", 345);

		Debug.debug(map.get("123").getClass());
	}

	public static <K, T> Map<K, T> create(K key, T value) {
		Map<K, T> result = new HashMap<K, T>();
		result.put(key, value);
		return result;
	}

	public void testProducer() throws Exception {
		List<Map<String, ?>> datas = ListUtil.newList();

		Map<String, Object> map = MapUtil.newMap() ;
		datas.add(map) ;
		datas.add(MapUtil.create("name", "bleujin"));
		datas.add(MapUtil.create("name", "novision"));
		datas.add(MapUtil.create("name", 333));

		to(datas);
	}
	
	private void to(List<Map<String, ?>> datas) {
		Debug.line('A') ;
		for (Map<String, ?> entry : datas) {
			Debug.debug(entry.keySet(), entry.values()) ;
		}
	}
	
	
	public void testCallDummy() throws Exception {
		
		List<Map<String, ?>> result = new DummySerial().toMap() ;
		
		
		
		
	}
	

//	private void to(List<Map<String, Object>> datas) throws ResourceException {
//		Debug.line('B') ;
//	}
}


class DummySerial {
	
	public List<Map<String, ? extends Object>> toMap(){
		
		List list = ListUtil.newList() ;
		list.add(MapUtil.create("key", "val")) ;
		
		return  list ;
	}
	
	
}
