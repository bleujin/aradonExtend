package net.ion.radon.impl.let.monitor;

import java.io.File;
import java.io.IOException;

import net.ion.framework.util.Debug;

import com.sun.jna.platform.FileMonitor;

public class RadonMonitor {

	private FileMonitor monitor = FileMonitor.getInstance() ;

	private File baseDir ;
	private CentralListener centralListener ;
	
	public RadonMonitor() throws IOException{
		this("./", 30) ;
	}
	
	public RadonMonitor(String baseDir) throws IOException{
		this(baseDir, 30) ;
	}
	
	public RadonMonitor(String baseDir, int maxListener) throws IOException{
		this.baseDir = new File(baseDir) ;
		if (! this.baseDir.exists()) throw new IOException("not exist dir : " + baseDir) ;
		this.centralListener = new CentralListener(baseDir, maxListener) ;
		init() ;
	}
	
	public void addListener(ClientFileListener listener){
		centralListener.addListener(listener) ;
	}
	
	public void removeListener(ClientFileListener listener){
		centralListener.removeListener(listener) ;
	}	
	
	public void removeListener(Class clz){
		centralListener.removeListener(clz) ;
	}
	
	private void init() throws IOException{
		monitor.addFileListener(centralListener) ;	
		monitor.addWatch(baseDir, FileMonitor.FILE_ANY, true) ;
		
		Debug.debug("File Watcher Start", baseDir, "monitor") ;
	}
	
}
