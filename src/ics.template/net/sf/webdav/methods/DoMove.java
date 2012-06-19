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
import java.util.Hashtable;
import java.util.logging.Logger;

import net.ion.framework.logging.LogBroker;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.sf.webdav.ITransaction;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.exceptions.AccessDeniedException;
import net.sf.webdav.exceptions.LockFailedException;
import net.sf.webdav.exceptions.ObjectAlreadyExistsException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.locking.ResourceLocks;

public class DoMove extends AbstractMethod {

	private static Logger LOG = LogBroker.getLogger(DoMove.class);

	private ResourceLocks _resourceLocks;
	private DoDelete _doDelete;
	private DoCopy _doCopy;
	private boolean _readOnly;

	public DoMove(ResourceLocks resourceLocks, DoDelete doDelete, DoCopy doCopy, boolean readOnly) {
		_resourceLocks = resourceLocks;
		_doDelete = doDelete;
		_doCopy = doCopy;
		_readOnly = readOnly;
	}

	public void execute(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException, LockFailedException {

		if (!_readOnly) {
			LOG.info("-- " + this.getClass().getName());

			String sourcePath = getRelativePath(req);
			Hashtable<String, Integer> errorList = new Hashtable<String, Integer>();

			if (!checkLocks(transaction, req, resp, _resourceLocks, sourcePath)) {
				errorList.put(sourcePath, WebdavStatus.SC_LOCKED);
				sendReport(req, resp, errorList);
				return;
			}

			String destinationPath = req.getHeader("Destination");
			if (destinationPath == null) {
				resp.sendError(WebdavStatus.SC_BAD_REQUEST);
				return;
			}

			if (!checkLocks(transaction, req, resp, _resourceLocks, destinationPath)) {
				errorList.put(destinationPath, WebdavStatus.SC_LOCKED);
				sendReport(req, resp, errorList);
				return;
			}

			String tempLockOwner = "doMove" + System.currentTimeMillis() + req.toString();

			if (_resourceLocks.lock(transaction, sourcePath, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {
				try {

					if (_doCopy.copyResource(transaction, req, resp)) {

						errorList = new Hashtable<String, Integer>();
						_doDelete.deleteResource(transaction, sourcePath, errorList, req, resp);
						if (!errorList.isEmpty()) {
							sendReport(req, resp, errorList);
						}
					}

				} catch (AccessDeniedException e) {
					resp.sendError(WebdavStatus.SC_FORBIDDEN);
				} catch (ObjectAlreadyExistsException e) {
					resp.sendError(WebdavStatus.SC_NOT_FOUND, req.getRequestURI());
				} catch (WebdavException e) {
					resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
				} finally {
					_resourceLocks.unlockTemporaryLockedObjects(transaction, sourcePath, tempLockOwner);
				}
			} else {
				errorList.put(req.getHeader("Destination"), WebdavStatus.SC_LOCKED);
				sendReport(req, resp, errorList);
			}
		} else {
			resp.sendError(WebdavStatus.SC_FORBIDDEN);

		}

	}

}
