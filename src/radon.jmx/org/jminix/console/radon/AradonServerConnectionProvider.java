package org.jminix.console.radon;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import net.ion.framework.util.ListUtil;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.IService;
import net.ion.radon.core.jmx.ContextMBean;
import net.ion.radon.core.jmx.ServiceMBean;
import net.ion.radon.core.jmx.ThreadDump;

import org.apache.commons.logging.LogFactory;
import org.jminix.server.AbstractListServerConnectionProvider;

public class AradonServerConnectionProvider extends AbstractListServerConnectionProvider {

	private Aradon aradon;
	private List<MBeanServerConnection> result ;
	public AradonServerConnectionProvider(Aradon aradon) {
		this.aradon = aradon;
	}

	public List<MBeanServerConnection> getConnections() {
		
		if (result != null){
			return result ;
		}
		
		result = ListUtil.newList() ;

		try {
			ManagementFactory.getPlatformMBeanServer(); // Make sure the platform server is instanciated
		} catch (SecurityException ex) {
			LogFactory.getLog(this.getClass()).warn("Not allowed to obtain the Java platform MBeanServer", ex);
		}

		try {
			result.addAll(MBeanServerFactory.findMBeanServer(null));

		} catch (Exception e) {
			LogFactory.getLog(this.getClass()).warn("Could not register the Java platform MBeanServer", e);
		}

		addAraonJXM(result);
		return result;
	}

	private void addAraonJXM(List<MBeanServerConnection> result) {
		MBeanServer server = MBeanServerFactory.createMBeanServer();
		try {
			for (IService sec : aradon.getChildren()) {
				server.registerMBean(new ServiceMBean(sec), new ObjectName("net.ion.radon.section:Name=" + sec.getName() + ",serverType=net.ion.radon.core.SectionService"));

				server.registerMBean(new ContextMBean(sec, sec.getServiceContext()), new ObjectName("net.ion.radon.section:Name=" + sec.getName() + "Context,serverType=net.ion.radon.core.TreeContext"));
				for (IService ps : sec.getChildren()) {
					server.registerMBean(new ServiceMBean(ps), new ObjectName("net.ion.radon.section.path-" + sec.getName() + ":Name=" + ps.getName() + ",serverType=net.ion.radon.core.PathService"));
					server.registerMBean(new ContextMBean(ps, ps.getServiceContext()), new ObjectName("net.ion.radon.section.path-" + ps.getName() + ":Name=" + ps.getName() + "Context,serverType=net.ion.radon.core.TreeContext"));
				}
			}
			server.registerMBean(new ServiceMBean(aradon), new ObjectName("net.ion.radon.core:Name=" + aradon.getConfig().getId() + ",serverType=net.ion.radon.core.Aradon"));
			server.registerMBean(new ContextMBean(aradon, aradon.getServiceContext()), new ObjectName("net.ion.radon.core:Name=AradonContext,serverType=net.ion.radon.core.Aradon"));
			
			server.registerMBean(new ThreadDump(), new ObjectName("net.ion.aradon.jmx.dump:name=ThreadDump"));
			result.add(server) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
