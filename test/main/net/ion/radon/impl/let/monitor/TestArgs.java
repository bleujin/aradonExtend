package net.ion.radon.impl.let.monitor;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import net.ion.framework.db.bean.handlers.AbstractListHandler;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

public class TestArgs extends TestCase {

	public void testArgs() throws Exception {
		for (int i = 0; i < 10000; i++) {
			Class clz = AbstractListHandler.class;
			Method[] methods = clz.getMethods();

			Paranamer paranamer = new AdaptiveParanamer();
			for (Method method : methods) {
				if (method.getDeclaringClass().equals(clz)) {
					String[] paramNames = paranamer.lookupParameterNames(method);
					// Debug.debug(method.getName(), clz.hashCode(), method.hashCode(), paramNames) ;
				}
			}
		}

	}

}
