package net.ion.radon.impl.let.monitor;

import com.sun.jna.platform.FileMonitor;

public class InterestEvent {
	
	private String fileName ;
	private int mask ;
	private boolean recursive ;
	private InterestEvent(String fileName, int mask, boolean recursive) {
		this.fileName = fileName ;
		this.mask = mask ;
		this.recursive = recursive ;
	}

	public final static InterestEvent create(String fileName){
		return new InterestEvent(fileName, FileMonitor.FILE_ANY, false) ;
	}

	public final static InterestEvent create(String fileName, int mask){
		return new InterestEvent(fileName, mask, false) ;
	}
	
	final static InterestEvent create(String fileName, boolean recursive){
		return new InterestEvent(fileName, FileMonitor.FILE_ANY, recursive) ;
	}

	final static InterestEvent create(String fileName, int mask, boolean recursive){
		return new InterestEvent(fileName, mask, recursive) ;
	}
	
	public String getFileName(){
		return fileName ;
	} 
	
	public int getMask() {
		return mask ;
	}
	
	public boolean isRecursive(){
		return recursive ;
	}
	
}


