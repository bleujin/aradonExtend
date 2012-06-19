package net.ion.radon.impl.let.system;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;

import net.ion.framework.util.Debug;
import net.ion.framework.util.PathMaker;
import net.ion.framework.util.StringUtil;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.SessionQuery;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class FileLet extends MongoDefaultLet {

	private final static String OBJECT_ID = "oid" ;
	
	public FileLet() {
		super(MediaType.ALL);
	}

	@Override
	protected Representation myGet() throws IOException {
		Node target = getSession().createQuery().id(getObjectId()).findOne() ;
		if (target == null) {
			throw new ResourceException(Status.SUCCESS_NO_CONTENT, "objectId:" + getObjectId()) ;
		} else {

			File file = new File(getRealFileValue(target));
			final String filename = StringUtil.toString(target.get("filename"));
			String localName = isExplorer() ? URLEncoder.encode(filename, "UTF-8") : new String(filename.getBytes("utf-8"), "latin1") ;
			
			String extension = StringUtil.substringAfterLast(filename, ".") ;
			MediaType mtype = getMetadataService().getMediaType(extension) ;
			
			final FileRepresentation result = new FileRepresentation(file, mtype);
			Disposition dis = new Disposition(Disposition.TYPE_ATTACHMENT) ;
			dis.setFilename(localName) ;
			// result.setLanguages(ListUtil.toList(Language.valueOf("ko"))) ;
			result.setDisposition(dis) ;
			
			return result;
		}
	}

	private String getBasePath(){
		return getContext().getAttributeObject("base.path", "./", String.class) ;
	}
	
	private String getRealFileValue(String oId) {
		return PathMaker.getFilePath(getBasePath(), StringUtil.toString(createQuery().id(oId).findOne().get("relPath")));
	}
	private String getRealFileValue(Node node) {
		Debug.line(getBasePath(), node.get("relPath"), node) ;

		return PathMaker.getFilePath(getBasePath(), StringUtil.toString(node.get("relPath")));
	}

	private boolean contains(String refId) {
		return createQuery().eq("resource", refId).count() > 0;
	}
	
	private Session getSession(){
		return  getSession("_file") ;
	}
	
	private SessionQuery createQuery(){
		return getSession().createQuery() ;
	}

	@Override
	protected Representation myHead() {
		Representation rep = null;
		String refId = getObjectId();
		if (contains(refId)) {
			String relPath = getRealFileValue(refId);
			File file = new File(PathMaker.getFilePath(getBasePath(), relPath));
			
			rep = new OutputRepresentation(MediaType.APPLICATION_OCTET_STREAM) {
				@Override
				public void write(OutputStream outputstream) throws IOException {
					outputstream.close() ;
				}
			};
			
			rep.setModificationDate(new Date(file.lastModified())) ;
			rep.setSize(file.length()) ;
			
			return rep;
		} else {
			setStatus(Status.SUCCESS_NO_CONTENT, "refId:" + refId);
		}
		return rep;
	}

	@Override
	protected Representation myDelete() throws IOException, ResourceException  {
		Node removed = getSession().createQuery().id(getObjectId()).findOne() ;
		int count = getSession().createQuery().id(getObjectId()).remove() ;
		if (count != 0) {
			File file = new File(getRealFileValue(removed));
			if (file != null && file.exists()) file.delete();
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "objectId:" + getObjectId()) ;
		}
		return toRepresentation(removed);
	}

	@Override
	protected Representation myPut(Representation entity) throws IOException, FileUploadException, ResourceException  {
		if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {

			Node removed = getSession().createQuery().id(getObjectId()).findOne() ;
			if (removed == null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "oid:" + getObjectId()) ;
			new File(getRealFileValue(removed)).delete() ;
			
			removed.clearProp() ;
			saveFile(removed);
			return toRepresentation(removed);
		} else {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Multipart/form-data required.[" + entity.getMediaType() + "]");
		}
	}

	private String getObjectId() {
		return getInnerRequest().getAttribute(OBJECT_ID);
	}

	// Create.
	@Override
	protected Representation myPost(Representation entity) throws IOException, FileUploadException, ResourceException {

		if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)){ // put mutation
			Node node = getSession().newNode() ;
			saveFile(node);
			
			
			return toRepresentation(node);
		}
		throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Multipart/form-data required.[" + entity.getMediaType() + "]");
	}



	private void saveFile(Node node) throws IOException, FileNotFoundException {
		for (Entry<String, Serializable> entry : getInnerRequest().getFormParameter().entrySet()) {
			if (entry.getValue() instanceof FileItem) { 
				FileItem fitem = (FileItem) entry.getValue() ;
				node.put("size", fitem.getSize());
				node.put("content-type", fitem.getContentType());
				node.put("filename", URLDecoder.decode(fitem.getName(), "UTF-8"));
				node.put("fieldname", fitem.getFieldName());
				String ext = StringUtil.substringAfterLast(fitem.getName(), ".");
				final String abstractName = node.getIdentifier() + "." + ext;

				saveFile(fitem.getInputStream(), node, abstractName, fitem.getName());
			} else{ // not file
				node.put(entry.getKey(), entry.getValue()) ;
			}
		}
		getSession().commit();
	}
	private void saveFile(InputStream input, Node node, final String abstractName, String uploadName) throws IOException, FileNotFoundException {
		OutputStream output = null;
		String parentDir = getDestDir(getBasePath()).getAbsolutePath();

		File file = new File(PathMaker.getFilePath(parentDir, abstractName));
		
		try {
			output = new FileOutputStream(file);

	        byte buffer[] = new byte[4096];
	        long count = 0L;
	        for(int n = 0; -1 != (n = input.read(buffer));){
	            output.write(buffer, 0, n);
	            count += n;
	        }
		} finally {
			IOUtils.closeQuietly(input);
			IOUtils.closeQuietly(output);
		}

		
		String absolutePath = StringUtil.substringAfter(StringUtil.replace(file.getAbsolutePath(), "\\", "/"), StringUtil.replace(getBasePath(), "\\", "/"));

		Debug.line(file.getAbsolutePath(), getBasePath(), absolutePath) ;
		node.put("relPath", absolutePath);
	}
	private File getDestDir(String basePath) {

		String yearDir = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		String monthDir = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
		String dateDir = String.valueOf(Calendar.getInstance().get(Calendar.DATE));

		String destDirPath = PathMaker.getFilePath(basePath, yearDir + SystemUtils.FILE_SEPARATOR + monthDir + SystemUtils.FILE_SEPARATOR + dateDir);
		File distDir = new File(destDirPath);
		if (!distDir.exists()) {
			distDir.mkdirs();
		}

		return distDir;
	}
}
