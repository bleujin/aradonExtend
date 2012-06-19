package net.ion.radon.impl.let.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import net.ion.framework.util.Debug;
import net.ion.radon.core.TreeContext;
import net.ion.radon.impl.let.monitor.ClientFileListener;
import net.ion.radon.impl.let.monitor.InterestEvent;
import net.ion.radon.impl.let.monitor.RadonMonitor;
import net.ion.radon.param.ConfigParser;

import org.apache.commons.io.IOUtils;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.sun.jna.platform.FileMonitor;
import com.sun.jna.platform.FileMonitor.FileEvent;

public class ConfigLoader implements ClientFileListener {

	private ProcedureGroup procGroup;

	private String filePath;

	private InterestEvent event;

	public ConfigLoader(String filePath, String className) {
		this.filePath = filePath;
		this.event = InterestEvent.create(filePath, FileMonitor.FILE_ANY);
		init();
	}

	private String monitorName;

	public void setMonitor(String monitorName) {
		this.monitorName = monitorName;
	}

	public void addMonitor(TreeContext context) {
		RadonMonitor mon = context.getAttributeObject(monitorName, RadonMonitor.class);
		if (mon != null)
			mon.addListener(this);
		Debug.line(filePath, " monitoring....");
	}

	private void init() {
		try {
			String str = IOUtils.toString(new FileInputStream(new File(filePath)), "UTF-8");
			this.procGroup = ConfigParser.parse(str, ProcedureGroup.class) ;

		} catch (FileNotFoundException e) {
			throw new IllegalStateException(e.getMessage(), e);
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public Procedures getProcedures(String id) {
		if (procGroup == null) {
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, id + " not found");
		}
		
		for(Procedures procCmd : procGroup.getProcedures()){
			if(procCmd.getId().equals(id)){
				return procCmd;
			}
		}
		
		return null;
	}

	private long lastTime = 0;

	public synchronized void fileChanged(FileEvent fevent) {
		final long currTime = new Date().getTime();
		if (currTime - lastTime < 1000)
			return;
		try {
			if (fevent.getType() == FileMonitor.FILE_MODIFIED) {
				Debug.debug("Modified :", filePath, fevent);

				init();
				lastTime = currTime;
			} else {
				Debug.debug(fevent + " is not my handle");
			}
		} catch (RuntimeException ignore) {
			ignore.printStackTrace();
		}
	}

	public InterestEvent getMyInteresting() {
		return event;
	}

}
