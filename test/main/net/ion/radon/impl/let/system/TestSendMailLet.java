package net.ion.radon.impl.let.system;

import java.io.File;
import java.nio.charset.Charset;

import net.ion.radon.TestAradonExtend;
import net.ion.radon.client.HttpMultipartEntity;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.config.PathConfiguration;
import net.ion.radon.core.config.SectionConfiguration;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.impl.let.common.SendMailLet;
import net.ion.radon.impl.section.PathInfo;

import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.representation.Representation;

public class TestSendMailLet extends TestAradonExtend{
	

	@Override
	public void setUp() throws Exception {
		super.setUp();
		initAradon() ;
		
		SectionService section = aradon.attach(SectionConfiguration.createBlank("test")) ;

		section.attach(PathConfiguration.create("sendmail", "/sendmail", SendMailLet.class)) ;
		TreeContext scontext = section.getServiceContext() ;
		scontext.putAttribute("smtp.config.host", "smtp.i-on.net") ;
		scontext.putAttribute("smtp.config.port", "25") ;
		scontext.putAttribute("smtp.config.userid", "bleujin@i-on.net") ;
		scontext.putAttribute("smtp.config.password", "******") ;
	}

	
	public void testSendMail() throws Exception {
		Request request = new Request(Method.POST, "riap://component/test/sendmail" );
		Form form = new Form() ;
		form.add("from", "bleujin@i-on.net") ;
		form.add("to", "bleujin@i-on.net") ;
		form.add("subject", "하이하이 Normal") ;
		form.add("content", "안녕하세요.") ;
		request.setEntity(form.getWebRepresentation()) ;
		Response response = aradon.handle(request);
		assertEquals(200, response.getStatus().getCode()) ;
	}

	
	public void testSendMailMultipart() throws Exception {
		Request request = new Request(Method.POST, "riap://component/test/sendmail" );
		FormBodyPart[] parts = { new FormBodyPart("from", new StringBody("bleujin@i-on.net")),
				new FormBodyPart("to", new StringBody("bleujin@i-on.net")),
				new FormBodyPart("subject", new StringBody("한글", Charset.forName("UTF-8"))),
				new FormBodyPart("content", new StringBody("안녕하세요.", Charset.forName("UTF-8"))),
				new FormBodyPart("attach1", new FileBody(new File("./resource/bak/dd.xml")))
		// , new FilePart("attach2", "한글.csv", new File("./imsi/한글.csv"), "text/plain", "UTF-8")};
		} ; 
		final HttpMultipartEntity mre = new HttpMultipartEntity(parts);
		Representation representation = mre.makeRepresentation() ;
		request.setEntity(representation) ;
		Response response = aradon.handle(request);
		assertEquals(200, response.getStatus().getCode()) ;
	}
	
}
