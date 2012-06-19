package net.ion.radon.impl.let.sample;

import java.util.ArrayList;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.RandomUtil;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.repository.NodeConstants;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class TestConnectionPool extends TestAradonExtend {

	public void testCall() throws Exception {
		final int range = 3000;
		final int thread_count = 40;
		final int request_per_thread = 400000 ;

		List<String> rowIds = new ArrayList<String>() ;
		Mongo mongo = new Mongo("localhost", 27017) ;
		DBCursor cursor = mongo.getDB("test").getCollection("article_sample").find().limit(range) ;
		
		while(cursor.hasNext()){
			final DBObject rowObj = cursor.next();
			rowIds.add(rowObj.get(NodeConstants.ID).toString()) ;
		}
		
		long start = System.currentTimeMillis() ;
		Thread[] ts = new Thread[thread_count] ; 
		for (int i = 0; i < thread_count; i++) {
			ts[i] = new ClientThread(rowIds, request_per_thread);
			ts[i].start();
		}
		
		for (int i = 0; i < thread_count; i++) {
			ts[i].join();
		}
		Debug.debug(System.currentTimeMillis() - start) ;

	}

}

class ClientThread extends Thread {

	private Client client = null;

	private List<String> store ;
	private int requestPerThread ;
	public ClientThread(List<String> store, int requestPerThread) {
		this.store = store ;
		this.requestPerThread = requestPerThread ;
	}

	public void run() {
		try {
			client = new Client(Protocol.HTTP);
			
			for (int i = 0; i < requestPerThread; i++) {
				String oid = store.get(RandomUtil.nextInt(store.size())) ;
				Request request = new Request(Method.GET, "http://localhost:9002/system/mongo/test/article_sample/" + oid + ".json");
				
				Response response = client.handle(request);
				String str = response.getEntityAsText();
				Thread.sleep(100) ;
				Debug.debug(str) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				client.stop();
			} catch (Exception ignore) {
			}
		}
	}

}
