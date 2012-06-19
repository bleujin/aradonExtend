
package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;

import net.ion.framework.util.StringUtil;
import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;
import net.ion.radon.impl.let.webdav.exceptions.AccessDeniedException;
import net.ion.radon.impl.let.webdav.exceptions.ObjectAlreadyExistsException;
import net.ion.radon.impl.let.webdav.exceptions.ObjectNotFoundException;
import net.ion.radon.impl.let.webdav.exceptions.WebdavException;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class DoCopy extends AbstractMethod {

	private static final String NULL_RESOURCE_METHODS_ALLOWED = "OPTIONS, MKCOL, PUT, PROPFIND, LOCK, UNLOCK";
	private static final String RESOURCE_METHODS_ALLOWED = "OPTIONS, GET, HEAD, POST, DELETE, TRACE, PROPPATCH, COPY, MOVE, LOCK, UNLOCK, PROPFIND";
	private static final String FOLDER_METHOD_ALLOWED = ", PUT";
	private static final String LESS_ALLOWED_METHODS = "OPTIONS, MKCOL, PUT";

	private DoCopy(VFileStore store) {
		super(store, Method.COPY);
	}

	public final static DoCopy create(VFileStore store) {
		return new DoCopy(store);
	}

	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response)  {
		try {
			return copyResource(transaction, request, response, getVFile());
		} catch (AccessDeniedException e) {
			throw WebdavException.create(Status.CLIENT_ERROR_FORBIDDEN, e) ;
		} catch (ObjectAlreadyExistsException e) {
			throw WebdavException.create(Status.CLIENT_ERROR_CONFLICT, e) ;
		} catch (ObjectNotFoundException e) {
			throw WebdavException.create(Status.CLIENT_ERROR_NOT_FOUND, e) ;
		} catch (IOException e) {
			throw WebdavException.create(Status.SERVER_ERROR_INTERNAL, e) ;
		}
	}

	private Representation copyResource(ITransaction transaction, InnerRequest request, InnerResponse response, VFile vfile) throws IOException {

		String destinationPath = parseDestinationHeader(request, response);
		if (destinationPath == null)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		if (vfile.getName().getPath().equals(destinationPath)) {
			throw new ResourceException(Status.CLIENT_ERROR_FORBIDDEN);
		}

		// Parsing overwrite header
		String overwriteHeader = request.getHeader("Overwrite");
		boolean overwrite = (StringUtil.isBlank(overwriteHeader) || overwriteHeader.equalsIgnoreCase("T")) ? true : false;

		// Overwriting the destination
		if (!vfile.exists()) {
			String methodsAllowed = determineMethodsAllowed();
			response.setHeader("Allow", methodsAllowed);
			throw WebdavException.create(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED) ;
		}

		VFile destfile = resolveFile(transaction, request.getAttribute("scheme"), destinationPath);
		vfile.copy(destfile);

		response.setEntity(new EmptyRepresentation()) ;
		response.setStatus(Status.SUCCESS_CREATED);
		return response.getEntity();
	}

	private String determineMethodsAllowed() {
		try {
			if (getVFile() != null) {
				if (!getVFile().exists()) {
					return NULL_RESOURCE_METHODS_ALLOWED;
				} else if (getVFile().isDir()) {
					return RESOURCE_METHODS_ALLOWED + FOLDER_METHOD_ALLOWED;
				}
				return RESOURCE_METHODS_ALLOWED;
			}
		} catch (Exception ignore) {
			ignore.printStackTrace() ;
		}
		return LESS_ALLOWED_METHODS;
	}

}
