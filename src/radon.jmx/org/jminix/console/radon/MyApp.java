package org.jminix.console.radon;

import java.util.Properties;

import net.ion.radon.core.Aradon;
import net.ion.radon.core.SectionService;
import net.ion.radon.core.EnumClass.IMatchMode;
import net.ion.radon.core.config.PathConfiguration;
import net.ion.radon.core.config.SectionConfiguration;
import net.ion.radon.core.config.XMLConfig;
import net.ion.radon.impl.section.PathInfo;

import org.apache.velocity.app.VelocityEngine;
import org.jminix.console.resource.AttributeResource;
import org.jminix.console.resource.AttributesResource;
import org.jminix.console.resource.DirLet;
import org.jminix.console.resource.DomainResource;
import org.jminix.console.resource.DomainsResource;
import org.jminix.console.resource.IndexHtmlLet;
import org.jminix.console.resource.MBeanResource;
import org.jminix.console.resource.MBeansResource;
import org.jminix.console.resource.OperationResource;
import org.jminix.console.resource.OperationsResource;
import org.jminix.console.resource.ServerResource;
import org.jminix.console.resource.ServersResource;

public class MyApp {

	public static void main(String[] args) throws Exception {

		Aradon aradon = Aradon.create() ;

		aradon.getServiceContext().putAttribute(MyConstants.VELOCITY_ENGINE_CONTEX_KEY, createVelocityEngine());

		SectionService ss = aradon.attach(SectionConfiguration.createBlank("plugin.jminix"));
		ss.attach(PathConfiguration.create("index.html", "/", IndexHtmlLet.class));
		ss.attach(PathConfiguration.create("js", "/js/", "js", IMatchMode.STARTWITH, DirLet.class));
		ss.attach(PathConfiguration.create("servers", "/servers/", ServersResource.class));
		ss.attach(PathConfiguration.create("server", "/servers/{server}/", ServerResource.class));
		ss.attach(PathConfiguration.create("domains", "/servers/{server}/domains/", DomainsResource.class));
		ss.attach(PathConfiguration.create("domain", "/servers/{server}/domains/{domain}/", DomainResource.class));
		ss.attach(PathConfiguration.create("mbeans", "/servers/{server}/domains/{domain}/mbeans/", MBeansResource.class));
		ss.attach(PathConfiguration.create("mbean", "/servers/{server}/domains/{domain}/mbeans/{mbean}/", MBeanResource.class));
		ss.attach(PathConfiguration.create("attributes", "/servers/{server}/domains/{domain}/mbeans/{mbean}/attributes/", AttributesResource.class));
		ss.attach(PathConfiguration.create("attribute", "/servers/{server}/domains/{domain}/mbeans/{mbean}/attributes/{attribute}/", AttributeResource.class));
		ss.attach(PathConfiguration.create("attribute_query", "/servers/{server}/domains/{domain}/mbeans/{mbean}/attributes/{attribute}/{query}", AttributeResource.class));
		ss.attach(PathConfiguration.create("operations", "/servers/{server}/domains/{domain}/mbeans/{mbean}/operations/", OperationsResource.class));
		ss.attach(PathConfiguration.create("operation", "/servers/{server}/domains/{domain}/mbeans/{mbean}/operations/{operation}", OperationResource.class));

//		ss.attach(PathInfo.create("index", "/index.html", IndexHtmlLet.class));
//		ss.getServiceContext().putAttribute("base.dir", "src/radon.jmx/jminix/") ;
		aradon.getServiceContext().putAttribute("serverProvider", new AradonServerConnectionProvider(aradon));

		aradon.startServer(8182);
	}

	private static VelocityEngine createVelocityEngine() {
		VelocityEngine ve = new VelocityEngine();

		Properties p = new Properties();
		p.setProperty("resource.loader", "class");
		p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
		p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		p.setProperty("runtime.log.logsystem.log4j.logger", "common.jmx.velocity");
		ve.init(p);
		return ve;
	}
}
