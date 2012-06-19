package net.ion.im.bbs.filter;

import java.util.Map;

import net.ion.radon.core.IService;
import net.ion.radon.core.filter.IFilterResult;
import net.ion.radon.core.filter.IRadonFilter;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class AuthenticationFilter extends IRadonFilter {

	@Override
	public IFilterResult afterHandle(IService iservice, Request request, Response response) {
		return null;
	}

	@Override
	public IFilterResult preHandle(IService iservice, Request request, Response response) {
		Map<String, Object> params = getInnerRequest(request).getGeneralParameter();
		
		CipherAES endecriptor;
		try {
			endecriptor = new CipherAES();
			
			if(params.containsKey("auth")) {
				String passedAuthKey = (String) params.get("auth");
				String storedAuthKey = endecriptor.encrypt("IMMobile");
				
				if(!storedAuthKey.equals(passedAuthKey)) {
					return IFilterResult.STOP_RESULT;
				}
			} else {
				return IFilterResult.STOP_RESULT;
			}
			
			return IFilterResult.CONTINUE_RESULT;
			
		} catch (Exception e) {
			return IFilterResult.stopResult(new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage())) ;
		}
	}
}
