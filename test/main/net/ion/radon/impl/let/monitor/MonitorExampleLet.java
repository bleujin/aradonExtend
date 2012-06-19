package net.ion.radon.impl.let.monitor;

import net.ion.framework.util.Debug;
import net.ion.radon.core.let.AbstractLet;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

import com.sun.jna.platform.FileMonitor.FileEvent;

public class MonitorExampleLet extends AbstractLet {

	
	private static String lastEventContent ="N/A";
	private static InterestEvent event = InterestEvent.create("./", 511, true) ;
	private static boolean ADDED = false ;
	
	private static ClientFileListener TEST = new ClientFileListener() {
		public void fileChanged(FileEvent fevent) {
			Debug.line(fevent) ;
			lastEventContent = fevent.toString() ;
		}
		
		public InterestEvent getMyInteresting() {
			return event;
		}
	};
	
	public MonitorExampleLet(){
	}
	
	@Override
	public Representation myDelete() throws Exception {
		return null;
	}

	@Override
	public Representation myGet() throws Exception {
		if (!ADDED){
			getContext().getAttributeObject("aradon.monitor.default", RadonMonitor.class).addListener(TEST) ;
			ADDED = true ;
		}
		return new StringRepresentation(lastEventContent);
	}

	@Override
	public Representation myPost(Representation entity) throws Exception {
		return new StringRepresentation(lastEventContent);
	}

	@Override
	public Representation myPut(Representation entity) throws Exception {
		return null;
	}



}
