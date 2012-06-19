package net.ion.radon.impl.let.core;

import org.restlet.Application;
import org.restlet.resource.ServerResource;

public class AbstractResource extends ServerResource {

	private static Application currentApplication = null ; 

	@Override
	public void doInit() {
		super.doInit() ;
		if (currentApplication == null) {
			this.currentApplication = getApplication() ;
			doFirstInit() ;
		}

		if (currentApplication != getApplication()) {
			this.currentApplication = getApplication() ;
			doFirstInit() ;
		}
	}

	protected void doFirstInit() {
		;
	}
	 
}
