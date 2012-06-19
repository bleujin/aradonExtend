package net.ion.radon.impl.let.mongo.icss.test;

import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;
import net.ion.framework.util.DateUtil;
import net.ion.framework.util.Debug;
import net.ion.framework.util.HashFunction;
import net.ion.framework.util.StringUtil;

import org.apache.commons.lang.ObjectUtils;

public class TestSimple extends TestCase{
	
	
	public void testHashCode() throws Exception {
		
		Debug.line(HashFunction.hashGeneral("아이온"+"그래서")); 
		Debug.line(HashFunction.hashGeneral("그래서"+"아이온"));
		Debug.line(HashFunction.hashGeneral("아이온"+"그래서")); 
		
	}
	
	public void testStringContains() throws Exception {
		String value1 = "aa.bb.cc";
		String value2 = "aa.bb cc";
		
		Debug.debug(StringUtil.containsOnly(value1, "."));
		Debug.debug(StringUtil.containsOnly(value2, "."));
		Debug.debug(StringUtil.countMatches(value1, "."));
		Debug.debug(StringUtil.countMatches(value2, "."));
		
	}
	
	public void testDate() throws Exception {
		String s = "20110517-101010";
		Date date = DateUtil.stringToDate(s, "yyyyMMdd-HHmmss");
		Debug.debug( DateUtil.dateToString(date, "yyyy-MM-dd HH:mm:ss"));
		
	}
	
	public void testDecode() throws Exception {
		Debug.debug(decode("F", "T", "9999", "2011", "2011", "F", "T"));
	}
	
	private Object decode(Object... args){
		Object pivot = args[0];
		int i=1;
		for (; i < (args.length - 1); i += 2) {
			if (ObjectUtils.equals(pivot, args[i]))
			return  args[i + 1];
		}
		return i == (args.length - 1) ? args[i] : "";
	}
	
	public void testCalendar() throws Exception {
		Calendar c = Calendar.getInstance();
		Debug.debug(DateUtil.calendarToString(c));
		c.add(Calendar.YEAR, -1);
		Debug.debug(DateUtil.calendarToString(c));
		c = Calendar.getInstance(); 
		c.add(Calendar.MONTH, -1);
		Debug.debug(DateUtil.calendarToString(c));
		c = Calendar.getInstance(); 
		c.add(Calendar.WEEK_OF_MONTH, -1);
		Debug.debug(DateUtil.calendarToString(c));
		c = Calendar.getInstance(); 
		c.add(Calendar.DATE, -1);
		Debug.debug(DateUtil.calendarToString(c));
		Debug.debug(DateUtil.calendarToDate(c));
	}
	
	
	private Date getDate(int opt){
		Calendar c = Calendar.getInstance();
		c.add(opt, -1);
		return DateUtil.calendarToDate(c);
	}
	
	/*
	  Select 'Repeat ' || Decode(frqTypeOfDaily, 'T', ' at '||substr(lpad(startTimeOfOnce, 4, '0'), 0, 2)|| ':'|| substr(lpad(startTimeOfOnce, 4, '0'), -2) , 
	                                             'F', ' every  ' || Decode(Mod(minuteOfRepeat, 60), 0, minuteOfRepeat/60 || 'hour ', minuteOfRepeat || 'mins ') 
	          || ' between '  || substr(lpad(startTimeOfRepeat, 4, '0'), 0, 2) || ':' || substr(lpad(startTimeOfRepeat, 4, '0'), -2) 
	          || ' to '  || substr(lpad(endTimeOfRepeat, 4, '0'), 0, 2)|| ':' || substr(lpad(endTimeOfRepeat, 4, '0'), -2) ) 
	          
	          
	          || Decode(repeatTypeCd, 'Daily', ' every '|| DAYOFREPEAT ||' day(s) ', 'Weekly', '  every ' 
	          || Decode(Mod(Floor(MonToSunBit / Power(2, 0)), 2), 1, 'Mon, ') 
	          || Decode(Mod(Floor(MonToSunBit / Power(2, 1)), 2), 1, 'Tue, ') 
	          || Decode(Mod(Floor(MonToSunBit / Power(2, 2)), 2), 1, 'Wed, ') 
	          || Decode(Mod(Floor(MonToSunBit / Power(2, 3)), 2), 1, 'Thr, ') 
	          || Decode(Mod(Floor(MonToSunBit / Power(2, 4)), 2), 1, 'Fri, ') 
	          || Decode(Mod(Floor(MonToSunBit / Power(2, 5)), 2), 1, 'Sat, ') 
	          || Decode(Mod(Floor(MonToSunBit / Power(2, 6)), 2), 1, 'Sun, ') ) 
	          || '[' || BEGINDay || '-' || Decode(endDay, '99991231', 'infinite', endDay) || ']' message,
Mod(Floor(optionBit / Power(2, 1)), 2) option1,
Mod(Floor(optionBit / Power(2, 2)), 2) option2,
Mod(Floor(optionBit / Power(2, 3)), 2) option3,
Mod(Floor(optionBit / Power(2, 4)), 2) option4,
Mod(Floor(optionBit / Power(2, 5)), 2) option5,
	 */
	
	
	public void testMessage() throws Exception {
		Debug.debug(getMessage("T", 1030, 0, 0, 0, "Daily", 1, 0, "20081112", "99991231"));
		Debug.debug(getMessage("F", 0, 1, 0, 2300, "Daily", 1, 0, "20081113", "99991231"));
		Debug.debug(getMessage("f", 0, 5, 0, 2300, "Weekly", 0, 127, "20100707", "99991231"));
		
		
	}
	
	private String getMessage(String frqTypeOfDaily, int startTimeOfOnce, int minuteOfRepeat, int startTimeOfRepeat, int endTimeOfRepeat,
			String repeatTypeCd, int dayOfRepeat, int monToSunBit, String beginDay , String endDay){
//		String msg="";
//		if(frqTypeOfDaily.equals("T")){
//			msg = " at " + new StringBuffer(String.valueOf(startTimeOfOnce)).insert(2, ":") ;
//		}else {
//			msg = " every " + ProcedureHelper.decode( minuteOfRepeat%60, 0, minuteOfRepeat/60 + " hour ", minuteOfRepeat + "mins " ) +
//			      " between " + new StringBuffer(StringUtil.leftPad( String.valueOf(startTimeOfRepeat), 4, "0")).insert(2,":") + 
//			      " to "  + new StringBuffer(StringUtil.leftPad(String.valueOf(endTimeOfRepeat), 4, "0")).insert(2,":") ;
//		}
//		
//		msg += ProcedureHelper.decode(repeatTypeCd, "Daily", " every " + dayOfRepeat + " day(s) ", 
//				"Weekly", " every " + ProcedureHelper.decode( ( Math.floor( monToSunBit/Math.pow(2,0) )%2) , 1D, "Mon, ") +   //1.0
//				ProcedureHelper.decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Tue, ") +
//				ProcedureHelper.decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Wed, ") +
//				ProcedureHelper.decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Thr, ") +
//				ProcedureHelper.decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Fri, ") +
//				ProcedureHelper.decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Sat, ") +
//				ProcedureHelper.decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Sun, "));
//		msg += "[" + beginDay + "-" + ProcedureHelper.decode(endDay, "99991231", "infinite", endDay) + "]";
//		
//		
//		return msg;
		return "";
	}
	

}

class Simple{
	String key;
	String value;
	
	Simple(String key, String value){
		this.key = key;
		this.value = value;
	}
	
	public String getWord(){
		return this.key + this.value;
	}
	@Override
	public boolean equals(Object obj) {
		return getWord().equals( ((Simple)obj).getWord());
	}
	
	@Override
	public int hashCode() {
		return getWord().hashCode();
	}
}
