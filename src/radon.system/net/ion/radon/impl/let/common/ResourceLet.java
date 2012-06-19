package net.ion.radon.impl.let.common;

import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.PathMaker;
import net.ion.framework.util.StringUtil;
import net.ion.radon.core.let.AbstractLet;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.engine.local.ZipEntryRepresentation;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;

public class ResourceLet extends AbstractLet{

	@Override
	protected Representation myDelete() throws Exception {
		return notImpl();
	}

	@Override
	protected Representation myGet() throws Exception {
		String baseDir = getContext().getAttributeObject("base.dir", "./", String.class) ;
		String zipSuffix = getContext().getAttributeObject("zip.suffix.order", "zip", String.class) ;
		String[] suffixs = StringUtil.split(zipSuffix, ',');
		
		String name = getInnerRequest().getAttribute("name") ;
		String remainPart = getInnerRequest().getRemainPath().substring(1); // except first char(/)
		String relPath = name +'/' + remainPart ;
		
		
		String extension = StringUtil.substringAfterLast(relPath, ".") ;
		final MediaType mediaType = ObjectUtil.coalesce(getMetadataService().getMediaType(extension), MediaType.ALL);

		if (new File(PathMaker.getFilePath(baseDir, relPath)).exists()){
			final FileRepresentation result = new FileRepresentation(new File(PathMaker.getFilePath(baseDir, relPath)), mediaType);
			return result ;	
		} else {
			for (String suffix : suffixs) {
				final File zipfile = new File(PathMaker.getFilePath(baseDir, name + "." + suffix));
				if (zipfile.exists()){
					String entryName = remainPart ;
					final ZipFile zip = new ZipFile(zipfile);
					final ZipEntry entry = zip.getEntry(entryName) ;
					if (entry == null) throw new ResourceException(404) ;
					final ZipEntryRepresentation result = new ZipEntryRepresentation(mediaType, zip, entry);
					
					return result ;
				}
			}
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND) ;
		}
	}

	@Override
	protected Representation myPost(Representation entity) throws Exception {
		return notImpl();
	}

	@Override
	protected Representation myPut(Representation entity) throws Exception {
		return notImpl();
	}

}
