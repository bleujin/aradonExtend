package net.ion.radon;

import java.io.IOException;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.config.XMLConfig;

import org.apache.commons.configuration.ConfigurationException;
import org.restlet.Request;
import org.restlet.Response;

public class TestAradonExtend extends TestCase{
	
	protected Aradon aradon ;
	public void setUp() throws Exception {
		aradon = new Aradon() ;
		
		Debug.setPrintLevel(Debug.Level.Debug) ;
	}
	
	protected void initAradon() throws ConfigurationException, InstanceCreationException, IOException{
		aradon.init(XMLConfig.BLANK) ;
		aradon.start() ;
	}

	protected Response handle(Request request) {
		return aradon.handle(request);
	}

	public Response handle(String configPath, Request request) throws ConfigurationException, InstanceCreationException, IOException {
		aradon = new Aradon() ;
		aradon.init(configPath) ;
		aradon.start() ;
		return aradon.handle(request);
	}
}
