package net.ion.radon.impl.let.webdav;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ion.framework.vfs.FileSystemEntry;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.impl.let.vfs.VFSEntry;
import net.ion.radon.impl.let.webdav.methods.DoCopy;
import net.ion.radon.impl.let.webdav.methods.DoDelete;
import net.ion.radon.impl.let.webdav.methods.DoGet;
import net.ion.radon.impl.let.webdav.methods.DoHead;
import net.ion.radon.impl.let.webdav.methods.DoLock;
import net.ion.radon.impl.let.webdav.methods.DoMkcol;
import net.ion.radon.impl.let.webdav.methods.DoMove;
import net.ion.radon.impl.let.webdav.methods.DoOptions;
import net.ion.radon.impl.let.webdav.methods.DoPropfind;
import net.ion.radon.impl.let.webdav.methods.DoProppatch;
import net.ion.radon.impl.let.webdav.methods.DoPut;

import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;
import org.restlet.util.Series;

public class WebDavLet extends AbstractServerResource {

	private static DocumentBuilder builder = createBuilder();

	private static DocumentBuilder createBuilder() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			return dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Representation handle() {
		if (!isExisting() && getMethod().isSafe())
			doError(Status.CLIENT_ERROR_NOT_FOUND);
		else
			try {
				Representation result = doHandle();

				if (!getResponse().isEntityAvailable())
					getResponse().setEntity(result);
				if (Status.CLIENT_ERROR_METHOD_NOT_ALLOWED.equals(getStatus()))
					updateAllowedMethods();
				else if (Method.GET.equals(getMethod()) && Status.SUCCESS_OK.equals(getStatus()) && (getResponseEntity() == null || !getResponseEntity().isAvailable())) {
					getLogger().fine("A response with a 200 (Ok) status should have an entity. Changing the status to 204 (No content).");
					setStatus(Status.SUCCESS_NO_CONTENT);
				}
			} catch (Throwable t) {
				doCatch(t);
			}
		return getResponse().getEntity();
	}

	public Representation doHandle() throws ResourceException {

		getInnerRequest().putAttribute("scheme", "template");

		Method method = getMethod();
		allowOtherHost();

		VFSEntry entry = getContext().getAttributeObject(VFSEntry.class.getCanonicalName(), VFSEntry.class);
		FileSystemEntry fileSystemEntry = entry.getFileSystemEntry();
		final VFileStore store = VFileStore.create(fileSystemEntry);
		
		User user = getRequest().getClientInfo().getUser();
		ITransaction transaction = store.begin(user);

		try {
			if (method == null) {
				throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, "No method specified");
			} else if (Method.GET.equals(method)) {
				DoGet.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.PUT.equals(method)) {
				DoPut.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.POST.equals(method)) {
				DoPut.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.DELETE.equals(method)) {
				DoDelete.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.HEAD.equals(method)) {
				DoHead.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.OPTIONS.equals(method)) {
				DoOptions.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.PROPFIND.equals(method)) {
				DoPropfind.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.COPY.equals(method)) {
				DoCopy.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.MOVE.equals(method)) {
				DoMove.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.PROPPATCH.equals(method)) {
				DoProppatch.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.MKCOL.equals(method)) {
				DoMkcol.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.LOCK.equals(method)) {
				DoLock.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
			} else if (Method.UNLOCK.equals(method)) {
				getResponse().setStatus(Status.SUCCESS_NO_CONTENT) ;
			} else {
				throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, "No method specified");
			}
			store.commit(transaction);
		} finally {
			getRequest().release();
		}

		return getResponse().getEntity();
		// return new StringRepresentation(text, MediaType.APPLICATION_XML) ;
	}

	private void allowOtherHost() {

		Series<Header> resHeader = getInnerResponse().getHeaders();
		resHeader.add("Access-Control-Allow-Origin", "*");
		// responseHeaders.add("Access-Control-Allow-Method", "*");
		resHeader.add("Access-Control-Request-Method", "POST,GET,OPTIONS");
		resHeader.add("XDomainRequestAllowed", "1");
		resHeader.add("Access-Control-Allow-Credentials", "1");
		resHeader.add("Access-Control-Max-Age", "1728000");

		resHeader.add("Access-Control-Allow-Headers", "X-ARADONUNER");
	}
}
