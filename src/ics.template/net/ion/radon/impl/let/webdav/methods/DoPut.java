package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;

import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;

public class DoPut extends AbstractMethod {

	private DoPut(VFileStore store) {
		super(store, Method.PUT);
	}

	public static DoPut create(VFileStore store) {
		return new DoPut(store);
	}

	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws IOException {

		VFile parent = getVFile().getParent();
		// This has already been created, just update the data User-Agent workarounds
		doUserAgentWorkaround(response, request.getHeader("User-Agent"));

		// if (!getVFile().exists()) {
		// getVFile().createFile() ;
		// }

		// setting resourceContent
		Representation entity = request.getEntity();
		if (entity != null) {
			if (entity.getStream() == null) {
				getVFile().createFile() ;
			} else if (entity.getStream() != null) {
				IOUtil.copyNClose(entity.getStream(), getVFile().getOutputStream());
			}
		}
		// final Representation result = new StringRepresentation("");
		// result.setTag(new Tag("If: (<opaquelocktoken:f81d4fae-7dec-11d0-a765-00a0c91e6bf6>)"));

		return response.getEntity();

	}

	/**
	 * @param response
	 */
	private void doUserAgentWorkaround(Response response, String userAgent) {
		if (userAgent != null && userAgent.indexOf("WebDAVFS") != -1 && userAgent.indexOf("Transmit") == -1) {
			Debug.trace("DoPut.execute() : do workaround for user agent '" + userAgent + "'");
			response.setStatus(Status.SUCCESS_CREATED);
		} else if (userAgent != null && userAgent.indexOf("Transmit") != -1) {
			// Transmit also uses WEBDAVFS 1.x.x but crashes
			// with SC_CREATED response
			Debug.trace("DoPut.execute() : do workaround for user agent '" + userAgent + "'");
			response.setStatus(Status.SUCCESS_NO_CONTENT);
		} else {
			response.setStatus(Status.SUCCESS_CREATED);
		}
	}
}
