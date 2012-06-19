package net.ion.radon.impl.let.velocity;


import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import net.ion.framework.util.Debug;

import org.apache.commons.collections.map.LRUMap;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.ResourceCache;
import org.apache.velocity.util.MapFactory;

public class ResourceCacheImpl implements ResourceCache {
	
	private static ResourceCache SELF ;
	
	public ResourceCacheImpl() {
		cache = MapFactory.create(512, 0.5F, 30, false);
		rsvc = null;
	}
	
	public final static ResourceCache getInstance(){
		return SELF ;
	}

	public void initialize(RuntimeServices rs) {
		rsvc = rs;
		int maxSize = rsvc.getInt("resource.manager.defaultcache.size", 89);
		if (maxSize > 0) {
			Map<Object, Resource> lruCache = Collections.synchronizedMap(new LRUMap(maxSize));
			lruCache.putAll(cache);
			cache = lruCache;
		}
		rsvc.getLog().debug("ResourceCache: initialized (" + getClass() + ") with " + cache.getClass() + " cache map.");
		SELF = this ;
	}

	public Resource get(Object key) {
		final Resource resource = (Resource) cache.get(key);
		if (resource != null) {
//			Debug.line('@', resource, resource.getResourceLoader(), resource.getResourceLoader().isSourceModified(resource)) ;
//			StringResource sr = StringResourceLoader.getRepository("string").getStringResource("4cfdfa5ac6f3cf56eada0ea5") ;
		}
		return resource;
	}

	public Resource put(Object key, Resource value) {
		Debug.line() ;
		return cache.put(key, value);
	}

	public Resource remove(Object key) {
		return cache.remove(key);
	}

	public Iterator enumerateKeys() {
		return cache.keySet().iterator();
	}

	protected Map<Object, Resource> cache;
	protected RuntimeServices rsvc;
}
