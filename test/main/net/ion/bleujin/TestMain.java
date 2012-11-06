package net.ion.bleujin;

import junit.framework.TestCase;
import net.ion.framework.util.InfinityThread;
import net.ion.nradon.Radon;
import net.ion.nradon.RadonServer;
import net.ion.radon.Options;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.AradonServer;

public class TestMain extends TestCase{

	public void testRadon() throws Exception {
		AradonServer server = new AradonServer(new Options(new String[]{"-action:start", "-config:resource/config/aradon-config.xml", "-port:8787"})) ;
		
		Aradon radon = server.start() ;
		new InfinityThread().startNJoin() ;
	}
}
