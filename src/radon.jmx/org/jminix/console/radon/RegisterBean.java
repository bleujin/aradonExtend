package org.jminix.console.radon;

import java.util.Properties;

import net.ion.radon.core.IService;
import net.ion.radon.core.context.OnEventObject;

import org.apache.velocity.app.VelocityEngine;

public class RegisterBean implements OnEventObject {

	public void onEvent(AradonEvent event, IService service) {

		if (event == AradonEvent.START || event == AradonEvent.RELOAD) {
			service.getServiceContext().putAttribute(MyConstants.VELOCITY_ENGINE_CONTEX_KEY, createVelocityEngine());
			service.getServiceContext().putAttribute("serverProvider", new AradonServerConnectionProvider(service.getAradon()));
		}

	}

	private static VelocityEngine createVelocityEngine() {
		VelocityEngine ve = new VelocityEngine();

		Properties p = new Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		p.setProperty("runtime.log.logsystem.log4j.logger", "common.jmx.velocity");
		ve.init(p);
		return ve;
	}
}
