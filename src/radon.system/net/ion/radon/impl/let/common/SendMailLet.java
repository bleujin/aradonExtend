package net.ion.radon.impl.let.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Properties;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import net.ion.framework.mail.MailException;
import net.ion.radon.core.let.DefaultLet;

import org.apache.commons.fileupload.FileItem;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class SendMailLet extends DefaultLet {

	protected Representation myGet() {

		String body = "<html><body>" + "<form target='/system/sendmail' method='post' enctype='multipart/form-data'>" + "<input type='file' name='myfile'/>" + "<input type='submit' name='submit' value='submit' />" + "</form></body></html>";

		return new StringRepresentation(body, MediaType.TEXT_HTML, Language.ALL, CharacterSet.UTF_8);
	}

	protected Representation myPost(Representation entity) throws Exception {
		if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true)) {
			sendMailMultipart();
		} else {
			sendMailNormal();
		}

		return new StringRepresentation("message sent");
	}

	private void sendMailMultipart() throws AddressException, MessagingException, IOException {
		Session session = getMailSession();

		Message msg = makeMessage(session);

		Multipart multipart = new MimeMultipart(); // create the message part
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(getInnerRequest().getParameter("content")); // // msg.setText(content);
		multipart.addBodyPart(messageBodyPart);

		for (Entry<String, Object> entry : getInnerRequest().getFormParameter().entrySet()) { // Part two is attachment
			if (entry.getValue() instanceof FileItem) {
				FileItem fitem = (FileItem) entry.getValue();
				InputStream input = fitem.getInputStream();
				String fileName = URLDecoder.decode(fitem.getName(), "UTF-8");

				messageBodyPart = new MimeBodyPart();

				messageBodyPart.setDataHandler(new DataHandler(new ByteArrayDataSource(input, "application/octet-stream")));
				messageBodyPart.setFileName(MimeUtility.encodeText(fileName));
				multipart.addBodyPart(messageBodyPart);
			}
		}

		msg.setContent(multipart); // Put parts in message
		Transport.send(msg);
	}

	private Message makeMessage(Session session) throws MessagingException, AddressException {
		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(getInnerRequest().getParameter("from")));
		msg.addRecipient(Message.RecipientType.TO, new InternetAddress(getInnerRequest().getParameter("to")));
		msg.setSubject(getInnerRequest().getParameter("subject"));
		msg.setSentDate(new Date());
		return msg;
	}

	private void sendMailNormal() throws MailException, AddressException, MessagingException {
		Session session = getMailSession();

		Message msg = makeMessage(session);
		msg.setText(getInnerRequest().getParameter("content"));
		Transport.send(msg);
	}

	private Session getMailSession() {
		Properties prop = new Properties();
		Authenticator auth = new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication(){
				return new PasswordAuthentication(getContext().getAttributeObject("smtp.config.userid", "anony", String.class), getContext().getAttributeObject("smtp.config.password", "nopass", String.class)) ;
			}
		}; 
		prop.put("mail.smtp.host", getContext().getAttributeObject("smtp.config.host", "localhost", String.class));
		prop.put("mail.smtp.port", getContext().getAttributeObject("smtp.config.port", "25", String.class));
		Session session = Session.getDefaultInstance(prop, auth);
		return session;
	}

}
