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
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;

import net.ion.framework.logging.LogBroker;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.sf.webdav.ITransaction;
import net.sf.webdav.IWebdavStore;
import net.sf.webdav.StoredObject;
import net.sf.webdav.WebdavStatus;
import net.sf.webdav.exceptions.LockFailedException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.fromcatalina.XMLWriter;
import net.sf.webdav.locking.IResourceLocks;
import net.sf.webdav.locking.LockedObject;

import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DoLock extends AbstractMethod {

	private static Logger LOG = LogBroker.getLogger(DoLock.class);

	private IWebdavStore _store;
	private IResourceLocks _resourceLocks;
	private boolean _readOnly;

	private boolean _macLockRequest = false;

	private boolean _exclusive = false;
	private String _type = null;
	private String _lockOwner = null;

	private String _path = null;
	private String _parentPath = null;

	private String _userAgent = null;

	public DoLock(IWebdavStore store, IResourceLocks resourceLocks, boolean readOnly) {
		_store = store;
		_resourceLocks = resourceLocks;
		_readOnly = readOnly;
	}

	public void execute(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException, LockFailedException {
		LOG.info("-- " + this.getClass().getName());

		if (_readOnly) {
			resp.sendError(WebdavStatus.SC_FORBIDDEN);
			return;
		} else {
			_path = getRelativePath(req);
			_parentPath = getParentPath(getCleanPath(_path));

			Hashtable<String, Integer> errorList = new Hashtable<String, Integer>();

			if (!checkLocks(transaction, req, resp, _resourceLocks, _path)) {
				errorList.put(_path, WebdavStatus.SC_LOCKED);
				sendReport(req, resp, errorList);
				return; // resource is locked
			}

			if (!checkLocks(transaction, req, resp, _resourceLocks, _parentPath)) {
				errorList.put(_parentPath, WebdavStatus.SC_LOCKED);
				sendReport(req, resp, errorList);
				return; // parent is locked
			}

			// Mac OS Finder (whether 10.4.x or 10.5) can't store files
			// because executing a LOCK without lock information causes a
			// SC_BAD_REQUEST
			_userAgent = req.getHeader("User-Agent");
			if (_userAgent != null && _userAgent.indexOf("Darwin") != -1) {
				_macLockRequest = true;

				String timeString = new Long(System.currentTimeMillis()).toString();
				_lockOwner = _userAgent.concat(timeString);
			}

			String tempLockOwner = "doLock" + System.currentTimeMillis() + req.toString();
			if (_resourceLocks.lock(transaction, _path, tempLockOwner, false, 0, TEMP_TIMEOUT, TEMPORARY)) {
				try {
					if (req.getHeader("If") != null) {
						doRefreshLock(transaction, req, resp);
					} else {
						doLock(transaction, req, resp);
					}
				} catch (LockFailedException e) {
					resp.sendError(WebdavStatus.SC_LOCKED);
					e.printStackTrace();
				} finally {
					_resourceLocks.unlockTemporaryLockedObjects(transaction, _path, tempLockOwner);
				}
			}
		}
	}

	private void doLock(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException, LockFailedException {

		StoredObject so = _store.getStoredObject(transaction, _path);

		if (so != null) {
			doLocking(transaction, req, resp);
		} else {
			// resource doesn't exist, null-resource lock
			doNullResourceLock(transaction, req, resp);
		}

		so = null;
		_exclusive = false;
		_type = null;
		_lockOwner = null;

	}

	private void doLocking(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException {

		// Tests if LockObject on requested path exists, and if so, tests
		// exclusivity
		LockedObject lo = _resourceLocks.getLockedObjectByPath(transaction, _path);
		if (lo != null) {
			if (lo.isExclusive()) {
				sendLockFailError(transaction, req, resp);
				return;
			}
		}
		try {
			// Thats the locking itself
			executeLock(transaction, req, resp);

		} catch (ServletException e) {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			LOG.info(e.toString());
		} catch (LockFailedException e) {
			sendLockFailError(transaction, req, resp);
		} finally {
			lo = null;
		}

	}

	private void doNullResourceLock(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException {

		StoredObject parentSo, nullSo = null;

		try {
			parentSo = _store.getStoredObject(transaction, _parentPath);
			if (_parentPath != null && parentSo == null) {
				_store.createFolder(transaction, _parentPath);
			} else if (_parentPath != null && parentSo != null && parentSo.isResource()) {
				resp.sendError(WebdavStatus.SC_PRECONDITION_FAILED);
				return;
			}

			nullSo = _store.getStoredObject(transaction, _path);
			if (nullSo == null) {
				// resource doesn't exist
				_store.createResource(transaction, _path);

				// Transmit expects 204 response-code, not 201
				if (_userAgent != null && _userAgent.indexOf("Transmit") != -1) {
					LOG.info("DoLock.execute() : do workaround for user agent '" + _userAgent + "'");
					resp.setStatus(WebdavStatus.SC_NO_CONTENT);
				} else {
					resp.setStatus(WebdavStatus.SC_CREATED);
				}

			} else {
				// resource already exists, could not execute null-resource lock
				sendLockFailError(transaction, req, resp);
				return;
			}
			nullSo = _store.getStoredObject(transaction, _path);
			// define the newly created resource as null-resource
			nullSo.setNullResource(true);

			// Thats the locking itself
			executeLock(transaction, req, resp);

		} catch (LockFailedException e) {
			sendLockFailError(transaction, req, resp);
		} catch (WebdavException e) {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		} catch (ServletException e) {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
		} finally {
			parentSo = null;
			nullSo = null;
		}
	}

	private void doRefreshLock(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException, LockFailedException {

		String[] lockTokens = getLockIdFromIfHeader(req);
		String lockToken = null;
		if (lockTokens != null)
			lockToken = lockTokens[0];

		if (lockToken != null) {
			// Getting LockObject of specified lockToken in If header
			LockedObject refreshLo = _resourceLocks.getLockedObjectByID(transaction, lockToken);
			if (refreshLo != null) {
				int timeout = getTimeout(transaction, req);

				refreshLo.refreshTimeout(timeout);
				// sending success response
				generateXMLReport(transaction, resp, refreshLo);

				refreshLo = null;
			} else {
				// no LockObject to given lockToken
				resp.sendError(WebdavStatus.SC_PRECONDITION_FAILED);
			}

		} else {
			resp.sendError(WebdavStatus.SC_PRECONDITION_FAILED);
		}
	}

	// ------------------------------------------------- helper methods

	/**
	 * Executes the LOCK
	 */
	private void executeLock(ITransaction transaction, InnerRequest req, InnerResponse resp) throws LockFailedException, IOException, ServletException {

		// Mac OS lock request workaround
		if (_macLockRequest) {
			LOG.info("DoLock.execute() : do workaround for user agent '" + _userAgent + "'");

			doMacLockRequestWorkaround(transaction, req, resp);
		} else {
			// Getting LockInformation from request
			if (getLockInformation(transaction, req, resp)) {
				int depth = getDepth(req);
				int lockDuration = getTimeout(transaction, req);

				boolean lockSuccess = false;
				if (_exclusive) {
					lockSuccess = _resourceLocks.exclusiveLock(transaction, _path, _lockOwner, depth, lockDuration);
				} else {
					lockSuccess = _resourceLocks.sharedLock(transaction, _path, _lockOwner, depth, lockDuration);
				}

				if (lockSuccess) {
					// Locks successfully placed - return information about
					LockedObject lo = _resourceLocks.getLockedObjectByPath(transaction, _path);
					if (lo != null) {
						generateXMLReport(transaction, resp, lo);
					} else {
						resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
					}
				} else {
					sendLockFailError(transaction, req, resp);

					throw new LockFailedException();
				}
			} else {
				// information for LOCK could not be read successfully
				resp.sendError(WebdavStatus.SC_BAD_REQUEST);
				resp.getEntity().setMediaType(MediaType.TEXT_XML) ;
				resp.getEntity().setCharacterSet(CharacterSet.UTF_8) ;
			}
		}
	}

	/**
	 * Tries to get the LockInformation from LOCK request
	 */
	private boolean getLockInformation(ITransaction transaction, InnerRequest req, InnerResponse resp) throws ServletException, IOException {

		Node lockInfoNode = null;
		DocumentBuilder documentBuilder = null;

		documentBuilder = getDocumentBuilder();
		try {
			Document document = documentBuilder.parse(new InputSource(req.getInputStream()));

			// Get the root element of the document
			Element rootElement = document.getDocumentElement();

			lockInfoNode = rootElement;

			if (lockInfoNode != null) {
				NodeList childList = lockInfoNode.getChildNodes();
				Node lockScopeNode = null;
				Node lockTypeNode = null;
				Node lockOwnerNode = null;

				Node currentNode = null;
				String nodeName = null;

				for (int i = 0; i < childList.getLength(); i++) {
					currentNode = childList.item(i);

					if (currentNode.getNodeType() == Node.ELEMENT_NODE || currentNode.getNodeType() == Node.TEXT_NODE) {

						nodeName = currentNode.getNodeName();

						if (nodeName.endsWith("locktype")) {
							lockTypeNode = currentNode;
						}
						if (nodeName.endsWith("lockscope")) {
							lockScopeNode = currentNode;
						}
						if (nodeName.endsWith("owner")) {
							lockOwnerNode = currentNode;
						}
					} else {
						return false;
					}
				}

				if (lockScopeNode != null) {
					String scope = null;
					childList = lockScopeNode.getChildNodes();
					for (int i = 0; i < childList.getLength(); i++) {
						currentNode = childList.item(i);

						if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
							scope = currentNode.getNodeName();

							if (scope.endsWith("exclusive")) {
								_exclusive = true;
							} else if (scope.equals("shared")) {
								_exclusive = false;
							}
						}
					}
					if (scope == null) {
						return false;
					}

				} else {
					return false;
				}

				if (lockTypeNode != null) {
					childList = lockTypeNode.getChildNodes();
					for (int i = 0; i < childList.getLength(); i++) {
						currentNode = childList.item(i);

						if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
							_type = currentNode.getNodeName();

							if (_type.endsWith("write")) {
								_type = "write";
							} else if (_type.equals("read")) {
								_type = "read";
							}
						}
					}
					if (_type == null) {
						return false;
					}
				} else {
					return false;
				}

				if (lockOwnerNode != null) {
					childList = lockOwnerNode.getChildNodes();
					for (int i = 0; i < childList.getLength(); i++) {
						currentNode = childList.item(i);

						if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
							_lockOwner = currentNode.getTextContent();
						}
					}
				}
				if (_lockOwner == null) {
					return false;
				}
			} else {
				return false;
			}

		} catch (DOMException e) {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
			return false;
		} catch (SAXException e) {
			resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * Ties to read the timeout from request
	 */
	private int getTimeout(ITransaction transaction, InnerRequest req) {

		int lockDuration = DEFAULT_TIMEOUT;
		String lockDurationStr = req.getHeader("Timeout");

		if (lockDurationStr == null) {
			lockDuration = DEFAULT_TIMEOUT;
		} else {
			int commaPos = lockDurationStr.indexOf(',');
			// if multiple timeouts, just use the first one
			if (commaPos != -1) {
				lockDurationStr = lockDurationStr.substring(0, commaPos);
			}
			if (lockDurationStr.startsWith("Second-")) {
				lockDuration = new Integer(lockDurationStr.substring(7)).intValue();
			} else {
				if (lockDurationStr.equalsIgnoreCase("infinity")) {
					lockDuration = MAX_TIMEOUT;
				} else {
					try {
						lockDuration = new Integer(lockDurationStr).intValue();
					} catch (NumberFormatException e) {
						lockDuration = MAX_TIMEOUT;
					}
				}
			}
			if (lockDuration <= 0) {
				lockDuration = DEFAULT_TIMEOUT;
			}
			if (lockDuration > MAX_TIMEOUT) {
				lockDuration = MAX_TIMEOUT;
			}
		}
		return lockDuration;
	}

	/**
	 * Generates the response XML with all lock information
	 */
	private void generateXMLReport(ITransaction transaction, InnerResponse resp, LockedObject lo) throws IOException {

		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("DAV:", "D");

		StringWriter sw = new StringWriter() ;
		
		
		XMLWriter generatedXML = new XMLWriter(sw, namespaces);
		generatedXML.writeXMLHeader();
		generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);
		generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.OPENING);
		generatedXML.writeElement("DAV::activelock", XMLWriter.OPENING);

		generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
		generatedXML.writeProperty("DAV::" + _type);
		generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
		if (_exclusive) {
			generatedXML.writeProperty("DAV::exclusive");
		} else {
			generatedXML.writeProperty("DAV::shared");
		}
		generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);

		int depth = lo.getLockDepth();

		generatedXML.writeElement("DAV::depth", XMLWriter.OPENING);
		if (depth == INFINITY) {
			generatedXML.writeText("Infinity");
		} else {
			generatedXML.writeText(String.valueOf(depth));
		}
		generatedXML.writeElement("DAV::depth", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::owner", XMLWriter.OPENING);
		generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
		generatedXML.writeText(_lockOwner);
		generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
		generatedXML.writeElement("DAV::owner", XMLWriter.CLOSING);

		long timeout = lo.getTimeoutMillis();
		generatedXML.writeElement("DAV::timeout", XMLWriter.OPENING);
		generatedXML.writeText("Second-" + timeout / 1000);
		generatedXML.writeElement("DAV::timeout", XMLWriter.CLOSING);

		String lockToken = lo.getID();
		generatedXML.writeElement("DAV::locktoken", XMLWriter.OPENING);
		generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
		generatedXML.writeText("opaquelocktoken:" + lockToken);
		generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
		generatedXML.writeElement("DAV::locktoken", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::activelock", XMLWriter.CLOSING);
		generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.CLOSING);
		generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);

		resp.addHeader("Lock-Token", "<opaquelocktoken:" + lockToken + ">");

		generatedXML.sendData();

		resp.setEntity(new StringRepresentation(sw.toString(), MediaType.TEXT_XML, Language.ALL, CharacterSet.UTF_8)) ;
		resp.setStatus(WebdavStatus.SC_OK);
	}

	/**
	 * Executes the lock for a Mac OS Finder client
	 */
	private void doMacLockRequestWorkaround(ITransaction transaction, InnerRequest req, InnerResponse resp) throws LockFailedException, IOException {
		LockedObject lo;
		int depth = getDepth(req);
		int lockDuration = getTimeout(transaction, req);
		if (lockDuration < 0 || lockDuration > MAX_TIMEOUT)
			lockDuration = DEFAULT_TIMEOUT;

		boolean lockSuccess = false;
		lockSuccess = _resourceLocks.exclusiveLock(transaction, _path, _lockOwner, depth, lockDuration);

		if (lockSuccess) {
			// Locks successfully placed - return information about
			lo = _resourceLocks.getLockedObjectByPath(transaction, _path);
			if (lo != null) {
				generateXMLReport(transaction, resp, lo);
			} else {
				resp.sendError(WebdavStatus.SC_INTERNAL_SERVER_ERROR);
			}
		} else {
			// Locking was not successful
			sendLockFailError(transaction, req, resp);
		}
	}

	/**
	 * Sends an error report to the client
	 */
	private void sendLockFailError(ITransaction transaction, InnerRequest req, InnerResponse resp) throws IOException {
		Hashtable<String, Integer> errorList = new Hashtable<String, Integer>();
		errorList.put(_path, WebdavStatus.SC_LOCKED);
		sendReport(req, resp, errorList);
	}

}