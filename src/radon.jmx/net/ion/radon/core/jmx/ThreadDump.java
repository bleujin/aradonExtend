package net.ion.radon.core.jmx;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonElement;
import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.parse.gson.JsonPrimitive;

public class ThreadDump implements ThreadDumpMBean {

	public JsonElement printDump() throws Exception {
		return threadInfo();
	}

	public JsonObject threadInfo() throws Exception {

		ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
		
		JsonObject all = new JsonObject() ;
		
		JsonArray deadlock = new JsonArray() ;
		long[] dtids = threadBean.findMonitorDeadlockedThreads() ;
		if (dtids != null && dtids.length > 0){
			for (long tid : dtids) {
				JsonObject dthread = threadInfo(threadBean, threadBean.getThreadInfo(tid)) ;
				deadlock.add(dthread) ;
			}
		}
		all.add("deadlocks", deadlock) ;
		
		JsonArray dump = new JsonArray();
		ThreadInfo[] tinfos = threadBean.getThreadInfo(threadBean.getAllThreadIds(), 15);
		for (ThreadInfo tinfo : tinfos) {
			JsonObject tinfoJson = threadInfo(threadBean, tinfo);
			dump.add(tinfoJson);
		}

		all.add("threads", dump) ;
		
		return all;

	}

	private JsonObject threadInfo(ThreadMXBean threadBean, ThreadInfo tinfo) {
		JsonObject tinfoJson = JsonParser.fromObject(tinfo).getAsJsonObject();
		tinfoJson.put("cpuTime", threadBean.getThreadCpuTime(tinfo.getThreadId())) ;
		tinfoJson.put("userTime", threadBean.getThreadUserTime(tinfo.getThreadId())) ;
		if (tinfo.getLockOwnerId() > -1) {
			tinfoJson.put("lockName", tinfo.getLockName());
			tinfoJson.put("lockOwnerName", tinfo.getLockOwnerName());
		}
		JsonArray starray = new JsonArray();
		for (StackTraceElement strace : tinfo.getStackTrace()) {
			String trString;
			if (strace.getLineNumber() < 0) {
				trString = "at " + strace.getClassName() + "." + strace.getMethodName();
			} else {
				trString = "at " + strace.getClassName() + "." + strace.getMethodName() + "(" + strace.getFileName() + ":" + strace.getLineNumber() + ")";
			}
			starray.add(new JsonPrimitive(trString));
		}
		tinfoJson.add("stackTrace", starray);
		return tinfoJson;
	}

}
