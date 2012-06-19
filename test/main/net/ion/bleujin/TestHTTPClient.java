package net.ion.bleujin;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.client.AradonClient;
import net.ion.radon.client.AradonClientFactory;
import net.ion.radon.client.IAradonRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

public class TestHTTPClient extends TestCase {
//
//	public void testCall() throws Exception {
//		DefaultHttpClient cl = new DefaultHttpClient();
//
//	}

	public void testURLConnection() throws Exception {
		CacheConfig cacheConfig = new CacheConfig();
		cacheConfig.setMaxCacheEntries(1000);
		cacheConfig.setMaxObjectSizeBytes(8192);

		HttpClient cachingClient = new CachingHttpClient(new DefaultHttpClient(), cacheConfig);

		HttpContext localContext = new BasicHttpContext();

		//for (int i : ListUtil.rangeNum(10)) {
			HttpGet httpget = new HttpGet("http://icon.daumcdn.net/w/c/10/12/37691415937798319.png");
			HttpResponse response = cachingClient.execute(httpget, localContext);
			HttpEntity entity = response.getEntity();
			EntityUtils.consume(entity);
			CacheResponseStatus responseStatus = (CacheResponseStatus) localContext.getAttribute(CachingHttpClient.CACHE_RESPONSE_STATUS);
			switch (responseStatus) {
			case CACHE_HIT:
				System.out.println("A response was generated from the cache with no requests " + "sent upstream");
				break;
			case CACHE_MODULE_RESPONSE:
				System.out.println("The response was generated directly by the caching module");
				break;
			case CACHE_MISS:
				System.out.println("The response came from an upstream server");
				break;
			case VALIDATED:
				System.out.println("The response was generated from the cache after validating " + "the entry with the origin server");
				break;
			}
		//}

	}
	
	public void testCallAmazon() throws Exception {
		AradonClient ac = AradonClientFactory.create("http://ec2-54-248-1-243.ap-northeast-1.compute.amazonaws.com:9000");
		IAradonRequest req = ac.createRequest("/") ;
		
		Debug.line(req.handle(Method.GET).getEntityAsText()) ;
	

	}
	
	public void testCallAmazon80() throws Exception {
		AradonClient ac = AradonClientFactory.create("http://ec2-54-248-1-243.ap-northeast-1.compute.amazonaws.com:80");
		IAradonRequest req = ac.createRequest("/") ;
		
		Debug.line(req.handle(Method.GET).getEntityAsText()) ;
	}

	public void testCallDaum() throws Exception {

		AradonClient ac = AradonClientFactory.create("http://icon.daumcdn.net");

		Client cli = new Client(Protocol.HTTP);

		for (int i = 0 ; i < 10 ; i++) {
			Request req = new Request(Method.GET, "http://icon.daumcdn.net/w/c/10/12/37691415937798319.png");

			req.setCacheDirectives(ListUtil.toList(CacheDirective.publicInfo(), CacheDirective.maxAge(24 * 60 * 60 * 30)));

			// Debug.debug(CacheDirective.publicInfo(), CacheDirective.maxAge(24 * 60 * 60 * 30));

			// Form headerForm = new Form() ;
			// headerForm.add("If-Modified-Since", "Fri, 10 Dec 2010 01:59:10 GMT") ;
			// headerForm.add("Cache-Control", "max-age=0") ;
			//
			// req.getAttributes().put(RadonAttributeKey.ATTRIBUTE_HEADERS, headerForm);

			Response res = cli.handle(req);
			Debug.debug(res.getStatus(), res.getEntity().getSize());

		}

	}
}
