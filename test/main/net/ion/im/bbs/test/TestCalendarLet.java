package net.ion.im.bbs.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.util.Debug;
import net.ion.im.bbs.util.IMCalendarUtils;
import net.ion.radon.core.Aradon;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.Method;

public class TestCalendarLet extends TestCase {

	private String config = "resource/config/plugin-im.xml";
	private String internalPrefixURL = "riap://component/im/cal";
	
	public void testCalendarUtil_month() throws ParseException {
		String testMonth1 = "201102";
		String expected1 = "$@2011-2-%";
		
		String testMonth2 = "201112";
		String expected2 = "$@2011-12-%";
		
		assertEquals(expected1, IMCalendarUtils.toCalendarMonthFormat(testMonth1));
		assertEquals(expected2, IMCalendarUtils.toCalendarMonthFormat(testMonth2));
	}
	
	public void testCalendarUtil_date() throws ParseException {
		String testMonth1 = "20110201";
		String expected1 = "$@2011-2-1-%";
		
		String testMonth2 = "20111231";
		String expected2 = "$@2011-12-31-%";
		
		assertEquals(expected1, IMCalendarUtils.toCalendarDateFormat(testMonth1));
		assertEquals(expected2, IMCalendarUtils.toCalendarDateFormat(testMonth2));		
	}
	
	public void testCalendarUtil_dection() throws ParseException {
		String testMonth1 = "201102";
		String expected1 = "$@2011-2-%";
		
		String testMonth2 = "20111231";
		String expected2 = "$@2011-12-31-%";
		
		assertEquals(expected1, IMCalendarUtils.getIMCalendarDateFormat(testMonth1));
		assertEquals(expected2, IMCalendarUtils.getIMCalendarDateFormat(testMonth2));		
	}
	
	public void testCalendar() throws Exception {
		String url = internalPrefixURL + "/list/resource";
		Request request = new Request(Method.GET, url);
		Response response = handle(config, request);
		Debug.line(response.getEntityAsText());
	}
	
	public void testCalendar_View() throws Exception {
		String url = internalPrefixURL + "/resource/4933";
		Request request = new Request(Method.GET, url);
		
		Response response = handle(config, request);
		Debug.line(response.getEntityAsText());
	}
	
	public void testCalendar_Edit() throws Exception {
		String url = internalPrefixURL + "/resource/4953";
		Request request = new Request(Method.GET, url);
		
		Response response = handle(config, request);
		JsonObject root = JsonObject.fromString(response.getEntityAsText()).asJsonObject("result");
		
		JsonObject content = root.asJsonArray("nodes").asJsonObject(0);
		
		String no = content.asString("no");
		String category = content.asString("category");
		String subject = content.asString("subject");
		String memo = content.asString("memo");
		
		Request editRequest = new Request(Method.PUT, url);
		Form form = new Form();
		form.add("userId", "airkjh");
		form.add("password", "kjh5660");
		form.add("no", no);
		form.add("category", category);
		form.add("subject", subject + "_edited");
		form.add("memo", memo + "_" + System.currentTimeMillis());
		
		editRequest.setEntity(form.getWebRepresentation()) ;
		Response editResponse = handle(config, editRequest);

		String entityText = editResponse.getEntityAsText();
		assertTrue(isSuccess(entityText));
	}
	
	private Response handle(String config, Request request) throws Exception {
		Aradon aradon = Aradon.create(config) ;
		aradon.start() ;
		return aradon.handle(request);
	}

	public void testCalendar_Delete() throws Exception {
		String url = internalPrefixURL + "/resource/4933";
		Request request = new Request(Method.DELETE, url);
		Form form = new Form();

		form.add("userId", "airkjh");
		form.add("password", "kjh5660");
		request.setEntity(form.getWebRepresentation());
		
		Response response = handle(config, request);
		String entityText = response.getEntityAsText();

		assertTrue(isSuccess(entityText));
	}
	
	public void testCalendar_Add() throws Exception {
		String url = internalPrefixURL + "/resource";
		Request request = new Request(Method.POST, url);
		
		Form form = new Form();
		form.add("userId", "airkjh");
		form.add("password", "kjh5660");
		form.add("subject", "Content which is created by Aradon IM plugin TestCase");
		form.add("memo", getTodayFormat() + "Content which is created by Aradon IM plugin TestCase");
		form.add("category", "7");
		
		request.setEntity(form.getWebRepresentation());
		Response response = handle(config, request);
		
		String entityText = response.getEntityAsText();
		assertTrue(isSuccess(entityText));
	}
	
	private String getTodayFormat() throws ParseException {
//		$@2005-9-27-9$@2005+9+27+23$@
		Date today = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddh");
		String formattedStr = dateFormat.format(today);
		Date formattedDate = dateFormat.parse(formattedStr);
		Calendar cal = Calendar.getInstance();
		cal.setTime(formattedDate);
		
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day = String.valueOf(cal.get(Calendar.DATE));
		String startHour = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
		String endHour = "24";
		
		return String.format("$@%s-%s-%s-%s$@%s+%s+%s+%s$@", year, month, day, startHour, year, month, day, endHour);
	}
	
	private JsonObject parseResult(String entityText) {
		JsonObject root = JsonObject.fromString(entityText);
		
		if(root.has("result")) {
			JsonObject result = root.asJsonObject("result");
			return result.asJsonArray("nodes").asJsonObject(0);
		}
		
		throw new IllegalArgumentException("response has no result");
	}
	
	private boolean isSuccess(String entityText) {
		Debug.line(entityText);
		JsonObject result = parseResult(entityText);
		
		return result.asBoolean("success");		
	}
	
	public void testRangeFormat() throws ParseException {
		String result = IMCalendarUtils.toRangeCalendarDateFormat("20101201", "20110131");
		Debug.line(result);
	}
}
