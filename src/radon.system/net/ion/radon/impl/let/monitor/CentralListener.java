package net.ion.radon.impl.let.monitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.MathUtil;
import net.ion.framework.util.PathMaker;
import net.ion.framework.util.StringUtil;

import com.sun.jna.platform.FileMonitor.FileEvent;
import com.sun.jna.platform.FileMonitor.FileListener;

public class CentralListener implements FileListener{

	private List<ClientFileListener> clients = new ArrayList<ClientFileListener>() ;
	private String baseDir ;
	private int maxUserListener ;
	
	public CentralListener(String baseDir, int maxUserListener) {
		this.baseDir = baseDir ;
		this.maxUserListener = maxUserListener ;
	}

	public synchronized void addListener(ClientFileListener listener){
		if (maxUserListener < clients.size()) throw new IllegalStateException("over listener : confirm listener scope");
		clients.add(listener) ;
	}

	public synchronized void removeListener(ClientFileListener listener) {
		clients.remove(listener) ;
	}

	public synchronized void removeListener(Class clz) {
		for (int i = clients.size()-1; i > -1 ; i--) { // desc order
			if (clz.isInstance(clients.get(i))){
				clients.remove(i) ;
			}
		}
	}

	public synchronized void fileChanged(FileEvent event) {
		for (ClientFileListener listenr : clients) {
			InterestEvent ievent = listenr.getMyInteresting() ;
			try {
				if (isDealWith(ievent, event)){
					listenr.fileChanged(event) ;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isDealWith(InterestEvent iv, FileEvent event) throws IOException {
		final String eventFileName = event.getFile().getCanonicalPath();
		final String interestFileName = new File(PathMaker.getFilePath(baseDir, iv.getFileName())).getCanonicalPath() ;

		if (! StringUtil.startsWith(eventFileName, interestFileName)) return false ;
		if (! iv.isRecursive() && ! (StringUtil.equals(eventFileName, interestFileName))) return false ;
		
		if ((iv.getMask() >> MathUtil.log2(event.getType()) & 1) == 0) return false ;
		
		return true ;
	}


}
