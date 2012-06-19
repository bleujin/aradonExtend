package net.ion.im.bbs.test;

import java.io.ObjectInputStream;

import net.ion.framework.db.DBController;
import net.ion.framework.db.IDBController;
import net.ion.framework.db.Rows;
import net.ion.framework.db.manager.DBManager;
import net.ion.framework.db.mysql.MySQLDBManager;
import net.ion.framework.rest.StdObject;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.radon.TestAradonExtend;
import net.ion.radon.core.RadonAttributeKey;
import net.ion.radon.core.TreeContext;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Protocol;

public class TestIMBBSLet extends TestAradonExtend {

	private String configURL = "resource/config/plugin-im.xml";

	public void testRows() throws Exception {

		DBManager dbm = new MySQLDBManager("jdbc:mysql://61.250.201.157:3306/imzeroboard?useUnicode=true&amp;characterEncoding=UTF-8&amp;characterSetResults=UTF-8", "root", "randd");
		IDBController dc = new DBController(dbm);
		try {
			dc.initSelf();

			Rows rows = dc.getRows("select * from zetyx_board_free order by no desc limit 10") ;
			while(rows.next()) {
				Debug.debug( new String(rows.getString("memo").getBytes("ISO-8859-1")) ) ;
			}
		} finally {

			dc.destroySelf();
		}
	}
	
	public void testAuth() throws Exception {
		Request request = new Request(Method.GET, "riap://component/im/bbs/free/list");
		Response response = handle(configURL, request);

		Debug.debug(response.getEntityAsText());
		TreeContext rq = (TreeContext) request.getAttributes().get(RadonAttributeKey.REQUEST_CONTEXT);

		Debug.debug(rq);		
	}

	public void testGet() throws Exception {
		Request request = new Request(Method.GET, "riap://component/im/bbs/free/list");
		Response response = handle(configURL, request);

		Debug.debug(response.getEntityAsText());
		TreeContext rq = (TreeContext) request.getAttributes().get(RadonAttributeKey.REQUEST_CONTEXT);

		Debug.debug(rq);
	}

	public void testHttpGet() throws Exception {
		Client client = new Client(Protocol.HTTP);
		Request request = new Request(Method.GET, "http://localhost:9002/im/bbs/free/list/10/1?aradon.result.format=html");
		Response response = client.handle(request);

		Form form = (Form) response.getAttributes().get(RadonAttributeKey.ATTRIBUTE_HEADERS);
		Debug.debug(form.getValuesMap());

		Debug.debug(IOUtil.toString(response.getEntity().getStream(), "UTF-8"));
		TreeContext rq = (TreeContext) request.getAttributes().get(RadonAttributeKey.REQUEST_CONTEXT);

		Debug.debug(rq);
	}

	public void testObjectRepresentation() throws Exception {
		Request request = new Request(Method.GET, "riap://component/im/hello?abcd=abcd&aradon.result.format=object");
		Response response = handle(configURL, request);

		Debug.debug(response.getEntityAsText());

		StdObject sto = (StdObject) (new ObjectInputStream(response.getEntity().getStream()).readObject());

		Debug.debug(sto.getRequest());
	}
	
	public void testArticleList() throws Exception {
		Request request = new Request(Method.GET, "riap://component/im/bbs/free/list?auth=719ff54d45d7951ceb1e1ff796dae45f&aradon.result.format=json");
		Response response = handle(configURL, request);
		
		Debug.debug(response.getEntityAsText());
	}
}
