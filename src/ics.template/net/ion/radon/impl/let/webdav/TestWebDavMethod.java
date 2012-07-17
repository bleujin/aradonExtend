package net.ion.radon.impl.let.webdav;

import java.io.File;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.vfs.VFile;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.impl.let.vfs.VFSEntry;
import net.ion.radon.util.AradonTester;

import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class TestWebDavMethod extends TestCase {

	public void testProfind() throws Exception {
		Aradon aradon = AradonTester.create().mergeSection("")
			.addLet("/", "webdav", IMatchMode.STARTWITH, WebDavLet.class)
			.putAttribute(VFSEntry.class.getCanonicalName(), VFSEntry.test())
			.getAradon();

		String text = "" 
			+ "<?xml version=\"1.0\" ?>\n"
			+ "<D:propfind xmlns:D=\"DAV:\">\n" 
			+ "        <D:allprop/>\n" 
			+ "</D:propfind>\n" ;

		StringRepresentation re = new StringRepresentation(text);

		final IAradonRequest request = AradonClientFactory.create(aradon).createRequest("/AbstractFileSystem.java");
		request.addHeader("Depth", "1");
		
		Representation r = request.setEntity(re).get();
		Debug.line(r);

		aradon.startServer(9002);
		new InfinityThread().startNJoin();
	}
	
	public void testRename() throws Exception {
		File source = new File("C:/temp/webdav/abcd/ccc.txt") ;
		File target = new File("C:/temp/webdav/abcd/ddd.txt") ;
		
		Debug.line(source.renameTo(target)) ;
	}
	
	public void testFileName() throws Exception {
		VFSEntry ve = VFSEntry.test() ;
		
		VFile vf = ve.getFileSystemEntry().resolveFile("template://abcd/ccc.txt") ;
		VFile vf2 = ve.getFileSystemEntry().resolveFile("template://abcd/ddd.txt") ;
		
		Debug.line(vf.getName(), vf2.getName()) ;
		
		vf.getFileObject().moveTo(vf2.getFileObject()) ;
	}
}
