package net.ion.radon;

import java.io.IOException;

import junit.framework.TestCase;
import net.ion.framework.configuration.ConfigurationException;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.config.XMLConfig;

import org.restlet.Request;
import org.restlet.Response;

public class TestAradonExtend extends TestCase {

	protected Aradon aradon;

	public void setUp() throws Exception {
		aradon = Aradon.create();

		Debug.setPrintLevel(Debug.Level.Debug);
	}

	protected void initAradon() throws ConfigurationException,
			InstanceCreationException, IOException {
		aradon.start();
	}

	protected Response handle(Request request) {
		return aradon.handle(request);
	}

	public Response handle(String configPath, Request request)
			throws ConfigurationException, InstanceCreationException,
			IOException, org.apache.commons.configuration.ConfigurationException {
		aradon = Aradon.create(configPath);
		aradon.start();
		return aradon.handle(request);
	}
}