package net.ion.radon.impl.let.velocity;

import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class ChainContext implements Context {

	private VelocityContext inner = new VelocityContext();
	public ChainContext(){
		inner = new VelocityContext() ;
	}
	
	public ChainContext(Context context){
		inner = new VelocityContext(context) ;
	}
	
	public ChainContext(Map map){
		inner = new VelocityContext(map) ;
	}
	
	
	public void addContext(Context context) {
		if (context != null) {
			Object[] keys = context.getKeys();
			for (int i = 0, len = keys.length; i < len; i++) {
				Object key = keys[i];
				String skey = String.valueOf(key);
				put(skey, context.get(skey));
			}
		}
	}

	public boolean containsKey(Object key) {
		return inner.containsKey(key);
	}

	public Object get(String key) {
		return inner.get(key);
	}

	public Object[] getKeys() {
		return inner.getKeys();
	}

	public Object put(String key, Object value) {
		return inner.put(key, value);
	}

	public Object remove(Object key) {
		return inner.remove(key);
	}
}
