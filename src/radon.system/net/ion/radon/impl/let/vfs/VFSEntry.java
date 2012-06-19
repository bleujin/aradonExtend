package net.ion.radon.impl.let.vfs;

import java.io.File;
import java.net.MalformedURLException;

import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFS;

import org.apache.commons.vfs2.FileSystemException;

public class VFSEntry {

	private FileSystemEntry manger ; 
	public VFSEntry(String configPath) throws MalformedURLException, FileSystemException{
		this.manger = VFS.getManger(new File(configPath).toURL()) ;
	}
	
	public FileSystemEntry getFileSystemEntry(){
		return manger ;
	}

	public static VFSEntry test() throws MalformedURLException, FileSystemException {
		return new VFSEntry("resource/config/vfs_provider.xml");
	}
}

