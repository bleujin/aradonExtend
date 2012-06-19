package net.ion.radon.impl.let.webdav;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.vfs.FileSystemEntry;
import net.ion.framework.vfs.VFS;
import net.ion.framework.vfs.VFile;

import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.ram.RamFileProvider;

public class TestRamDirectory extends TestCase{

	FileSystemEntry entry = null ; 
	public void setUp() throws Exception{
		entry = VFS.DEFAULT;
		entry.addProvider("ram", new RamFileProvider());
	}
	
	public void testRamDirectory() throws Exception {
		writeFile("ram://mytext.txt");

		VFile rfile = entry.resolveFile("ram://mytext.txt");
		// Debug.debug(IOUtil.toString(rfile.getInputStream()));
	}
	
	public void testCreateDiretory() throws Exception {
		writeFile("ram://abcd/mytext1.txt");
		writeFile("ram://abcd/mytext2.txt");

		VFile rfile = entry.resolveFile("ram://abcd/mytext1.txt");
		Debug.debug(IOUtil.toString(rfile.getInputStream()));
		
		VFile dfile = entry.resolveFile("ram://abcd");
		Debug.debug(dfile.getChildren()) ;
	}

	
	
	private void writeFile(String path) throws FileSystemException, IOException {
		VFile vfile = entry.resolveFile(path);
		Reader reader = new FileReader(new File("resource/bak/Dav1VFSResource.java")) ;
		Writer writer = new OutputStreamWriter(vfile.getOutputStream());
		
		IOUtil.copyNClose(reader, writer) ;
	}

}
