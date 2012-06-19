package net.sf.webdav.methods;

import java.io.IOException;
import java.util.logging.Logger;

import net.ion.framework.logging.LogBroker;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.exceptions.LockFailedException;
import net.sf.webdav.locking.IResourceLocks;
import net.sf.webdav.locking.LockedObject;

import org.restlet.data.Status;

public class DoUnlock extends DeterminableMethod {

	private static Logger LOG = LogBroker.getLogger(DoUnlock.class);

	private IWebdavStore _store;
	private IResourceLocks _resourceLocks;
	private boolean _readOnly;

	public DoUnlock(IWebdavStore store, IResourceLocks resourceLocks, boolean readOnly) {
		_store = store;
		_resourceLocks = resourceLocks;
		_readOnly = readOnly;
	}

	public void execute(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException, LockFailedException {
		LOG.info("-- " + this.getClass().getName());

		if (_readOnly) {
			resp.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
			return;
		} else {

			String path = getRelativePath(req);
			String tempLockOwner = "doUnlock" + System.currentTimeMillis() + req.toString();
			try {
				if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {

					String lockId = getLockIdFromLockTokenHeader(req);
					LockedObject lo;
					if (lockId != null && ((lo = _resourceLocks.getLockedObjectByID(transaction, lockId)) != null)) {

						String[] owners = lo.getOwner();
						String owner = null;
						if (lo.isShared()) {
							// more than one owner is possible
							if (owners != null) {
								for (int i = 0; i < owners.length; i++) {
									// remove owner from LockedObject
									lo.removeLockedObjectOwner(owners[i]);
								}
							}
						} else {
							// exclusive, only one lock owner
							if (owners != null)
								owner = owners[0];
							else
								owner = null;
						}

						if (_resourceLocks.unlock(transaction, lockId, owner)) {
							StoredObject so = _store.getStoredObject(transaction, path);
							if (so.isNullResource()) {
								_store.removeObject(transaction, path);
							}

							resp.setStatus(Status.SUCCESS_NO_CONTENT);
						} else {
							LOG.info("DoUnlock failure at " + lo.getPath());
							resp.setStatus(Status.CLIENT_ERROR_FAILED_DEPENDENCY);
						}

					} else {
						resp.setStatus(Status.CLIENT_ERROR_BAD_REQUEST) ;
					}
				}
			} catch (LockFailedException e) {
				e.printStackTrace();
			} finally {
				_resourceLocks.unlockTemporaryLockedObjects(transaction, path, tempLockOwner);
			}
		}
	}

}
