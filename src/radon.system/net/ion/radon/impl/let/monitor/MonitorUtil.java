package net.ion.radon.impl.let.monitor;

import net.ion.framework.util.CaseInsensitiveHashMap;
import net.ion.framework.util.StringUtil;

public class MonitorUtil {

	
	private static CaseInsensitiveHashMap<Integer> MASK = new CaseInsensitiveHashMap<Integer>() ;
	static {
		load() ;
	}
	private static void load(){
		MASK.put("CREATED", 1) ;
		MASK.put("DELETED", 2) ;
		MASK.put("MODIFIED", 4) ;
		MASK.put("ACCESSED", 8) ;
		MASK.put("NAME_CHANGED_OLD", 16) ;
		MASK.put("NAME_CHANGED_NEW", 32) ;
		MASK.put("RENAMED", 48) ;
		MASK.put("SIZE_CHANGED", 64) ;
		MASK.put("ATTRIBUTES_CHANGED", 128) ;
		MASK.put("SECURITY_CHANGED", 256) ;
		MASK.put("ANY", 511) ;
	}
	
	
	public final static int toMaskNum(String masks){
		String[] mask = StringUtil.split(masks) ;
		int result = 0 ;
		for (String m : mask) {
			if (MASK.containsKey(m)) result += MASK.get(m) ;
		}
		return result ;
	}
	
}
