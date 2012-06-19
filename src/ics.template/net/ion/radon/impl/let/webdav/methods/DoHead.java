package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;
import java.util.Date;

import net.ion.framework.util.DateUtil;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;

public class DoHead extends AbstractMethod {

	private DoHead(VFileStore store) {
		super(store, Method.HEAD);
	}

	public static DoHead create(VFileStore store) {
		return new DoHead(store);
	}

	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws IOException {
		response.setEntity(new EmptyRepresentation()) ;

		if (getVFile().isDir()) {
			response.setStatus(Status.SUCCESS_NO_CONTENT);
			return response.getEntity();
		}

		String eTagMatch = request.getHeader("If-None-Match");
		if (String.valueOf(getVFile().getETag()).equals(eTagMatch)) {
			response.setStatus(Status.REDIRECTION_NOT_MODIFIED);
			return response.getEntity();
		}

		// setting headers
		long lastModified = getVFile().getContent().getLastModifiedTime();
		response.setHeader("last-modified", DateUtil.toHTTPDateFormat(new Date(lastModified)));
		response.setHeader("ETag", String.valueOf(getVFile().getETag()));

		long resourceLength = getVFile().getContent().getSize();
		response.setHeader("content-length", String.valueOf(resourceLength));
		response.getEntity().setMediaType(MediaType.valueOf(getVFile().getContent().getContentType()));
		response.setStatus(Status.SUCCESS_OK) ;
		return response.getEntity();
	}

}
