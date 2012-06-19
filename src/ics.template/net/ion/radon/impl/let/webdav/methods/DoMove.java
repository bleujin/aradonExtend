package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;

import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;
import net.ion.radon.impl.let.webdav.exceptions.WebdavException;

import org.apache.commons.vfs2.provider.local.LocalFile;
import org.apache.commons.vfs2.util.FileObjectUtils;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

public class DoMove extends AbstractMethod {

	private DoMove(VFileStore store) {
		super(store, Method.MOVE);
	}

	public final static DoMove create(VFileStore store) {
		return new DoMove(store);
	}

	public Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws IOException  {
		VFile destfile = resolveFile(transaction, request.getAttribute("scheme"), parseDestinationHeader(request, response));

		String destinationPath = request.getHeader("Destination");
		if (destinationPath == null) {
			throw WebdavException.create(Status.CLIENT_ERROR_BAD_REQUEST) ;
		}

		
		LocalFile newLocalFile = (LocalFile)FileObjectUtils.getAbstractFileObject(destfile.getFileObject());
		
		getVFile().moveTo(destfile) ;
		// getVFile().rename(destfile.getName().getBaseName());
		response.setStatus(Status.SUCCESS_CREATED);
		response.setEntity(new EmptyRepresentation()) ;
		return response.getEntity() ;
	}

}
