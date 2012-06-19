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
package net.ion.radon.impl.let.webdav.methods;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Hashtable;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ObjectId;
import net.ion.framework.util.StringUtil;
import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.VFileStore;
import net.ion.radon.impl.let.webdav.exceptions.LockFailedException;
import net.ion.radon.impl.let.webdav.exceptions.WebdavException;
import net.ion.radon.impl.let.webdav.fromcatalina.XMLWriter;
import net.ion.radon.impl.let.webdav.locking.LockManager;
import net.ion.radon.impl.let.webdav.locking.LockedObject;
import net.ion.radon.impl.let.webdav.locking.MemLockManager;

import org.restlet.Request;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class DoLock extends AbstractMethod {

	private boolean macLockRequest = false;
	private String type = null;
	private String lockOwner = null;
	private String userAgent = null;
	private String path = null ;

	private static LockManager lockManager = new MemLockManager() ;
	private DoLock(VFileStore store) {
		super(store, Method.LOCK);
	}

	public static DoLock create(VFileStore store) {
		return new DoLock(store);
	}

	protected Representation myHandle(ITransaction transaction, InnerRequest request, InnerResponse response) throws IOException, LockFailedException, SAXException {
		this.path = getRelativePath(request);

		VFile vfile = resolveFile(transaction, request.getAttribute("scheme"), getRelativePath(request));

		Hashtable<String, Status> errorList = new Hashtable<String, Status>();

		// Mac OS Finder (whether 10.4.x or 10.5) can't store files because executing a LOCK without lock information causes a SC_BAD_REQUEST
		userAgent = request.getHeader("User-Agent");
		if (userAgent != null && userAgent.indexOf("Darwin") != -1) {
			macLockRequest = true;

			String timeString = new Long(System.currentTimeMillis()).toString();
			lockOwner = userAgent.concat(timeString);
		}

		String tempLockOwner = "doLock" + System.currentTimeMillis() + request.toString();
		if (request.getHeader("If") != null) {
			return doRefreshLock(transaction, request, response);
		} else {
			// return doRefreshLock(transaction, request, response);
			return doLock(transaction, request, response, vfile);
		}
	}

	private Representation doLock(ITransaction transaction, InnerRequest request, InnerResponse response, VFile vfile) throws IOException, LockFailedException, SAXException {
		type = null;
		lockOwner = null;

		if (vfile.exists())
			return doLocking(transaction, request, response, vfile);
		else
			return doNullResourceLock(transaction, request, response, vfile);
	}

	private Representation doLocking(ITransaction transaction, InnerRequest request, InnerResponse response, VFile vfile) throws IOException, SAXException {

		LockedObject lo = lockManager.getWriteLockedObjectByPath(transaction, path);
		if (lo != null) {
			if (lo.isExclusive()) {
				throw WebdavException.create(Status.CLIENT_ERROR_LOCKED) ;
			}
		}
		try {
			return executeLock(transaction, request, response, vfile);
		} catch (LockFailedException e) {
			throw WebdavException.create(Status.CLIENT_ERROR_LOCKED) ;
		}
	}

	private Representation doNullResourceLock(ITransaction transaction, InnerRequest request, InnerResponse response, VFile vfile) throws IOException, SAXException {

		try {
			VFile parentSo = vfile.getParent();
			// Transmit expects 204 response-code, not 201
			if (userAgent != null && userAgent.indexOf("Transmit") != -1) {
				Debug.trace("DoLock.execute() : do workaround for user agent '" + userAgent + "'");
				response.setStatus(Status.SUCCESS_NO_CONTENT);
			} else {
				response.setStatus(Status.SUCCESS_CREATED);
			}
			return executeLock(transaction, request, response, vfile);

		} catch (LockFailedException e) {
			throw WebdavException.create(Status.CLIENT_ERROR_LOCKED) ;
		}
	}

	private Representation doRefreshLock(ITransaction transaction, InnerRequest request, InnerResponse response) throws IOException, LockFailedException {

		String lockToken = getLockIdFromIfHeader(request);

		// Getting LockObject of specified lockToken in If header
		LockedObject lo = lockManager.getWriteLockedObjectByID(transaction, lockToken);
		if (lo != null) {
			int timeout = getTimeout(transaction, request);

			lo.refreshTimeout(timeout);
			// sending success response
			return generateXMLReport(transaction, response);
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_PRECONDITION_FAILED);
		}
	}

	private String getLockIdFromIfHeader(InnerRequest request) {
		String id = request.getHeader("If");
		if (StringUtil.isNotBlank(id)) {
			String firstId = id.substring(id.indexOf("(<"), id.indexOf(">)"));
			if (firstId.indexOf("locktoken:") != -1) {
				firstId = firstId.substring(firstId.indexOf(':') + 1);
			}
			return firstId;
		} else {
			return "" ;
		}
	}


	/**
	 * Executes the LOCK
	 * 
	 * @throws SAXException
	 * @throws
	 */
	private Representation executeLock(ITransaction transaction, InnerRequest request, InnerResponse response, VFile vfile) throws LockFailedException, IOException, SAXException {

		// Mac OS lock request workaround
		if (macLockRequest) {
			Debug.trace("DoLock.execute() : do workaround for user agent '" + userAgent + "'");

			return doMacLockRequestWorkaround(transaction, request, response);
		} else {
			// Getting LockInformation from request
			if (getLockInformation(transaction, request)) {
				int depth = getDepth(request);
				int lockDuration = getTimeout(transaction, request);

				boolean lockSuccess = lockManager.sharedLock(transaction, path, lockOwner, depth, lockDuration);

				if (lockSuccess) {
					// Locks successfully placed - return information about
					return generateXMLReport(transaction, response);
				} else {
					throw WebdavException.create(Status.CLIENT_ERROR_LOCKED) ;
				}
			} else {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
	}

	/**
	 * Tries to get the LockInformation from LOCK request
	 * 
	 * @throws SAXException
	 */
	private boolean getLockInformation(ITransaction transaction, Request request) throws IOException, SAXException {

		Node lockInfoNode = null;
		Document document = getDocBuilder().parse(new InputSource(request.getEntity().getStream()));

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
						type = currentNode.getNodeName();

						if (type.endsWith("write")) {
							type = "write";
						} else if (type.equals("read")) {
							type = "read";
						}
					}
				}
				if (type == null) {
					return false;
				}
			} else {
				return false;
			}

			if (lockOwnerNode != null) {
				childList = lockOwnerNode.getChildNodes();
				for (int i = 0; i < childList.getLength(); i++) {
					currentNode = childList.item(i);

					if (currentNode.getNodeType() == Node.ELEMENT_NODE || currentNode.getNodeType() == Node.TEXT_NODE) {
						lockOwner = currentNode.getTextContent();
					}
				}
			}
			if (lockOwner == null) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 * Ties to read the timeout from request
	 */
	private int getTimeout(ITransaction transaction, InnerRequest request) {

		int lockDuration = DEFAULT_TIMEOUT;
		String lockDurationStr = request.getHeader("Timeout");

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
	private Representation generateXMLReport(ITransaction transaction, InnerResponse response) throws IOException {

		HashMap<String, String> namespaces = new HashMap<String, String>();
		namespaces.put("DAV:", "D");

		StringWriter writer = new StringWriter();
		XMLWriter generatedXML = new XMLWriter(writer, namespaces);
		generatedXML.writeXMLHeader();
		generatedXML.writeElement("DAV::prop", XMLWriter.OPENING);
		generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.OPENING);
		generatedXML.writeElement("DAV::activelock", XMLWriter.OPENING);

		generatedXML.writeElement("DAV::locktype", XMLWriter.OPENING);
		generatedXML.writeProperty("DAV::" + type);
		generatedXML.writeElement("DAV::locktype", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::lockscope", XMLWriter.OPENING);
		generatedXML.writeProperty("DAV::shared");
		generatedXML.writeElement("DAV::lockscope", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::depth", XMLWriter.OPENING);
		generatedXML.writeText("1");
		generatedXML.writeElement("DAV::depth", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::owner", XMLWriter.OPENING);
		generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
		generatedXML.writeText(lockOwner);
		generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
		generatedXML.writeElement("DAV::owner", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::timeout", XMLWriter.OPENING);
		generatedXML.writeText("Second-1" );
		generatedXML.writeElement("DAV::timeout", XMLWriter.CLOSING);

		String lockToken = new ObjectId().toString() ;
		
		generatedXML.writeElement("DAV::locktoken", XMLWriter.OPENING);
		generatedXML.writeElement("DAV::href", XMLWriter.OPENING);
		generatedXML.writeText("opaquelocktoken:" + lockToken);
		generatedXML.writeElement("DAV::href", XMLWriter.CLOSING);
		generatedXML.writeElement("DAV::locktoken", XMLWriter.CLOSING);

		generatedXML.writeElement("DAV::activelock", XMLWriter.CLOSING);
		generatedXML.writeElement("DAV::lockdiscovery", XMLWriter.CLOSING);
		generatedXML.writeElement("DAV::prop", XMLWriter.CLOSING);

		response.setHeader("Lock-Token", "<opaquelocktoken:" + lockToken + ">");

		generatedXML.sendData();

		response.setStatus(Status.SUCCESS_OK);
		Debug.line('#', writer.getBuffer()) ;
		return new StringRepresentation(writer.getBuffer(), MediaType.valueOf("text/xml; charset=UTF-8"), Language.ALL, CharacterSet.UTF_8);
	}

	/**
	 * Executes the lock for a Mac OS Finder client
	 */
	private Representation doMacLockRequestWorkaround(ITransaction transaction, InnerRequest request, InnerResponse response) throws LockFailedException, IOException {
		int depth = getDepth(request);
		int lockDuration = getTimeout(transaction, request);
		if (lockDuration < 0 || lockDuration > MAX_TIMEOUT)
			lockDuration = DEFAULT_TIMEOUT;

		boolean lockSuccess = false;
		lockSuccess = lockManager.exclusiveLock(transaction, path, lockOwner, depth, lockDuration);

		if (lockSuccess) {
			// Locks successfully placed - return information about
			return generateXMLReport(transaction, response);
		} else {
			String path = getRelativePath(request);
			throw WebdavException.create(Status.CLIENT_ERROR_LOCKED) ;
		}
	}

}
