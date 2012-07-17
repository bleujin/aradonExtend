package net.sf.webdav;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.impl.let.vfs.VFSEntry;
import net.ion.radon.util.AradonTester;

import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class TestWebDavMethod extends TestCase {

	public void testProfind() throws Exception {
		Aradon aradon = AradonTester.create().mergeSection("")
			.addLet("/", "webdav", IMatchMode.STARTWITH, WebDavLet.class)
			.putAttribute(VFSEntry.class.getCanonicalName(), VFSEntry.test())
			.getAradon();

		String text = "" 
			+ "<?xml version=\"1.0\" ?>\n"
			+ "<D:propfind xmlns:D=\"DAV:\">\n" 
			+ "        <D:allprop/>\n" 
			+ "</D:propfind>\n" ;

		StringRepresentation re = new StringRepresentation(text);

		final IAradonRequest request = AradonClientFactory.create(aradon).createRequest("/");
		request.addHeader("Depth", "1");
		Representation r = request.setEntity(re).handle(Method.PROPFIND).getEntity();
		Debug.line(r);

//		aradon.startServer(9002);
//		new InfinityThread().startNJoin();
	}
	
	public void testMediaType() throws Exception {
		Debug.line(MediaType.APPLICATION_WWW_FORM.equals(MediaType.APPLICATION_OCTET_STREAM)) ;
	}

}
