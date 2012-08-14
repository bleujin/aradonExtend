package net.ion.radon.impl.let.core;

import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.PageBean;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.PathConfiguration;
import net.ion.radon.core.config.SectionConfiguration;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.impl.section.PathInfo;

public class TestDataNodeLet extends TestAradonExtend{
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		initAradon() ;
		aradon.attach(SectionConfiguration.createBlank("core")).attach(PathConfiguration.create("dataio", "/dataio/", "", IMatchMode.STARTWITH, RamDataStore .class)) ;
	}

	public void testPath() throws Exception {
		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
		DataNode rootNode = session.getRootNode() ;
		
		assertEquals("/", rootNode.getPath());
		assertEquals("/", rootNode.getName());
		assertEquals("", rootNode.getParentPath());
		assertEquals("", rootNode.getParentName());
		
		DataNode child = rootNode.createChild("child");
		assertEquals("/child", child.getPath());
		assertEquals("child", child.getName());
		assertEquals("/", child.getParentPath());
		assertEquals("/", child.getParentName());
		
		DataNode gchild = child.createChild("gchild");
		assertEquals("/child/gchild", gchild.getPath());
		assertEquals("gchild", gchild.getName());
		assertEquals("/child", gchild.getParentPath());
		assertEquals("child", gchild.getParentName());
	}
	
	
	public void testCrud() throws Exception {
		
		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
		DataNode rootNode = session.getRootNode() ;
		
		DataNode freeboard = rootNode.createChild("freeboard");
		freeboard.save();
		
		List<DataNode> nodes = rootNode.getChildren();
		assertEquals(1, nodes.size()) ;
		
		DataNode fb = rootNode.getFirstChild() ;
		fb.addAttribute("greeting", "hi");
		
		Debug.debug(fb.getPath()) ;
		
		
		fb.save();
		
		fb = session.get("/freeboard") ;
		assertEquals("hi", fb.getAttribute().get("greeting"));
		
		fb.remove() ;
		assertEquals(0, rootNode.getChildren().size()) ;
	}
	
	public void testPage() throws Exception {
		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
		DataNode rootNode = session.getRootNode() ;

		for(int i=0; i < 10; i++){
			rootNode.createChild("page" + i).save();
		}
		
		List<DataNode> nodes = rootNode.getChildren(PageBean.create(5, 1));
		assertEquals(5, nodes.size());
		
		assertEquals(3, rootNode.getChildren(PageBean.create(7, 2)).size());
	}
	
	
	public void testOnlyChild() throws Exception {
		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
		DataNode rootNode = session.getRootNode() ;
		
		for(int i=0; i < 3; i++){
			rootNode.createChild("child" + i).save();
		}

		for(int i=0; i < 3; i++){
			for (int j = 0; j < 1; j++) {
				session.get("/child" + i).createChild("gchild" + j).save() ;
			}
		}
		
		assertEquals(3, rootNode.getChildren().size()) ;
		
		DataNode child = session.get("/child0");
		List<DataNode> gchildren =  child.getChildren();
		assertEquals(1, gchildren.size());
		assertEquals("/child0/gchild0", gchildren.get(0).getPath());
	}
	
	
	public void testEquals() throws Exception {
		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
		DataNode rootNode = session.getRootNode() ;
		
		DataNode d1 = rootNode.createChild("child");
		DataNode d2 = rootNode.createChild("child");
		
		assertTrue(d1.equals(d2)) ;
	}
	
	
	public void testSessionSave() throws Exception {
		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
		DataNode rootNode = session.getRootNode() ;
		
		for(int i=0; i < 3; i++){
			rootNode.createChild("child" + i);
		}

		assertEquals(3, session.getModifiedNode().size()) ;
	}
	
	
	public void testSession2() throws Exception {
		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
		DataNode rootNode = session.getRootNode() ;
		rootNode.createChild("child").save();
		
		DataNode child = session.get("/child") ;
		child.addAttribute("greeting", "hi") ;

		assertTrue(child == session.getModifiedNode().toArray()[0]) ;
		
		assertEquals(1, session.save());
		
		DataNode loadnode = session.get("/child");
		assertEquals("hi", loadnode.getAttribute().get("greeting"));
		
	}
	

//	public void testWorkspace() throws Exception {
//		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
//		
//		assertEquals(ClientSession.DEFAULT_WORKSPACE_NAME, session.getWorkspace()) ;
//		DataNode root1 = session.getRootNode() ;
//		root1.createChild("aa");
//		
//		session.setWorkspace("board") ;
//		assertEquals("board", session.getWorkspace()) ;
//		
//		DataNode root2 = session.getRootNode() ;
//		root2.createChild("bb");
//		
//		bb = session.get();
//		
//		session.setWorkspace("");
//		 DataNode root3 = session.getRootNode() ;
//		 assertEquals(root1, root3);
//	}
	
//	public void testOrder() throws Exception {
//		ClientSession session = ClientSession.create(aradon, "/core/dataio/") ;
//		DataNode rootNode = session.getRootNode() ;
//
//		session.setWorkspace("board") ;
//		
//		rootNode.createChild("child0").save();
//		rootNode.createChild("child1").save();
//
//		rootNode.getChildren()
//		
//		NodeCursor nc = rootNode.getCursor().sort("aaaa", -1).skip(10).limit(10).filter(PropertyQuery.create("key", "&lt 3")) ;
//
//	}

//
//	
//	public void testExpect() throws Exception {
//		initAradon() ;
//		
//		AradonClient client = AradonClientFactory.create(aradon) ;
//		IAradonRequest request = client.createRequest("riap://component/core/dataio/") ;
//		DataNode node = request.get(DataNode.class) ;
//		
//		
//		List<DataNode> children = node.getChildNode(PageBean.TEN) ;
//	}
}
