/*
 * Copyright 1999,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.webdav.methods;

import java.io.IOException;
import java.util.logging.Logger;

import net.ion.framework.logging.LogBroker;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.LockFailedException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.locking.ResourceLocks;

public class DoOptions extends DeterminableMethod {

	private static Logger LOG = LogBroker.getLogger(DoOptions.class);

	private IWebdavStore _store;
	private ResourceLocks _resourceLocks;

	public DoOptions(IWebdavStore store, ResourceLocks resLocks) {
		_store = store;
		_resourceLocks = resLocks;
	}

	public void execute(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException, LockFailedException {

		LOG.info("-- " + this.getClass().getName());

		String tempLockOwner = "doOptions" + System.currentTimeMillis() + req.toString();
		String path = getRelativePath(req);
		if (_resourceLocks.lock(transaction, path, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {
			StoredObject so = null;
			try {
				resp.getHeaders().add("DAV", "1, 2");

				so = _store.getStoredObject(transaction, path);
				String methodsAllowed = determineMethodsAllowed(so);
				resp.getHeaders().add("Allow", methodsAllowed);
				resp.getHeaders().add("MS-Author-Via", "DAV");
			} catch (AccessDeniedException e) {
				resp.sendError(WebdavStatus.SC_FORBIDDEN);
			} catch (WebdavException e) {
				resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			} finally {
				_resourceLocks.unlockTemporaryLockedObjects(transaction, path, tempLockOwner);
			}
		} else {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
