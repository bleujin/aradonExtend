package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;

import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

public class DoMkcol extends AbstractMethod {

	private DoMkcol(VFileStore store) {
		super(store, Method.MKCOL);
	}

	public final static DoMkcol create(VFileStore store) {
		return new DoMkcol(store);
	}

	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws IOException {
		getVFile().createFolder();
		response.setEntity(new EmptyRepresentation()) ;
		response.setStatus(Status.SUCCESS_CREATED);
		return response.getEntity();

	}

}
