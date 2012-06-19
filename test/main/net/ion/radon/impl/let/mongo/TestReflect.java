package net.ion.radon.impl.let.mongo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.util.List;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.lang.ClassUtils;

public class TestReflect extends TestCase {

	public void testInvokeMethod() throws Exception {

		Object instance = ClassUtils.getClass("net.ion.radon.impl.let.mongo.Dept").newInstance();
		String methodName = "createWith";
		List params = ListUtil.toList(10, "dev", "seoul");
		
		Integer result = (Integer) MethodUtils.invokeMethod(instance, methodName, params.toArray());

		assertEquals(1, result.intValue());
	}
	

	public void testNodeSerialized() throws Exception {
		 Session session = RepositoryCentral.create("61.250.201.157", 27017).testLogin("default");
		 
		 Node node = session.newNode("dev");
		 node.put("key", "val") ;
		 
		 ByteArrayOutputStream bos = new ByteArrayOutputStream();
		 ObjectOutputStream oos = new ObjectOutputStream(bos);
		 oos.writeObject(node);
		 
		 ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		 ObjectInputStream ois = new ObjectInputStream(bis);
		 Node another = (Node) ois.readObject();
		
		 assertEquals("dev", another.getName());
		 assertEquals("val", another.get("key"));
	}
	
	public void testDate() throws Exception {
		Session session = RepositoryCentral.create("61.250.201.157", 27017).testLogin("default");
		session.dropWorkspace();
		Node node = session.newNode("dev");
		node.put("key", "val") ;
		node.put("date", new Date(System.currentTimeMillis()));
		session.commit();
		
		Node getNode = session.createQuery().aradonGroupId("key", "val").findOne();
		Object value = getNode.get("date");
		Debug.debug(value.getClass());
	}
}
