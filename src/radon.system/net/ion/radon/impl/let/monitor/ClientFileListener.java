package net.ion.radon.impl.let.monitor;

import com.sun.jna.platform.FileMonitor.FileListener;

public interface ClientFileListener extends FileListener{
	public InterestEvent getMyInteresting() ;
}
