package net.sf.webdav;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ion.framework.logging.LogBroker;
import net.ion.radon.core.let.AbstractServerResource;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.sf.webdav.exceptions.UnauthenticatedException;
import net.sf.webdav.exceptions.WebdavException;
import net.sf.webdav.fromcatalina.MD5Encoder;
import net.sf.webdav.locking.ResourceLocks;
import net.sf.webdav.methods.DoCopy;
import net.sf.webdav.methods.DoDelete;
import net.sf.webdav.methods.DoGet;
import net.sf.webdav.methods.DoHead;
import net.sf.webdav.methods.DoLock;
import net.sf.webdav.methods.DoMkcol;
import net.sf.webdav.methods.DoMove;
import net.sf.webdav.methods.DoNotImplemented;
import net.sf.webdav.methods.DoOptions;
import net.sf.webdav.methods.DoPropfind;
import net.sf.webdav.methods.DoProppatch;
import net.sf.webdav.methods.DoPut;
import net.sf.webdav.methods.DoUnlock;

import org.restlet.data.ClientInfo;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.engine.header.Header;
import org.restlet.representation.Representation;
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
				init() ;
				Representation result = doHandle(getInnerRequest(), getInnerResponse());

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

//	public Representation doHandle() throws ResourceException {
//
//		getInnerRequest().putAttribute("scheme", "template");
//
//		Method method = getMethod();
//		allowOtherHost();
//
//		VFSEntry entry = getContext().getAttributeObject(VFSEntry.class.getCanonicalName(), VFSEntry.class);
//		FileSystemEntry fileSystemEntry = entry.getFileSystemEntry();
//		final VFileStore store = VFileStore.create(fileSystemEntry);
//
//		User user = getRequest().getClientInfo().getUser();
//		ITransaction transaction = store.begin(user);
//
//		try {
//			if (method == null) {
//				throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, "No method specified");
//			} else if (Method.GET.equals(method)) {
//				DoGet.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.PUT.equals(method)) {
//				DoPut.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.POST.equals(method)) {
//				DoPut.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.DELETE.equals(method)) {
//				DoDelete.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.HEAD.equals(method)) {
//				DoHead.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.OPTIONS.equals(method)) {
//				DoOptions.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.PROPFIND.equals(method)) {
//				DoPropfind.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.COPY.equals(method)) {
//				DoCopy.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.MOVE.equals(method)) {
//				DoMove.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.PROPPATCH.equals(method)) {
//				DoProppatch.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//			} else if (Method.MKCOL.equals(method)) {
//				DoMkcol.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//				// } else if (Method.LOCK.equals(method)) {
//				// DoLock.create(store).handle(transaction, getInnerRequest(), getInnerResponse());
//				// } else if (Method.UNLOCK.equals(method)) {
//				// getResponse().setStatus(Status.SUCCESS_NO_CONTENT) ;
//			} else {
//				throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED, "No method specified");
//			}
//			store.commit(transaction);
//		} finally {
//			getRequest().release();
//		}
//
//		return getResponse().getEntity();
//		// return new StringRepresentation(text, MediaType.APPLICATION_XML) ;
//	}

	public void init() throws ServletException {

		// Parameters from web.xml
		String clazzName = LocalFileSystemStore.class.getName() ;

		File root = getFileRoot();

		IWebdavStore webdavStore = constructStore(clazzName, root);

		boolean lazyFolderCreationOnPut = false ;

		String dftIndexFile = "index.html" ; // getInitParameter("default-index-file");
		String insteadOf404 = "404.html" ;// getInitParameter("instead-of-404");

		int noContentLengthHeader = -1 ; // getIntInitParameter("no-content-length-headers");

		superinit(webdavStore, dftIndexFile, insteadOf404, noContentLengthHeader, lazyFolderCreationOnPut);
	}
	
	
	
	
	private static Logger LOG = LogBroker.getLogger(WebDavLet.class);

	/**
	 * MD5 message digest provider.
	 */
	private static MessageDigest MD5_HELPER;

	/**
	 * The MD5 helper object for this class.
	 */
	private static final MD5Encoder MD5_ENCODER = new MD5Encoder();

	private static final boolean READ_ONLY = false;
	private ResourceLocks _resLocks;
	private IWebdavStore _store;
	private HashMap<String, IMethodExecutor> _methodMap = new HashMap<String, IMethodExecutor>();

	

	protected IWebdavStore constructStore(String clazzName, File root) {
		IWebdavStore webdavStore;
		try {
			Class<?> clazz = WebdavServlet.class.getClassLoader().loadClass(clazzName);

			Constructor<?> ctor = clazz.getConstructor(new Class[] { File.class });

			webdavStore = (IWebdavStore) ctor.newInstance(new Object[] { root });
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("some problem making store component", e);
		}
		return webdavStore;
	}

	private File getFileRoot() {
		String rootPath = "./";  //getInitParameter(ROOTPATH_PARAMETER);
		if (rootPath.equals("*WAR-FILE-ROOT*")) {
			String file = LocalFileSystemStore.class.getProtectionDomain().getCodeSource().getLocation().getFile().replace('\\', '/');
			if (file.charAt(0) == '/' && System.getProperty("os.name").indexOf("Windows") != -1) {
				file = file.substring(1, file.length());
			}

			int ix = file.indexOf("/WEB-INF/");
			if (ix != -1) {
				rootPath = file.substring(0, ix).replace('/', File.separatorChar);
			} else {
				throw new WebdavException("Could not determine root of war file. Can't extract from path '" + file + "' for this web container");
			}
		}
		return new File(rootPath);
	}
	
	

	public void superinit(IWebdavStore store, String dftIndexFile, String insteadOf404, int nocontentLenghHeaders, boolean lazyFolderCreationOnPut) throws ServletException {

		_store = store;
		_resLocks = new ResourceLocks() ;

//		IMimeTyper mimeTyper = new IMimeTyper() {
//			public String getMimeType(String path) {
//				return getServletContext().getMimeType(path);
//			}
//		};

		IMimeTyper mimeTyper = new IMimeTyper() {
			public String getMimeType(String path) {
				return MediaType.valueOf(path).toString();
			}
		};
		
		register("GET", new DoGet(store, dftIndexFile, insteadOf404, _resLocks, mimeTyper, nocontentLenghHeaders));
		register("HEAD", new DoHead(store, dftIndexFile, insteadOf404, _resLocks, mimeTyper, nocontentLenghHeaders));
		DoDelete doDelete = (DoDelete) register("DELETE", new DoDelete(store, _resLocks, READ_ONLY));
		DoCopy doCopy = (DoCopy) register("COPY", new DoCopy(store, _resLocks, doDelete, READ_ONLY));
		register("LOCK", new DoLock(store, _resLocks, READ_ONLY));
		register("UNLOCK", new DoUnlock(store, _resLocks, READ_ONLY));
		register("MOVE", new DoMove(_resLocks, doDelete, doCopy, READ_ONLY));
		register("MKCOL", new DoMkcol(store, _resLocks, READ_ONLY));
		register("OPTIONS", new DoOptions(store, _resLocks));
		register("PUT", new DoPut(store, _resLocks, READ_ONLY, lazyFolderCreationOnPut));
		register("PROPFIND", new DoPropfind(store, _resLocks, mimeTyper));
		register("PROPPATCH", new DoProppatch(store, _resLocks, READ_ONLY));
		register("*NO*IMPL*", new DoNotImplemented(READ_ONLY));
	}

	private IMethodExecutor register(String methodName, IMethodExecutor method) {
		_methodMap.put(methodName, method);
		return method;
	}

	/**
	 * Handles the special WebDAV methods.
	 */
	public Representation doHandle(InnerRequest req, InnerResponse resp) throws ServletException, IOException {

		String methodName = req.getMethod().getName();
		ITransaction transaction = null;
		boolean needRollback = false;

		if (LOG.isLoggable(Level.INFO))
			debugRequest(methodName, req);

		try {
			ClientInfo userPrincipal = req.getClientInfo();
			transaction = _store.begin(userPrincipal);
			needRollback = true;
			_store.checkAuthentication(transaction);
			resp.setStatus(Status.SUCCESS_OK);

			try {
				IMethodExecutor methodExecutor = (IMethodExecutor) _methodMap.get(methodName);
				if (methodExecutor == null) {
					methodExecutor = (IMethodExecutor) _methodMap.get("*NO*IMPL*");
				}

				methodExecutor.execute(transaction, req, resp);

				_store.commit(transaction);
				needRollback = false;
			} catch (IOException e) {
				java.io.StringWriter sw = new java.io.StringWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(sw);
				e.printStackTrace(pw);
				LOG.warning("IOException: " + sw.toString());
				resp.setStatus(Status.SERVER_ERROR_INTERNAL);
				_store.rollback(transaction);
				throw new ServletException(e);
			}

		} catch (UnauthenticatedException e) {
			resp.setStatus(Status.CLIENT_ERROR_FORBIDDEN);
		} catch (WebdavException e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			LOG.warning("WebdavException: " + sw.toString());
			throw new ServletException(e);
		} catch (Exception e) {
			java.io.StringWriter sw = new java.io.StringWriter();
			java.io.PrintWriter pw = new java.io.PrintWriter(sw);
			e.printStackTrace(pw);
			LOG.warning("Exception: " + sw.toString());
		} finally {
			if (needRollback)
				_store.rollback(transaction);
		}
		return resp.getEntity() ;

	}

	private void debugRequest(String methodName, InnerRequest req) {
		LOG.info("-----------");
		LOG.info("WebdavServlet\n request: methodName = " + methodName);
		LOG.info("time: " + System.currentTimeMillis());
		LOG.info("path: " + req.getResourceRef());
		LOG.info("-----------");
		for (String headerName :  (Set<String>)req.getHeaders().getNames()) {
			LOG.info("header: " + headerName + " " + req.getHeader(headerName));
		}

		for (Entry<String, Object> entry : req.getFormParameter().entrySet()) {
			LOG.info("parameter: " + entry.getKey() + " " + entry.getValue());
		}
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
