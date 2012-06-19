package net.ion.radon.impl.let.monitor;

import java.io.File;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.MathUtil;

import com.sun.jna.platform.FileMonitor;
import com.sun.jna.platform.FileMonitor.FileEvent;
import com.sun.jna.platform.FileMonitor.FileListener;

public class TestMonitor extends TestCase{

	public void testM() throws Exception {
		FileMonitor monitor = FileMonitor.getInstance() ;
		FileListener listener = new MyFileListener() ;
		monitor.addFileListener(listener) ;
		
		int mask = FileMonitor.FILE_ANY ;
		monitor.addWatch(new File("c:/temp/"), mask, true) ;
		
		Thread.sleep(1000000) ;
	}
	
	
	public void testtMask() throws Exception {
		assertEquals(true, isDealWith(7, 4)) ;
		assertEquals(false, isDealWith(11, 4)) ;
		assertEquals(false, isDealWith(3, 4)) ;
		assertEquals(false, isDealWith(10, 4)) ;
		assertEquals(false, isDealWith(24, 4)) ;
		assertEquals(false, isDealWith(33, 4)) ;
		assertEquals(false, isDealWith(130, 4)) ;
		assertEquals(true, isDealWith(511, 4)) ;
		assertEquals(true, isDealWith(511, 1)) ;
		assertEquals(true, isDealWith(511, 2)) ;
		assertEquals(true, isDealWith(511, 8)) ;
		assertEquals(true, isDealWith(511, 16)) ;
		assertEquals(true, isDealWith(511, 32)) ;
	}
	
	
	private boolean isDealWith(int maskNum, int eventNum){
		return (maskNum >> MathUtil.log2(eventNum) & 1) == 1 ;
	}
	
	
	private double log2(double num){
		return Math.log(num) / Math.log(2) ;
	}
	
}

class MyFileListener implements FileListener {

	public void fileChanged(FileEvent event) {
		//Debug.debug(event.getSource(), event.getSource().getClass()) ;
		File file = event.getFile() ;
		
		if (event.getType() == FileMonitor.FILE_CREATED){
			Debug.debug("Created", event.getSource(), event.getFile(), event.getType()) ;
			//file.delete() ;
		} else if (event.getType() == FileMonitor.FILE_DELETED) {
			Debug.debug("Deleted", event.getSource(), event.getFile(), event.getType()) ;
			//file.delete() ;
		} else if (event.getType() == FileMonitor.FILE_MODIFIED){
			Debug.debug("Modify", event.getSource(), event.getFile(), event.getType()) ;
			//file.delete() ;
		}
		//Debug.debug(event, file) ;
	}
	
}