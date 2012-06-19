package net.sf.webdav.methods;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import net.ion.framework.logging.LogBroker;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.sf.webdav.IMimeTyper;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.LockFailedException;
import net.sf.webdav.exceptions.ObjectAlreadyExistsException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.locking.ResourceLocks;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;

public class DoHead extends AbstractMethod {

	protected String _dftIndexFile;
	protected IWebdavStore _store;
	protected String _insteadOf404;
	protected ResourceLocks _resourceLocks;
	protected IMimeTyper _mimeTyper;
	protected int _contentLength;

	private static Logger LOG = LogBroker.getLogger(DoHead.class);

	public DoHead(IWebdavStore store, String dftIndexFile, String insteadOf404, ResourceLocks resourceLocks, IMimeTyper mimeTyper, int contentLengthHeader) {
		_store = store;
		_dftIndexFile = dftIndexFile;
		_insteadOf404 = insteadOf404;
		_resourceLocks = resourceLocks;
		_mimeTyper = mimeTyper;
		_contentLength = contentLengthHeader;
	}

	public void execute(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException, LockFailedException {

		// determines if the uri exists.

		boolean bUriExists = false;

		String path = getRelativePath(req);
		LOG.info("-- " + this.getClass().getName());

		StoredObject so = _store.getStoredObject(transaction, path);
		if (so == null) {
			if (this._insteadOf404 != null && !_insteadOf404.trim().equals("")) {
				path = this._insteadOf404;
				so = _store.getStoredObject(transaction, this._insteadOf404);
			}
		} else
			bUriExists = true;

		if (so != null) {
			if (so.isFolder()) {
				if (_dftIndexFile != null && !_dftIndexFile.trim().equals("")) {
					resp.redirectPermanent(req.getResourceRef().toString() + this._dftIndexFile);
					return;
				}
			} else if (so.isNullResource()) {
				String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
				resp.getHeaders().add("Allow", methodsAllowed);
				resp.setStatus(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
				return;
			}

			String tempLockOwner = "doGet" + System.currentTimeMillis() + req.toString();

			if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {
				try {

					String eTagMatch = req.getHeader("If-None-Match");
					if (eTagMatch != null) {
						if (eTagMatch.equals(getETag(so))) {
							resp.setStatus(Status.REDIRECTION_NOT_MODIFIED);
							return;
						}
					}

					if (so.isResource()) {
						// path points to a file but ends with / or \
						if (path.endsWith("/") || (path.endsWith("\\"))) {
							resp.sendError(WebdavStatus.SC_NOT_FOUND, req.getRequestURI());
						} else {

							// setting headers
							long lastModified = so.getLastModified().getTime();
							resp.setHeader("last-modified", LAST_MODIFIED_DATE_FORMAT.format(new Date(lastModified))) ;

							String eTag = getETag(so);
							resp.getHeaders().add("ETag", eTag);

							long resourceLength = so.getResourceLength();

							if (_contentLength == 1) {
								if (resourceLength > 0) {
									if (resourceLength <= Integer.MAX_VALUE) {
										resp.setHeader("content-length", "" + resourceLength);
									} else {
										resp.setHeader("content-length", "" + resourceLength);
										// is "content-length" the right header?
										// is long a valid format?
									}
								}
							}

							String mimeType = _mimeTyper.getMimeType(path);
							if (mimeType == null) {
								int lastSlash = path.replace('\\', '/').lastIndexOf('/');
								int lastDot = path.indexOf(".", lastSlash);
								if (lastDot == -1) {
									mimeType = "text/html";
								}
							}

							doBody(transaction, resp, path);
							if (resp.getEntity() == null) resp.setEntity(new EmptyRepresentation()) ;
							resp.getEntity().setMediaType(MediaType.valueOf(mimeType)) ;
						}
					} else {
						folderBody(transaction, path, resp, req);
					}
				} catch (AccessDeniedException e) {
					resp.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
				} catch (ObjectAlreadyExistsException e) {
					resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
				} catch (WebdavException e) {
					resp.setStatus(Status.SERVER_ERROR_INTERNAL);
				} finally {
					_resourceLocks.unlockTemporaryLockedObjects(transaction, path, tempLockOwner);
				}
			} else {
				resp.setStatus(Status.SERVER_ERROR_INTERNAL);
			}
		} else {
			folderBody(transaction, path, resp, req);
		}

		if (!bUriExists)
			resp.setStatus(Status.CLIENT_ERROR_NOT_FOUND);

	}

	protected void folderBody(ITransaction transaction, String path, InnerResponse resp, InnerRequest req) throws IOException {
		// no body for HEAD
	}

	protected void doBody(ITransaction transaction, InnerResponse resp, String path) throws IOException {
		// no body for HEAD
	}
}
