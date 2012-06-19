package net.ion.radon.impl.let.monitor;

import java.io.File;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.PathMaker;

import com.sun.jna.platform.FileMonitor.FileEvent;

public class TestFileMonitor extends TestCase{

	
	public void testDefault() throws Exception {
		RadonMonitor mon = new RadonMonitor("c:/temp", 5) ;
		
		ClientFileListener cf = new ClientFileListener() {
			
			public void fileChanged(FileEvent fileevent) {
				Debug.debug(fileevent.getType() , fileevent.getFile().getName()) ;
			}
			
			public InterestEvent getMyInteresting() {
				return InterestEvent.create("report.txt", 511);
			}
		};
		
		mon.addListener(cf) ;
		
		
		Thread.sleep(1000000) ;
	}
	
	public void testFileName() throws Exception {
		File file = new File(".") ;
		File another = new File(PathMaker.getFilePath(file.getCanonicalPath(), "./")) ;
		Debug.debug(file.getCanonicalPath(), file.getAbsolutePath()) ;
	}
}
