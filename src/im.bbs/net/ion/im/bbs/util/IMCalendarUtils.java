package net.ion.im.bbs.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class IMCalendarUtils {

	private static final String DATE_FORMAT_PREFIX = "$@";

	public static String toCalendarMonthFormat(String yyyymm) throws ParseException {
		Calendar cal = getCurrentCalendar(yyyymm + "01");
		
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		
		return String.format("%s%s-%s-", DATE_FORMAT_PREFIX, year, month) + "%";
	}

	public static String toCalendarDateFormat(String yyyymmdd) throws ParseException {
		Calendar cal = getCurrentCalendar(yyyymmdd);
		
		String year = String.valueOf(cal.get(Calendar.YEAR));
		String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
		String day = String.valueOf(cal.get(Calendar.DATE));
		
		return String.format("%s%s-%s-%s-", DATE_FORMAT_PREFIX, year, month, day) + "%";
	}
	
	public static String toRangeCalendarDateFormat(String start, String end) throws ParseException {
		Calendar startCal = getCurrentCalendar(start);
		Calendar endCal = getCurrentCalendar(end);
		
		String startYear = String.valueOf(startCal.get(Calendar.YEAR));
		String startMonth = String.valueOf(startCal.get(Calendar.MONTH) + 1);
		String startDay = String.valueOf(startCal.get(Calendar.DATE));
		
		String endYear = String.valueOf(endCal.get(Calendar.YEAR));
		String endMonth = String.valueOf(endCal.get(Calendar.MONTH) + 1);
		String endDay = String.valueOf(endCal.get(Calendar.DATE));
		
		return String.format("$@%s-%s-%s-%s$@%s+%s+%s+%s$@", startYear, startMonth, startDay, "0", endYear, endMonth, endDay, "24");
	}

	private static Calendar getCurrentCalendar(String yyyymmdd) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date input = dateFormat.parse(yyyymmdd);
		Calendar cal = Calendar.getInstance();
		cal.setTime(input);
		
		return cal;
	}

	public static String getCurrentMonth() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar cal = Calendar.getInstance();
		
		return dateFormat.format(cal.getTime());
	}

	public static String getIMCalendarDateFormat(String searchDate) throws ParseException {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		
		try {
			dateFormat.parse(searchDate);
			return toCalendarDateFormat(searchDate);
		} catch (ParseException e) {
			SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
			monthFormat.parse(searchDate);
			
			return toCalendarMonthFormat(searchDate);
		}
	}
}
