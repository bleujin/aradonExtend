package net.ion.radon.core.jmx;

import net.ion.framework.parse.gson.JsonElement;

public interface ThreadDumpMBean {

	JsonElement printDump() throws Exception ;
}
