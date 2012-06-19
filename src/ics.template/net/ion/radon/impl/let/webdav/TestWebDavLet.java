package net.ion.radon.impl.let.webdav;

import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.Aradon;
import net.ion.radon.impl.let.vfs.VFSEntry;
import net.ion.radon.util.AradonTester;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.representation.StringRepresentation;

public class TestWebDavLet extends TestAradonExtend{

	
	public void runVFSAradon() throws Exception {
		Aradon aradon = AradonTester.create()
			.mergeSection("webdav")
			.addLet("/{scheme}", "scheme", WebDavLet.class)
			.putAttribute("system.vfs.entry", new VFSEntry("resource/config/vfs_provider.xml")) 
			.getAradon() ;
		aradon.startServer(9002) ;
		new InfinityThread().startNJoin() ;
	}
	
	public void testEmptyPropfind() throws Exception {
		Request request = new Request(Method.PROPFIND, "riap://component/webdav/afield") ;
		Response response = handle("resource/config/plugin-system-vfs.xml", request) ;
		Debug.debug(IOUtil.toString(response.getEntity().getStream())) ;
	}
	
	public void testPropfind() throws Exception {
		Request request = new Request(Method.PROPFIND, "http://localhost:9002/webdav/afield/Admin") ;
		
		// <?xml version="1.0"?><a:propfind xmlns:a="DAV:"><DAV:prop><a:getcontenttype/></DAV:prop><DAV:prop><a:getcontentlength/></DAV:prop></a:propfind>
		String body = "<?xml version=\"1.0\"?>\n"
			+ "<a:propfind xmlns:a=\"DAV:\">\n"  
			+ "<a:prop><a:getcontenttype/></a:prop>\n"  
			+ "<a:prop><a:getcontentlength/></a:prop>\n"  
			+ "</a:propfind>";
		StringRepresentation sr = new StringRepresentation(body, MediaType.TEXT_XML) ;
		
		request.setEntity(sr); 

//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        dbf.setNamespaceAware(true);
//		
//        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
//        Document reqdoc = documentBuilder.parse(new InputSource(request.getEntity().getStream()));
//        final Element root = reqdoc.getDocumentElement();
//		
//		
//		NodeList plist = root.getElementsByTagNameNS("DAV:", "prop") ;
//		Debug.line('$', plist.item(0)) ;
//		
//		int index = 0 ;
//		while(index < root.getChildNodes().getLength()){
//			final Node item = root.getChildNodes().item(index++);
//			Debug.debug(item) ;
//		}
//
//		
//		
//		Debug.debug(root.getChildNodes().getLength()) ;
//		
//		final Element propel = (Element) root.getElementsByTagNameNS("DAV:", "prop").item(0);
//		
//		Debug.line(propel, root, root.getLocalName(), root.getElementsByTagNameNS("DAV:", "prop").getLength(), ((NodeList)root.getElementsByTagName("prop")).item(0)) ;
//		Debug.line(request.getEntityAsText()) ;
		
		// Response response = handle("resource/config/plugin-system-vfs.xml", request) ;
		Response response = handle(request) ;
		Debug.debug(IOUtil.toString(response.getEntity().getStream())) ;
	}
	

	public void testVFSGet() throws Exception {
		Request request = new Request(Method.GET, "riap://component/vfs/afield/Admin") ;

		Response response = handle("resource/config/plugin-system-vfs.xml", request) ;
		assertEquals(200, response.getStatus().getCode()) ;
	}
}
