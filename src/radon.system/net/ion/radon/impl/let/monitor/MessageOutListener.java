package net.ion.radon.impl.let.monitor;

import net.ion.framework.util.Debug;

import com.sun.jna.platform.FileMonitor.FileEvent;

public class MessageOutListener implements ClientFileListener{

	private InterestEvent myevent ;
	public MessageOutListener(String fileName, int mask, boolean recursive){
		this.myevent = InterestEvent.create(fileName, mask, recursive) ;
	}

	public InterestEvent getMyInteresting() {
		return myevent ; 
	}

	public void fileChanged(FileEvent fevent) {
		Debug.debug(fevent.getFile(), "event:" + fevent.getType()) ;
	}
	
}
