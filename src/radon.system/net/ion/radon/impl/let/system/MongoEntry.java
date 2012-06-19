package net.ion.radon.impl.let.system;

import java.io.File;
import java.io.IOException;

import net.ion.framework.db.mongo.MongoRunner;
import net.ion.framework.util.Debug;
import net.ion.radon.repository.RepositoryCentral;
import net.ion.radon.repository.Session;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.restlet.Request;


public class MongoEntry {

	private String configPath ;
	private String cmd ;
	
	public static String MONGO_ID = "aradon.repository" ;
	private static String REPOSITORY_DB_NAME = "test" ;
	private RepositoryCentral rc ;
	public MongoEntry(String configPath, String cmd){
		this.configPath = configPath ;
		this.cmd = cmd ;
		init() ;
	}
	
	
	private void init(){
		try {
			startMongo() ;
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void startMongo() throws ConfigurationException, IOException{
		
		PropertiesConfiguration pc = new PropertiesConfiguration(new File(configPath)) ;
		
		File path = new File(pc.getString("dbpath")) ;
		if (! path.exists()) path.mkdirs() ;
		
		new Thread(){
			public void run(){
				new MongoRunner().run(cmd, configPath) ;
			}
		}.start() ;
		Debug.debug("Mongo Start") ;
		this.rc = RepositoryCentral.create("localhost", 27017) ;
	}
	
	public Session login(Request request) {
		return rc.login(REPOSITORY_DB_NAME, "default");
	}

}

