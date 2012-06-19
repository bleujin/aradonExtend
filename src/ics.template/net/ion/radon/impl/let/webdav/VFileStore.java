package net.ion.radon.impl.let.webdav;

import java.util.List;

import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFile;

import org.apache.commons.vfs2.FileSystemException;
import org.restlet.security.User;

public class VFileStore {
	
	private FileSystemEntry entry ;
	private VFileStore(FileSystemEntry entry) {
		this.entry = entry ;
	}

	public final static VFileStore create(FileSystemEntry entry){
		return new VFileStore(entry) ;
	}

	public VFile resolveFile(ITransaction transaction, String scheme, String path) throws FileSystemException {
		return entry.resolveFile(scheme + "://" + path);
	}
	
	public ITransaction begin(User user) {
		return ITransaction.EMPTY;
	}

	public List<VFile> getChildren(ITransaction transaction, VFile vfile) throws FileSystemException {
		return vfile.getChildren();
	}

	public void rollback(ITransaction transaction) {
	}

	public void commit(ITransaction transaction) {
	}
	
	public FileSystemEntry getFileSystem() {
		return entry ;
	}

}
