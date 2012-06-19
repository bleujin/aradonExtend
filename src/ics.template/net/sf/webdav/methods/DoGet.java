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
import java.io.InputStream;
import java.util.logging.Logger;

import net.ion.framework.logging.LogBroker;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.sf.webdav.IMimeTyper;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.locking.ResourceLocks;

import org.restlet.representation.InputRepresentation;
import org.restlet.representation.StringRepresentation;

public class DoGet extends DoHead {

	private static Logger LOG = LogBroker.getLogger(DoGet.class);

	public DoGet(IWebdavStore store, String dftIndexFile, String insteadOf404, ResourceLocks resourceLocks, IMimeTyper mimeTyper, int contentLengthHeader) {
		super(store, dftIndexFile, insteadOf404, resourceLocks, mimeTyper, contentLengthHeader);

	}

	protected void doBody(ITransaction transaction, InnerResponse resp, String path) {

		try {
			StoredObject so = _store.getStoredObject(transaction, path);
			if (so.isNullResource()) {
				String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
				resp.addHeader("Allow", methodsAllowed);
				resp.sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
				return;
			}
			
			InputStream in = _store.getResourceContent(transaction, path);
			resp.setEntity(new InputRepresentation(in)) ;
		} catch (Exception e) {
			LOG.info(e.toString());
		}
	}

	protected void folderBody(ITransaction transaction, String path, InnerResponse resp, InnerRequest req) throws IOException {

		StoredObject so = _store.getStoredObject(transaction, path);
		if (so == null) {
			resp.sendError(WebdavStatus.SC_NOT_FOUND, req.getRequestURI());
		} else {

			if (so.isNullResource()) {
				String methodsAllowed = DeterminableMethod.determineMethodsAllowed(so);
				resp.addHeader("Allow", methodsAllowed);
				resp.sendError(WebdavStatus.SC_METHOD_NOT_ALLOWED);
				return;
			}

			if (so.isFolder()) {
				// TODO some folder response (for browsers, DAV tools
				// use propfind) in html?
				String[] children = _store.getChildrenNames(transaction, path);
				children = (children == null) ? new String[] {} : children;
				StringBuffer childrenTemp = new StringBuffer();
				childrenTemp.append("Contents of this Folder:\n");
				for (String child : children) {
					childrenTemp.append(child);
					childrenTemp.append("\n");
				}
				
				resp.setEntity(new StringRepresentation(childrenTemp)) ;
			}
		}
	}

}
