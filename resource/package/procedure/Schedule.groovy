package procedure

import net.ion.framework.util.DateUtil;
import net.ion.framework.util.NumberUtil;
import net.ion.framework.util.StringUtil;
import net.ion.framework.db.Rows;
import net.ion.radon.core.PageBean;
import net.ion.radon.repository.NodeColumns;
import net.ion.radon.repository.NodeCursor;
import net.ion.radon.repository.NodeScreen;
import net.ion.radon.repository.Session;
import net.ion.radon.repository.Node;
import net.ion.radon.repository.NodeImpl;
import net.ion.radon.repository.SessionQuery;
import static procedure.GroupConstants.*;
import static procedure.table.ScheduleTable.*;
import net.ion.framework.db.Rows;
import net.ion.framework.db.bean.handlers.ArrayListHandler;
import net.ion.framework.db.bean.handlers.StringArrayHandler;
import net.ion.framework.db.procedure.Queryable;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.NodeRows;
import net.ion.radon.repository.myapi.AradonQuery;
import net.ion.radon.repository.myapi.ICursor;
import net.ion.framework.util.ListUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.ion.framework.util.DateUtil;
import java.util.Calendar;
import static procedure.ProcedureHelper.*;

class Schedule extends IProcedure{
	
	private final String REF_CODE = "r_code";
	private final String REF_REPOSITORY = "r_repository";
	
	public Rows listBy(String scheType, int listNum, int pageNo, int screenCount){
		NodeScreen ns = session.createQuery().aradonGroup(SCHEDULE).eq(ScheType, scheType).descending(ScheType, RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt", Message, ScheId, ScheNm, ScheType, IsActive, OptionBit, TargetId, ItemCount, TargetValue, 
						"tochar(lastExecDate, 'yyyy-MM-dd HH:mm:ss') laslExecDate", "tochar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows indexListBy(String targetId){
		NodeCursor nc = session.createQuery().aradonGroup(SCHEDULE).eq(ScheType, "index").eq(TargetId, targetId).descending(RegDate).find();
		return fromCursor(nc, Message, ScheId, ScheNm, ScheType, TargetId, IsActive, OptionBit, TargetValue, 
						"tochar(lastExecDate, 'yyyy-MM-dd HH:mm:ss') laslExecDate", "tochar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}   
	
	public Rows allListBy(int listNum, int pageNo, int screenCount){
		NodeScreen ns = session.createQuery().aradonGroup(SCHEDULE).descending(ScheType, TargetId, RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt", Message, ScheId, ScheNm, ScheType, SCHEDULETYPE+".cdNm scheTypeNm", IsActive, OptionBit, TargetId, TargetValue,
			 			"tochar(lastExecDate, 'yyyy-MM-dd HH:mm:ss') laslExecDate", "tochar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows searchListBy(String scheType, String searchKey, int listNum, int pageNo, int screenCount){
		SessionQuery query = session.createQuery().aradonGroup(SCHEDULE);
		if(StringUtil.isNotEmpty(scheType)) query.eq(ScheType, scheType);
		if(StringUtil.isNotEmpty(searchKey)) query.eq(ScheNm, searchKey);
		
		NodeScreen ns = query.descending(ScheType, TargetId, RegDate).find().screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt", Message, ScheId, ScheNm, ScheType, SCHEDULETYPE+".cdNm scheTypeNm", IsActive, OptionBit, TargetId, TargetValue,
						 "tochar(lastExecDate, 'yyyy-MM-dd HH:mm:ss') laslExecDate", "tochar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows typeBy(){
		return fromCursor(session.createQuery().aradonGroup(SCHEDULETYPE).find(), "codeId", "cdNm codeNm");
	}
	
	public Rows retrieveBy(int scheId){
		Node node = session.createQuery().aradonGroupId(SCHEDULE,scheId).findOne();
		return fromNode(node, ScheId, ScheNm, TargetId, IsActive, BeginDay, EndDay, RepeatTypeCd, DayOfRepeat, MonToSunBit, 
						FrqTypeOfDaily, StartTimeOfOnce, StartTimeOfRepeat, EndTimeOfRepeat, MinuteOfRepeat, OptionBit, 
						LastExecDate, RegDate, RecursiveFlg, ItemCount + " keywordCnt", TargetValue, 
						"decode(scheType, 'index', repository.repoNm, '') repoNm",
						"decode(mod(floor(divide(monToSunBit, power(2,0))),2), 1, 'checked', '') monToSunBitMon",
						"decode(mod(floor(divide(monToSunBit, power(2,1))),2), 1, 'checked', '') monToSunBitTue",
						"decode(mod(floor(divide(monToSunBit, power(2,2))),2), 1, 'checked', '') monToSunBitWed",
						"decode(mod(floor(divide(monToSunBit, power(2,3))),2), 1, 'checked', '') monToSunBitThu",
						"decode(mod(floor(divide(monToSunBit, power(2,4))),2), 1, 'checked', '') monToSunBitFri",
						"decode(mod(floor(divide(monToSunBit, power(2,5))),2), 1, 'checked', '') monToSunBitSat",
						"decode(mod(floor(divide(monToSunBit, power(2,6))),2), 1, 'checked', '') monToSunBitSun",
						"decode(mod(floor(divide(optionBit, power(2,1))),2), 1, 'checked', '') optionBit_a",
						"decode(mod(floor(divide(optionBit, power(2,2))),2), 1, 'checked', '') optionBit_c",
						"decode(mod(floor(divide(optionBit, power(2,3))),2), 1, 'checked', '') optionBit_m",
						"decode(mod(floor(divide(optionBit, power(2,5))),2), 1, 'checked', '') optionBit_l");
	}
	
	public Rows indexRetrieveBy(int scheId){
		Node node = session.createQuery().aradonGroupId(SCHEDULE,scheId).findOne();
		return fromNode(node, ScheId, ScheNm, TargetId, IsActive, BeginDay, EndDay, RepeatTypeCd, DayOfRepeat, MonToSunBit,
						FrqTypeOfDaily, StartTimeOfOnce, StartTimeOfRepeat, EndTimeOfRepeat, MinuteOfRepeat, OptionBit,
						LastExecDate, RegDate, RecursiveFlg, ItemCount + " keywordCnt", TargetValue, TargetId + " repoId",
						"decode(scheType, 'index', repository.repoNm, '') repoNm",
						"decode(mod(floor(divide(monToSunBit, power(2,0))),2), 1, 'checked', '') monToSunBitMon",
						"decode(mod(floor(divide(monToSunBit, power(2,1))),2), 1, 'checked', '') monToSunBitTue",
						"decode(mod(floor(divide(monToSunBit, power(2,2))),2), 1, 'checked', '') monToSunBitWed",
						"decode(mod(floor(divide(monToSunBit, power(2,3))),2), 1, 'checked', '') monToSunBitThu",
						"decode(mod(floor(divide(monToSunBit, power(2,4))),2), 1, 'checked', '') monToSunBitFri",
						"decode(mod(floor(divide(monToSunBit, power(2,5))),2), 1, 'checked', '') monToSunBitSat",
						"decode(mod(floor(divide(monToSunBit, power(2,6))),2), 1, 'checked', '') monToSunBitSun",
						"decode(mod(floor(divide(optionBit, power(2,1))),2), 1, 'checked', '') optionBit_a",
						"decode(mod(floor(divide(optionBit, power(2,2))),2), 1, 'checked', '') optionBit_c",
						"decode(mod(floor(divide(optionBit, power(2,3))),2), 1, 'checked', '') optionBit_m",
						"decode(mod(floor(divide(optionBit, power(2,5))),2), 1, 'checked', '') optionBit_l");
	}
   
	public Rows useTargetBy(String targetId, String scheType){
		ICursor cursor = session.createQuery().aradonGroup(SCHEDULE).eq(TargetId, targetId).eq(ScheType, scheType).find();
		return fromCursor(cursor, "nvl('T', 'F') isUse");
	}

	public int createWith(String scheNm, String scheType, String targetId, String isActive, String beginDay,
		String endDay, String isNoEndDay, String repeatTypeCd, int dayOfRepeat, int monToSunBit,
		String frqTypeOfDaily, int startTimeOfOnce, int startTimeOfRepeat, int endTimeOfRepeat, int minuteOfRepeat,
		int optionBit, String recursiveFlg, int itemCount, String targetValue) throws Exception {

		def scheNo = ProcedureHelper.getIncrementNo(ScheId, session.createQuery().aradonGroup(SCHEDULE));
		Node node = session.newNode().setAradonId(SCHEDULE, scheNo).put(ScheId, scheNo).put(ScheNm, scheNm).put(ScheType, scheType);
		node.put(TargetId, targetId).put(IsActive, isActive).put(BeginDay, beginDay);
		node.put(EndDay, ProcedureHelper.decode( StringUtil.defaultString(isNoEndDay, "F"), "T", "99991231", endDay));
		node.put(RepeatTypeCd, repeatTypeCd);
		node.put(DayOfRepeat, ProcedureHelper.decode( repeatTypeCd, "Daily", dayOfRepeat));
		node.put(MonToSunBit, ProcedureHelper.decode(repeatTypeCd, "Weekly",  monToSunBit));
		node.put(FrqTypeOfDaily, frqTypeOfDaily);
		node.put(StartTimeOfOnce, ProcedureHelper.decode(frqTypeOfDaily, "T", startTimeOfOnce));
		node.put(StartTimeOfRepeat, ProcedureHelper.decode(frqTypeOfDaily, "F", startTimeOfRepeat));
		node.put(EndTimeOfRepeat, ProcedureHelper.decode(frqTypeOfDaily, "F", ProcedureHelper.nvl(endTimeOfRepeat, 2359)));
		node.put(MinuteOfRepeat, ProcedureHelper.decode(frqTypeOfDaily, "F", ProcedureHelper.nvl(minuteOfRepeat, 5)));
		node.put(OptionBit, optionBit);
		node.put(LastExecDate, new Date(System.currentTimeMillis()));
		node.put(RegDate, new Date(System.currentTimeMillis()));
		node.put(RecursiveFlg, recursiveFlg).put(ItemCount, itemCount).put(TargetValue, targetValue);
		node.put(Message, getMessage(frqTypeOfDaily, startTimeOfOnce, minuteOfRepeat, startTimeOfRepeat, endTimeOfRepeat,
									repeatTypeCd, dayOfRepeat, monToSunBit, beginDay, node.getString(EndDay)));
		putOptionValue(node, optionBit);
		
		node.addReference(REF_CODE, AradonQuery.newByGroupId(SCHEDULETYPE, scheType)); 
		if(scheType.equalsIgnoreCase("index"))
			node.addReference(REF_REPOSITORY, AradonQuery.newByGroupId(REPOSITORY, targetId));
		return commit();
	}
		
	private String getMessage(String frqTypeOfDaily, int startTimeOfOnce, int minuteOfRepeat, int startTimeOfRepeat, int endTimeOfRepeat,
							  String repeatTypeCd, int dayOfRepeat, int monToSunBit, String beginDay , String endDay){
		String msg="";
		if(frqTypeOfDaily.equals("T")){
			msg = " at " + new StringBuffer(String.valueOf(startTimeOfOnce)).insert(2, ":") ;
		}else {
			msg = " every " + decode( minuteOfRepeat%60, 0, minuteOfRepeat/60 + " hour ", minuteOfRepeat + "mins " ) +
		    	  " between " + new StringBuffer(StringUtil.leftPad( String.valueOf(startTimeOfRepeat), 4, "0")).insert(2,":") + 
				  " to "  + new StringBuffer(StringUtil.leftPad(String.valueOf(endTimeOfRepeat), 4, "0")).insert(2,":") ;
		}
		
		msg += decode(repeatTypeCd, "Daily", " every " + dayOfRepeat + " day(s) ", 
				"Weekly", " every " + decode( ( Math.floor( monToSunBit/Math.pow(2,0) )%2) , 1D, "Mon, ") +   //1.0
				decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Tue, ") +
				decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Wed, ") +
				decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Thr, ") +
				decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Fri, ") +
				decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Sat, ") +
				decode( ( Math.floor( monToSunBit/Math.pow(2,1) )%2) , 1D, "Sun, "));
		msg += "[" + beginDay + "-" + ProcedureHelper.decode(endDay, "99991231", "infinite", endDay) + "]";
		
		return msg;
	}
								
	private void putOptionValue(Node node, int optionBit){
		node.put(Option1, Math.floor(optionBit/Math.pow(2,1))%2);
		node.put(Option2, Math.floor(optionBit/Math.pow(2,2))%2);
		node.put(Option3, Math.floor(optionBit/Math.pow(2,3))%2);
		node.put(Option4, Math.floor(optionBit/Math.pow(2,4))%2);
		node.put(Option5, Math.floor(optionBit/Math.pow(2,5))%2);
	}
	
	public int updateWith(int scheId, String scheNm, String scheType, String targetId, String isActive, 
		                  String beginDay,  String endDay, String isNoEndDay, String repeatTypeCd, int dayOfRepeat, 
						  int monToSunBit, String frqTypeOfDaily, int startTimeOfOnce, int startTimeOfRepeat, int endTimeOfRepeat, 
						  int minuteOfRepeat, int optionBit, String recursiveFlg, int itemCount, String targetValue){
	
		Node node = session.createQuery().aradonGroupId(SCHEDULE, scheId).findOne();
		if(node == null) return 0;
		
		node.setReference(REF_CODE, AradonQuery.newByGroupId(SCHEDULETYPE, node.getString(ScheType)), AradonQuery.newByGroupId(SCHEDULETYPE, scheType));
		if(scheType.equalsIgnoreCase("index"))
			node.setReference(REF_REPOSITORY, AradonQuery.newByGroupId(REPOSITORY, node.getString(TargetId)), AradonQuery.newByGroupId(REPOSITORY, targetId));
			
		node.put(ScheNm, scheNm).put(ScheType, scheType);
		node.put(TargetId, targetId).put(IsActive, isActive).put(BeginDay, beginDay);
		node.put(EndDay, ProcedureHelper.decode( StringUtil.defaultString(isNoEndDay, "F"), "T", "99991231", endDay));
		node.put(RepeatTypeCd, repeatTypeCd);
		node.put(DayOfRepeat, ProcedureHelper.decode( repeatTypeCd, "Daily", dayOfRepeat));
		node.put(MonToSunBit, ProcedureHelper.decode(repeatTypeCd, "Weekly",  monToSunBit));
		node.put(FrqTypeOfDaily, frqTypeOfDaily);
		node.put(StartTimeOfOnce, ProcedureHelper.decode(frqTypeOfDaily, "T", startTimeOfOnce));
		node.put(StartTimeOfRepeat, ProcedureHelper.decode(frqTypeOfDaily, "F", startTimeOfRepeat));
		node.put(EndTimeOfRepeat, ProcedureHelper.decode(frqTypeOfDaily, "F", ProcedureHelper.nvl(endTimeOfRepeat, 2359)));
		node.put(MinuteOfRepeat, ProcedureHelper.decode(frqTypeOfDaily, "F", ProcedureHelper.nvl(minuteOfRepeat, 5)));
		node.put(OptionBit, optionBit);
		node.put(RecursiveFlg, recursiveFlg).put(ItemCount, itemCount).put(TargetValue, targetValue);
		node.put(Message, getMessage(frqTypeOfDaily, startTimeOfOnce, minuteOfRepeat, startTimeOfRepeat, endTimeOfRepeat,
			              repeatTypeCd, dayOfRepeat, monToSunBit, beginDay, node.getString(EndDay)));
		putOptionValue(node, optionBit);
		return commit();
	}
	
	public int updateWithExec(int scheId, String targetId){
		Node node = session.createQuery().aradonGroupId(SCHEDULE, scheId).eq(TargetId, targetId).findOne();
		if(node == null) return 0;
		
		node.put(LastExecDate, new Date(System.currentTimeMillis()));
		return commit();
	}
	
	public Rows infoBy(int scheId){
		Node node = session.createQuery().aradonGroupId(SCHEDULE, scheId).findOne();
		return fromNode(node, ScheId, ScheNm, ScheType, TargetId, IsActive, OptionBit,
			    "tochar(lastExecDate, 'yyyy-MM-dd HH:mm:ss') lastExeDate", "tochar(regDate, 'yyyy-MM-dd HH:mm:ss') regDate");
	}
	
	public Rows infoTaskBy(String rankType, String targetId){
		Node node = session.createQuery().aradonGroup(SCHEDULE).eq(ScheType, rankType).eq(TargetId, targetId).findOne();
		return fromNode(node, ScheId, ScheNm, TargetId, IsActive, BeginDay, EndDay, RepeatTypeCd, DayOfRepeat, MonToSunBit,
						FrqTypeOfDaily, StartTimeOfOnce, StartTimeOfRepeat, EndTimeOfRepeat, MinuteOfRepeat, OptionBit,
						LastExecDate, RegDate, RecursiveFlg, ItemCount + " keywordCnt", TargetValue, 
						"decode(scheType, 'ranking', tonumber(itemCount), 0) limiteCount",
						"decode(scheType, 'ranking', tonumber(targetValue), 0) interval",
						"decode(scheType, 'auto', tonumber(targetValue), 0) limiteCount",
						"decode(mod(floor(divide(monToSunBit, power(2,0))),2), 1, 'checked', '') monToSunBitMon",
						"decode(mod(floor(divide(monToSunBit, power(2,1))),2), 1, 'checked', '') monToSunBitTue",
						"decode(mod(floor(divide(monToSunBit, power(2,2))),2), 1, 'checked', '') monToSunBitWed",
						"decode(mod(floor(divide(monToSunBit, power(2,3))),2), 1, 'checked', '') monToSunBitThu",
						"decode(mod(floor(divide(monToSunBit, power(2,4))),2), 1, 'checked', '') monToSunBitFri",
						"decode(mod(floor(divide(monToSunBit, power(2,5))),2), 1, 'checked', '') monToSunBitSat",
						"decode(mod(floor(divide(monToSunBit, power(2,6))),2), 1, 'checked', '') monToSunBitSun",
						"decode(mod(floor(divide(optionBit, power(2,1))),2), 1, 'checked', '') optionBit_a",
						"decode(mod(floor(divide(optionBit, power(2,2))),2), 1, 'checked', '') optionBit_c",
						"decode(mod(floor(divide(optionBit, power(2,3))),2), 1, 'checked', '') optionBit_m",
						"decode(mod(floor(divide(optionBit, power(2,5))),2), 1, 'checked', '') optionBit_l");
	}
	
	public int removeWith(int scheId){
		int removeCount = session.createQuery().aradonGroupId(SCHEDULE, scheId).remove();
		removeCount += session.createQuery().aradonGroup(SCHEDULE_TRACE).in(TaskId, getTaskIds(scheId)).remove();
		removeCount += session.createQuery().aradonGroup(SCHEDULE_TASK).eq(ScheId, scheId).remove();
		return removeCount;
	}
	
	private Object[] getTaskIds(int scheId){
		Rows rows = fromCursor(session.createQuery().aradonGroup(SCHEDULE_TASK).eq(ScheId, scheId).find(), TaskId);
		rows.beforeFirst();
		ArrayList<Object> list =  (ArrayList<Object>) rows.toHandle(new ArrayListHandler());
		return list.toArray(new Object[0]);
	}
	
	public int removeTargetIDWith(String targetId){
		int removeCount = session.createQuery().aradonGroup(SCHEDULE_TRACE).in(TaskId, getTaskIdByTargetId(targetId)).remove();
		removeCount+= session.createQuery().aradonGroup(SCHEDULE_TASK).in(ScheId, getScheIds(targetId)).remove();
		removeCount+= session.createQuery().aradonGroup(SCHEDULE).eq(TargetId, targetId).eq(ScheType, "index").remove();		
	}
	
	private Object[] getScheIds(String targetId){
		Rows rows = fromCursor(session.createQuery().aradonGroup(SCHEDULE).eq(TargetId, targetId).eq(ScheType, "index").find(), ScheId);
		rows.beforeFirst();
		ArrayList<Object> result =  (ArrayList<Object>) rows.toHandle(new ArrayListHandler());
		return result.toArray(new Object[0]);
	}
	
	private Object[] getTaskIdByTargetId(String targetId){
		Rows rows = fromCursor(session.createQuery().aradonGroup(SCHEDULE_TASK).in(ScheId, getScheIds(targetId)).find(), TaskId);
		rows.beforeFirst();
		ArrayList<Object> result =  (ArrayList<Object>) rows.toHandle(new ArrayListHandler());
		return result.toArray(new Object[0]);
	}
	
	// FUNCTION  actionListBy
	public Rows actionListBy(){
		String currentDay = DateUtil.calendarToDayString(Calendar.getInstance());
		NodeCursor nc = session.createQuery().aradonGroup(SCHEDULE).eq(IsActive, "T").lt(BeginDay, currentDay).gt(EndDay, currentDay).find();
		
		List<Node> list = ListUtil.newList();
		while (nc.hasNext()) {
			Node node = nc.next();
			int checkWeek =  NumberUtil.toIntWithMark( decode( node.getString(RepeatTypeCd), "Daily",  1, "Weekly",  checkWeekByWeekly(node.getAsInt(MonToSunBit))) , 0);
			int checkDay =  NumberUtil.toIntWithMark(decode(decode(node.getString(RepeatTypeCd), "Daily", checkIncludeDailyRepeat(node.getString(BeginDay), node.getAsInt(DayOfRepeat)), 0) , 0, 1, 0), 0);
			int checkOnceTime = NumberUtil.toIntWithMark(decode(node.getString(FrqTypeOfDaily), "T",  decode(DateUtil.currentDateToString("HHmm"), node.getString(StartTimeOfOnce), 1, 0), 1), 0);
			int checkRepeatTime = NumberUtil.toIntWithMark(decode(node.getString(FrqTypeOfDaily), "F",  checkExecDateByCurrent((Date)node.get(LastExecDate), node.getAsInt(MinuteOfRepeat)), 1), 0);
			int checkTimeRange = NumberUtil.toIntWithMark(decode(node.getString(FrqTypeOfDaily), "F", checkBetweenRepeatDate(node.getAsInt(StartTimeOfRepeat), node.getAsInt(EndTimeOfRepeat)), 1), 0);

			if(checkWeek + checkDay + checkOnceTime + checkRepeatTime + checkTimeRange == 5){
				list.add(node);
			}
		}
		
		return fromList(list, ScheId, ScheNm, ScheType, TargetId, IsActive, LastExecDate, OptionBit, RegDate, RecursiveFlg, ItemCount, TargetValue);
	}
	
	private int checkWeekByWeekly(int monToSunBit) {
		int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		double result =  Math.floor( monToSunBit / Math.pow(2,  (dayOfWeek == 1) ? 6 : (dayOfWeek-2))) % 2;
		return result == 1D ? 1 : 0;
	}

	private int checkBetweenRepeatDate(int startTimeOfRepeat, int endTimeOfRepeat) {
		int currTime = Integer.parseInt(DateUtil.currentDateToString("HHmm"));
		return (startTimeOfRepeat < currTime && currTime < endTimeOfRepeat) ? 1:0;
	}

	private int checkExecDateByCurrent(Date lastExeDate, int minuteOfRepeat){
		Calendar lastDate = DateUtil.dateToCalendar(lastExeDate);
		lastDate.add(Calendar.MINUTE, minuteOfRepeat);
		return Calendar.getInstance().before(lastDate)? 1:0 ;
	}
	
	private int  checkIncludeDailyRepeat(String beginDay , int dayOfRepeat){
		if(dayOfRepeat == 0) return -1;
		Calendar begin =  DateUtil.stringToCalendar( beginDay+"-000000");
		Calendar curr = Calendar.getInstance();
		int count = 0;
		while(!begin.after(curr)){
			count++;
			begin.add(Calendar.DATE, 1);
		}
		return count % dayOfRepeat;
	}
	
	//Schedule Task =============================
	public Rows taskListBy(int scheId, String targetId, int listNum, int pageNo, int screenCount){
		NodeCursor nc = session.createQuery().aradonGroup(SCHEDULE_TASK).eq(ScheId, scheId).in(TargetId, getTargetIds(targetId)).descending(TargetId, ExeDate).find();
		NodeScreen ns = nc.screen(PageBean.create(listNum, pageNo, screenCount));
		return fromScreen(ns, "cnt", TaskId, ScheId, TargetId, IsSuccess, "tochar(exeDate, 'yyyy-MM-dd HH:mm:ss') exeDate");
	}
	
	private String[] getTargetIds(String targetId){
		if(StringUtil.isEmpty(targetId)) return new String[1] {targetId};
		
		Rows rows = fromCursor(session.createQuery().aradonGroup(SCHEDULE_TASK).find(), TargetId);
		rows.beforeFirst();
		return (String[]) new StringArrayHandler(TargetId).handle(rows);
	}
	
	public int taskCreateWith(int taskId, int scheId, String targetId, String isSuccess, String exeDate){
		Node node = session.newNode().setAradonId(SCHEDULE_TASK, taskId).put(ScheId, scheId).put(TaskId, taskId);
		node.put(TargetId, targetId).put(IsSuccess, isSuccess).put(ExeDate, DateUtil.stringToDate(exeDate, "yyyyMMdd-HHmmss"));
		return commit();
	}

	public int taskTargetDelLogWith(String targetId, String range){
		Date date = decode(range.toLowerCase(), "year", getPreviousDate(Calendar.YEAR), "month", getPreviousDate(Calendar.MONTH), "week", getPreviousDate(Calendar.WEEK_OF_MONTH), "day", getPreviousDate(Calendar.DATE));
		return session.createQuery().aradonGroup(SCHEDULE_TASK).eq(TargetId, targetId).gt(ExeDate, date).remove();
	}
	
	public int taskDelLogWith(int scheId, String range){
		Date date = decode(range.toLowerCase(), "year", getPreviousDate(Calendar.YEAR), "month", getPreviousDate(Calendar.MONTH), "week", getPreviousDate(Calendar.WEEK_OF_MONTH), "day", getPreviousDate(Calendar.DATE));
		return session.createQuery().aradonGroup(SCHEDULE_TASK).eq(ScheId, scheId).gt(ExeDate, date).remove();
	}
		
	// ScheduleTrace =============================
	public traceCreateWith(int taskId, String startDate, String endDate, String content){
		Node node = session.newNode().setAradonId(SCHEDULE_TRACE, taskId).put(TaskId, taskId);
		node.put(StartDate, DateUtil.stringToDate(startDate, "yyyyMMdd-HHmmss"));
		node.put(EndDate, DateUtil.stringToDate(endDate, "yyyyMMdd-HHmmss"));
		node.put(Content, content);
		return commit();
	}
	
	public Rows traceRetrieveBy(int taskId){
		Node node = session.createQuery().aradonGroupId(SCHEDULE_TRACE, taskId).findOne();
		return fromNode(node, TaskId, "tochar(startDate, 'yyyy-MM-dd HH:mm:ss') startDate", "tochar(endDate, 'yyyy-MM-dd HH:mm:ss') endDate", Content);
	}
}
