package net.ion.radon.impl.let.velocity;


import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import net.ion.radon.core.IService;
import net.ion.radon.core.config.Attribute;
import net.ion.radon.core.filter.IFilterResult;
import net.ion.radon.core.filter.IRadonFilter;

import org.apache.velocity.app.Velocity;
import org.restlet.Request;
import org.restlet.Response;

public class VelocityLoaderFilter extends IRadonFilter{

	private boolean initialized = false ;  
	
	@Override
	public IFilterResult preHandle(IService service, Request request, Response response) {
		
		if (! initialized){
			Properties props = new Properties() ;
			Map<String, Attribute> attrs = getAttributes() ;
			for (Entry<String, Attribute> entry : attrs.entrySet()) {
				props.put(entry.getKey(), entry.getValue().getElementValue()) ;
			}
			Velocity.init(props) ;
			initialized = true ;
		}
		
		return IFilterResult.CONTINUE_RESULT;
	}

	@Override
	public IFilterResult afterHandle(IService service, Request request, Response response) {
		return IFilterResult.CONTINUE_RESULT;
	}
}
