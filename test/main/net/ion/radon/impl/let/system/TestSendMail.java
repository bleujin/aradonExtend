package net.ion.radon.impl.let.system;

import java.net.URLDecoder;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;


public class TestSendMail extends TestCase{

	public void testSendMail() throws Exception {
		Properties prop = new Properties() ;
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication("bleujin@i-on.net", "redfpark") ;
			}
		}; 
		prop.put("mail.smtp.host", "smtp.i-on.net");
		Session session = Session.getDefaultInstance(prop, auth) ;
		String msgBody = "ÇÏÀÌ¿ä" ;
		
		Message msg = new MimeMessage(session) ;
		msg.setFrom(new InternetAddress("bleujin@i-on.net")) ;
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress("bleujin@i-on.net")) ;
		msg.setSubject("¾È³çÇÏ¼¼¿ä") ;
		msg.setText(msgBody) ;
		Transport.send(msg) ;
		
	}

	public void testDecode() throws Exception {
		String s = "%ED%95%9C%EA%B8%80%ED%8A%A4%EB%A6%BD.jpg" ;
		Debug.debug(URLDecoder.decode(s, "UTF-8")) ;
		Debug.debug(URLDecoder.decode("ÇÑ±ÛÆ«¸³", "UTF-8")) ;
		Debug.debug(URLDecoder.decode("ÚÜ", "UTF-8")) ;
	}
}
