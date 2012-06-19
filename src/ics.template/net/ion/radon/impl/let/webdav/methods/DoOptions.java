package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;
import java.util.List;

import net.ion.framework.util.ListUtil;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

public class DoOptions extends AbstractMethod {

	private DoOptions(VFileStore store) {
		super(store, Method.OPTIONS);
	}
	
	public static DoOptions create(VFileStore store) {
		return new DoOptions(store);
	}
	
	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws IOException  {

		response.getAllowedMethods().add(Method.HEAD);
		response.getAllowedMethods().add(Method.GET);
		response.getAllowedMethods().add(Method.PUT);
		response.getAllowedMethods().add(Method.POST);
		response.getAllowedMethods().add(Method.PROPFIND);
		response.getAllowedMethods().add(Method.OPTIONS);
		response.getAllowedMethods().add(Method.DELETE);
		response.getAllowedMethods().add(Method.PROPPATCH);
		response.getAllowedMethods().add(Method.MKCOL);
		response.getAllowedMethods().add(Method.COPY);
		response.getAllowedMethods().add(Method.MOVE);

//		response.getAllowedMethods().add(Method.LOCK);
//		response.getAllowedMethods().add(Method.UNLOCK);

		
		final List<Method> allowMethod = ListUtil.toList(Method.HEAD, Method.GET, Method.PUT, Method.POST, Method.PROPFIND, 
										Method.OPTIONS, Method.DELETE, //  Method.LOCK, Method.UNLOCK, 
										Method.PROPPATCH, Method.MKCOL, Method.COPY, Method.MOVE);
		
		response.setHeader("Allow", StringUtil.join(allowMethod.toArray(new Method[0]), ',') );
		
		response.setHeader("MS-Author-Via", "DAV");
		response.setHeader("DAV", "1");
		response.setEntity(new EmptyRepresentation()) ;
		response.setStatus(Status.SUCCESS_OK) ;
		
		return response.getEntity();

	}

	
}
