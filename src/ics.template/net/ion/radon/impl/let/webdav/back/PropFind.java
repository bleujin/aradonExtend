package net.ion.radon.impl.let.webdav.back;


public class PropFind {
	/*final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss";

	private Request request;
	private VFile target;
	private DocumentBuilder builder;
	private WebDavLet let;

	public PropFind(WebDavLet let, VFile vfile, DocumentBuilder builder) {
		this.request = let.getRequest();
		this.target = vfile;
		this.builder = builder;
		this.let = let;
	}

	public static PropFind create(WebDavLet let, VFile vfile, DocumentBuilder builder) throws IOException, SAXException {
		final PropFind propFind = new PropFind(let, vfile, builder);
		propFind.init();
		return propFind;
	}

	private List<DavProperty> reqProperty = null;
	private Document mydoc = null;

	private void init() throws IOException, SAXException {
		this.reqProperty = makeRequestPropList(request);
		this.mydoc = builder.newDocument();
	}

	private Document getDocument() {
		return mydoc;
	}

	private List<DavProperty> getRequestProperties() {
		return reqProperty;
	}

	final static String WEBDAV_NAMESPACE = "DAV:";
	final static String PREFIX = "D";

	public Representation handle() throws IOException, SAXException {
		if (!target.exists()) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}

		getDocument().createElementNS(WEBDAV_NAMESPACE, PREFIX);
		getDocument().createElementNS("urn:schemas-microsoft-com:datatypes", "M:preload2");

		final Element multistatus = createElementNS("multistatus");
		getDocument().appendChild(multistatus);

		// if (! let.isNoRoot() && (! "/".equals(target.getName().getPath())) ){
		if (let.getDepth() == 0) {
			parseProperty(0, multistatus, target);
		} else {
			recursivePropfind(let.getDepth(), multistatus, target);
		}

		final DomRepresentation rep = new DomRepresentation(MediaType.APPLICATION_XML, getDocument());
		// rep.setNamespaceAware(false) ;
		rep.setIndenting(true);

		// Debug.debug(rep.getText()) ;

		return new StringRepresentation(rep.getText(), MediaType.APPLICATION_XML);
	}

	private Element createElementNS(String tagName) {
		Element ele = getDocument().createElementNS(WEBDAV_NAMESPACE, tagName);
		ele.setPrefix(PREFIX);
		return ele;
	}

	private void recursivePropfind(final int remainDepth, final Element parent, final VFile vfile) throws FileSystemException, DOMException {

		parseProperty(remainDepth, parent, vfile);

		if (vfile.isDir() && (remainDepth > 0)) {
			final List<VFile> children = vfile.getChildren();
			for (final VFile child : children)
				recursivePropfind(remainDepth - 1, parent, child);
		}
	}

	private void parseProperty(final int remainDepth, final Element parent, final VFile vfile) throws FileSystemException {
		String baseName = WebDavUtil.escape(vfile.getName().getBaseName());
		if ("".equals(baseName))
			baseName = "/";
		String hrefPath = vfile.getName().getPath() + (vfile.isDir() ? "/" : "");
		if ("//".equals(hrefPath))
			hrefPath = "/";
		boolean isDirExpression = vfile.isDir();
		String displayName = baseName;

		// VFSMetadata md = vfs.getMetadata(uri);
		if (vfile.getFileObject().isHidden())
			return;

		// set up basic response element
		final Element response = createElementNS("response");
		final Element href = createElementNS("href");

		href.appendChild(createTextNode(hrefPath));

		response.appendChild(href);

		// set up all properties we actually know about

		final Map<String, Element> properties = new HashMap<String, Element>();
		final Element creationdate = createElementNS("creationdate");
		// creationdate.setAttributeNS("urn:schemas-microsoft-com:datatypes", "dt", "dateTime.tz");
		creationdate.appendChild(createTextNode(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date(vfile.getContent().getLastModifiedTime())) + "Z"));
		properties.put(new DavProperty(WEBDAV_NAMESPACE, "creationdate").toString(), creationdate);

		final Element resourcetype = createElementNS("resourcetype");
		properties.put(new DavProperty(WEBDAV_NAMESPACE, "resourcetype").toString(), resourcetype);
		if (isDirExpression)
			resourcetype.appendChild(createElementNS("collection"));

		final Element name = createElementNS("name");
		name.appendChild(createTextNode(displayName));
		properties.put(new DavProperty(WEBDAV_NAMESPACE, "name").toString(), name);

		final Element displayname = createElementNS("displayname");
		displayname.appendChild(createTextNode(displayName));
		properties.put(new DavProperty(WEBDAV_NAMESPACE, "displayname").toString(), displayname);

		// These are placeholders; locking is non-functional
		// final Element lockdiscovery = createElementNS("lockdiscovery");
		// properties.put(new DavProperty(WEBDAV_NAMESPACE, "lockdiscovery").toString(), lockdiscovery);
		// final Element supportedlock = createElementNS("supportedlock");
		// properties.put(new DavProperty(WEBDAV_NAMESPACE, "supportedlock").toString(), supportedlock);

		// final Element isroot = createElementNS("isroot");
		// isroot.appendChild(createTextNode(isTarget(vfile) ? "1" : "0"));
		// isroot.setAttributeNS("urn:schemas-microsoft-com:datatypes", "dt", "boolean");
		// properties.put(new DavProperty(WEBDAV_NAMESPACE, "isroot").toString(), isroot);

		// final Element ishidden = createElementNS("ishidden");
		// ishidden.appendChild(createTextNode(isTarget(vfile) ? "1" : "0"));
		// ishidden.setAttributeNS("urn:schemas-microsoft-com:datatypes", "dt", "boolean");
		// properties.put(new DavProperty(WEBDAV_NAMESPACE, "ishidden").toString(), ishidden);

		// final Element iscollection = createElementNS(doc, "iscollection");
		// iscollection.appendChild(doc.createTextNode(vfile.isDir() ? "1" : "0"));
		// iscollection.setAttributeNS("urn:schemas-microsoft-com:datatypes", "dt", "boolean");
		// properties.put(new DavProperty(WEBDAV_NAMESPACE, "iscollection").toString(), iscollection);

		if (vfile.isFile()) {

			final Element contentlength = createElementNS("getcontentlength");
			contentlength.appendChild(createTextNode("" + vfile.getContent().getSize()));
			contentlength.setAttributeNS("urn:schemas-microsoft-com:datatypes", "dt", "int");
			properties.put(new DavProperty(WEBDAV_NAMESPACE, "getcontentlength").toString(), contentlength);

			final Element getcontenttype = createElementNS("getcontenttype");
			getcontenttype.appendChild(createTextNode(StringUtil.defaultIfEmpty(vfile.getContent().getContentType(), "application/binary")));
			properties.put(new DavProperty(WEBDAV_NAMESPACE, "getcontenttype").toString(), getcontenttype);

			final Element getlastmodified = createElementNS("getlastmodified");
			// getlastmodified.setAttributeNS("urn:schemas-microsoft-com:datatypes", "dt", "dateTime.rfc1123");
			getlastmodified.appendChild(createTextNode(DateUtil.toHTTPDateFormat(new Date(vfile.getContent().getLastModifiedTime()))));
			properties.put(new DavProperty("DAV:", "getlastmodified").toString(), getlastmodified);

			final Element etag = createElementNS("getetag");
			etag.appendChild(createTextNode("" + vfile.getETag()));
			properties.put(new DavProperty(WEBDAV_NAMESPACE, "getetag").toString(), etag);

		}
		// lm = new SimpleDateFormat(HTTP_DATE_FORMAT).format(lmd);
		// final Element getlastmodified = doc.createElementNS("DAV:", "getlastmodified");
		// getlastmodified.setAttributeNS("urn:schemas-microsoft-com:datatypes", "dt", "dateTime.rfc1123");
		// getlastmodified.appendChild(doc.createTextNode(lm + " GMT"));
		// properties.put(new DavProperty("DAV:", "getlastmodified").toString(), getlastmodified);

		// propstat for valid responses
		final Element propstat = createElementNS("propstat");
		response.appendChild(propstat);
		final Element prop = createElementNS("prop");
		propstat.appendChild(prop);
		final Element status = createElementNS("status");
		status.appendChild(createTextNode("HTTP/1.1 200 OK"));
		propstat.appendChild(status);
		final List<DavProperty> missing = new ArrayList<DavProperty>();
		if (getRequestProperties().size() > 0) {
			for (DavProperty dp : getRequestProperties()) {
				if (properties.containsKey(dp.toString()))
					// if(d) log.debug("Including requested property"+dp.getNamespace()+" / "+dp.getProperty());
					prop.appendChild(properties.get(dp.toString()));
				else
					missing.add(dp);
			}
		} else {
			for (Element ele : properties.values()) {
				prop.appendChild(ele);
			}
		}

		if (missing.size() > 0) {
			// propstat for missing responses
			final Element propstat2 = createElementNS("propstat");
			response.appendChild(propstat2);
			final Element prop2 = createElementNS("prop");
			propstat2.appendChild(prop2);
			final Element status2 = createElementNS("status");
			status2.appendChild(createTextNode("HTTP/1.1 404 Not Found"));
			propstat2.appendChild(status2);
			final Iterator<DavProperty> it = missing.iterator();
			while (it.hasNext()) {
				final DavProperty dp = it.next();
				prop2.appendChild(getDocument().createElementNS(dp.getNamespace(), dp.getProperty()));
				// if(d) log.debug("Missing requested property"+dp.getNamespace()+" / "+dp.getProperty());
			}
		}
		parent.appendChild(response);
	}

	private boolean isRootDir(final int remainDepth) {
		return let.getDepth() == remainDepth;
	}

	private boolean isTarget(VFile vfile) {
		return target.getName().getPath().equals(vfile.getName().getPath());
	}

	private Node createTextNode(String data) {
		return getDocument().createTextNode(data);
	}

	private List<DavProperty> makeRequestPropList(Request request) throws IOException, SAXException {
		final List<DavProperty> reqProperties = ListUtil.newList();

		if (request.getEntity() == null)
			return ListUtil.newList();
		final InputStream input = request.getEntity().getStream();
		if (input == null)
			return ListUtil.newList();

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IOUtil.copyNClose(input, output);

		final Document doc = builder.parse(new ByteArrayInputStream(output.toByteArray()));

		final Element root = doc.getDocumentElement();
		final Element propList = (Element) root.getElementsByTagNameNS("DAV:", "prop").item(0);
		if (propList == null)
			return ListUtil.newList();
		final NodeList props = propList.getChildNodes();
		for (int i = 0; i < props.getLength(); i++) {
			final Node n = props.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				final Element e = (Element) n;
				final DavProperty dp = new DavProperty(e.getNamespaceURI(), e.getLocalName());
			
				reqProperties.add(dp);
			}
		}
		return reqProperties;
	}*/

}
