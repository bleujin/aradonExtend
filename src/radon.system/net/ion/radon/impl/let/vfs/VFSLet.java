package net.ion.radon.impl.let.vfs;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.MapUtil;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFile;
import net.ion.radon.core.let.AbstractLet;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ResourceException;

public class VFSLet extends AbstractLet {

	// protocol/path1/path2/..

	@Override
	protected Representation myDelete() throws Exception {
		VFile vfile = getVirtualFile();
		boolean deleted = vfile.delete();
		return makeRepresentation(vfile);
	}

	@Override
	protected Representation myGet() throws Exception {
		VFile vfile = getVirtualFile();

		if (vfile.getType().equals(FileType.IMAGINARY)) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, vfile.getName().getPath() + " not found") ;

		if (vfile.getType().hasChildren() || vfile.getType().equals(FileType.FOLDER)) {
			Debug.debug(vfile.getName());
			List<Map<String, ?>> result = new ListUtil().newList() ;
			
			List<VFile> children = vfile.getChildren() ;
			for (VFile vf : children) {
				Map<String, Object> info = MapUtil.newMap() ;
				info.put("name", vf.getName().getBaseName()) ;
				info.put("path", vf.getName().getPath()) ;
				info.put("url", vf.getName().getURI()) ;
				info.put("size", vf.isFile() ? vf.getLengthAsInt() : 0) ;
				info.put("lastmodified", vf.getContent().getLastModifiedTime()) ;
				result.add(info) ;
			}
			return toRepresentation(result) ;
		} else {

			Debug.debug(vfile.getName().getPath(), vfile.getContent().getContentType());
			MediaType mediaType = MediaType.valueOf(vfile.getContent().getContentType());

			return new InputRepresentation(vfile.getInputStream(), mediaType);
		}
	}

	private VFile getVirtualFile() throws FileSystemException {
		VFSEntry entry = getContext().getAttributeObject("system.vfs.entry", VFSEntry.class);
		FileSystemEntry fe = entry.getFileSystemEntry();
		return fe.resolveFile(getPath());
	}

	private String getPath() {
		String scheme = getInnerRequest().getAttribute("scheme");
		return scheme + "://" + getInnerRequest().getRemainPath();
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		VFile vfile = getVirtualFile();

		if (vfile.exists()) {
			throw new ResourceException(Status.CLIENT_ERROR_CONFLICT, vfile.getName().getPath() + " exists");
		}

		return myPut(entity);
	}

	private StringRepresentation makeRepresentation(VFile vfile) {
		return new StringRepresentation(vfile.getName().getPath());
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		VFile vfile = getVirtualFile();

		InputStream input = entity.getStream();

		OutputStream output = vfile.getOutputStream();
		IOUtil.copyNClose(input, output) ;
		vfile.close();

		return makeRepresentation(vfile);
	}

	private String getTargetPath() {
		return getRequest().getResourceRef().getRemainingPart();
	}
}
