package net.ion.radon.impl.let.webdav.methods;

import net.ion.radon.core.let.AbstractLet;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;

import org.apache.commons.vfs2.FileSystemException;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

public class DoDelete extends AbstractMethod {

	private DoDelete(VFileStore store) {
		super(store, Method.DELETE);
	}

	public final static DoDelete create(VFileStore store) {
		return new DoDelete(store);
	}

	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws FileSystemException {
		getVFile().deleteSub();
		response.setStatus(Status.SUCCESS_NO_CONTENT);
		return AbstractLet.EMPTY_REPRESENTATION;
	}

}
