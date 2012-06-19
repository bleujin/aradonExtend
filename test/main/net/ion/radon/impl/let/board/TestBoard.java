package net.ion.radon.impl.let.board;

import java.io.StringReader;
import java.net.UnknownHostException;

import net.ion.framework.parse.html.HTag;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.framework.util.InstanceCreationException;
import net.ion.radon.Options;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.AradonServer;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.impl.section.PathInfo;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

import org.apache.commons.configuration.ConfigurationException;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Status;

public class TestBoard extends TestAradonExtend{
	
	// private static String configPath = "src/radon.board/board-config.xml";
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		initAradon();
		aradon_init(aradon);
	}


	private void aradon_init(Aradon aradon) throws ConfigurationException, InstanceCreationException, UnknownHostException {
		SectionService board = aradon.attach("board", XMLConfig.BLANK);
		board.attach(PathInfo.create("list", "/list/{boardid}, /list/{boardid}/{listnum}, /list/{boardid}/{listnum}/{pageno}", ListLet.class));
		board.attach(PathInfo.create("article_add", "/add/{boardid}, /add/{boardid}/{no}" , AddLet.class));
		board.attach(PathInfo.create("article_view", "/view/{boardid}/{no}" , ViewLet.class));
		board.attach(PathInfo.create("article_edit", "/edit/{boardid}/{no}", EditLet.class));
		board.attach(PathInfo.create("article_delete", "/delete/{boardid}/{no}", DeleteLet.class));
		board.attach(PathInfo.create("sequence", "/sequence/{boardid}", SequenceLet.class)); 
		
		RepositoryCentral rc = RepositoryCentral.create("61.250.201.157", 27017) ;
		Session session =  rc.testLogin("board") ;
		session.dropWorkspace() ;
		session.getSequence("board", "bleujin").reset() ;
	}

	
	public void xtestStartAradon() throws Exception {
		AradonServer server = new AradonServer(new Options(new String[]{"-config:resource/config/readonly-config.xml"})) ;
		
		Aradon aradon = server.start() ;
		aradon_init(aradon) ;
		
		aradon.startServer(9002) ;
		
		new InfinityThread().startNJoin() ;
	}
	
	
	
	
	public void testAdd() throws Exception {
		Response response = callAdd();
		assertEquals(200, response.getStatus().getCode()) ;
		assertEquals(MediaType.APPLICATION_XML, response.getEntity().getMediaType()) ;
	
		Request request = new Request(Method.GET, "riap://component/board/add/bleujin/1") ;
		Response getRes = aradon.handle(request) ;
		
		assertEquals(200, getRes.getStatus().getCode()) ;
	}

	public void testAddMudltiple() throws Exception {
		for (int i = 0; i < 15; i++) {
			Request request = new Request(Method.POST, "riap://component/board/add/bleujin") ;
			Form form = new Form() ;
			form.add("subject", i + ". Bleujin Hi~") ;
			form.add("key", "greeting") ;
			form.add("content", "Hello H...") ;
			
			request.setEntity(form.getWebRepresentation());
			
			aradon.handle(request) ;
		}

		
	}
	

	private Response callAdd() {
		Request request = new Request(Method.POST, "riap://component/board/add/bleujin") ;
		Form form = new Form() ;
		form.add("subject", "Bleujin Hi~") ;
		form.add("key", "greeting") ;
		form.add("content", "Hello") ;
		
		request.setEntity(form.getWebRepresentation());

		return aradon.handle(request) ;
	}
	
	
	public void testView() throws Exception {
		callAdd();
		Request request = new Request(Method.GET, "riap://component/board/view/bleujin/1");
		Response response = aradon.handle(request);
		
		assertEquals(Status.SUCCESS_OK, response.getStatus());
		Debug.debug(response.getEntity()) ;
	}
	
	
	public void testEdit() throws Exception {
		callAdd();
		Request request = new Request(Method.POST, "riap://component/board/edit/bleujin/1");
		Form updateForm = new Form() ;
		updateForm.add("subject", "Heeya Hi~") ;
		updateForm.add("key", "greeting") ;
		updateForm.add("content", "Hello Heeya") ;
		request.setEntity(updateForm.getWebRepresentation());
		Response response = aradon.handle(request);
		assertEquals(Status.SUCCESS_OK, response.getStatus());

		Request confirm = new Request(Method.GET, "riap://component/board/view/bleujin/1") ;
		Response getRes = aradon.handle(confirm) ;
		assertEquals(200, getRes.getStatus().getCode()) ;

		Debug.debug(getRes.getEntityAsText()) ;
		
		HTag htag = HTag.createGeneral(new StringReader(getRes.getEntityAsText()), "result").getChild("nodes") ;
		assertEquals("<![CDATA[Heeya Hi~]]>", htag.findElementBy("property", "name", "subject").getTagText());
	}
	
	
	

	public void testDelete() throws Exception {
		callAdd();
		Request request = new Request(Method.POST, "riap://component/board/delete/bleujin/1");
		Response response = aradon.handle(request);
		
		Request confirm = new Request(Method.GET, "riap://component/board/view/bleujin/1") ;
		Response getRes = aradon.handle(confirm) ;
		assertEquals(404, getRes.getStatus().getCode()) ;
		
	}

	
	public void testList() throws Exception {
		callAdd() ;
		
		Request request = new Request(Method.GET, "riap://component/board/list/bleujin/10/1") ;
		Response response = aradon.handle(request) ;
		
		assertEquals(200, response.getStatus().getCode()) ;
		assertEquals(MediaType.APPLICATION_XML, response.getEntity().getMediaType()) ;
		String xmlString = response.getEntityAsText() ;
		
		HTag htag = HTag.createGeneral(new StringReader(xmlString), "result") ;
		assertEquals("1", htag.getValue("/response/property"));
		
		Debug.debug(xmlString) ;
		
	}

	
}


class Dummy extends Object{
	
}

//public void testSearialize() throws Exception {
//	DataIOBean dio = DataIOBean.create("bleujin", 1, "Hi", "bleujin", new HashMap()) ;
////	DataIOBean dio = DataIOBean.create("bleujin", 1) ;
//	List<Map<String, Object>> datas = ListUtil.create(MapUtil.create("abcd", (Object)dio)) ;
//	
//	final ByteArrayOutputStream bout = new ByteArrayOutputStream();
//	ObjectOutputStream oo = new ObjectOutputStream(bout) ;
//	
//	oo.writeObject(datas) ;
//	oo.close() ;
//	
//	ObjectInputStream oi = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
//	List<Map<String, Object>> readDatas = (List<Map<String, Object>>) oi.readObject() ;
//	
//	Debug.debug(readDatas.get(0)) ;
//}
