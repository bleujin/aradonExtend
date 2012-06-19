package net.ion.radon.impl.let.velocity;


import java.util.Map;

import net.ion.framework.util.ObjectUtil;
import net.ion.radon.core.TreeContext;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.restlet.Request;
import org.restlet.Response;

public class RadonVelocityContext implements Context{

	private VelocityContext inner ;
	private RadonVelocityContext(Map map){
		this.inner = new VelocityContext(map) ;
	}
	
	public final static RadonVelocityContext create(Request request, TreeContext tc, Response response){
		 final RadonVelocityContext result = new RadonVelocityContext(tc.getAttributes());
		 
		 result.put("request", ObjectUtil.coalesce(request, ObjectUtil.NULL)) ;
		 result.put("response", ObjectUtil.coalesce(response, ObjectUtil.NULL)) ;
		return result ;
	}
	
	public boolean containsKey(Object obj) {
		return inner.containsKey(obj) ;
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
		return inner.remove(key) ;
	}
}
