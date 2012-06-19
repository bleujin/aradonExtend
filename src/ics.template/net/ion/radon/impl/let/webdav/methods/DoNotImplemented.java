package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;

import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class DoNotImplemented extends AbstractMethod {

	private final static DoNotImplemented NOT_IMPL_METHOD = new DoNotImplemented() ;
	
	private DoNotImplemented() {
		super(null, Method.ALL); 
	}
	
	public final static DoNotImplemented create(){
		return NOT_IMPL_METHOD ;
	}

	public Representation myHandle(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED) ;
	}

}
