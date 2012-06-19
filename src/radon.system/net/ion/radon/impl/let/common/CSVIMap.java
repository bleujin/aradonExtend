package net.ion.radon.impl.let.common;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.util.PathMaker;
import net.ion.radon.impl.util.CsvReader;
import net.ion.radon.impl.util.CsvWriter;

public class CSVIMap implements IMap{

	private File file = null ;
	private Map<String, String> memory = new HashMap<String, String>() ;

	private static CSVIMap SELF = null ;
	private CSVIMap(String basePath, String fileName) throws IOException {
		this.file = new File(PathMaker.getFilePath(basePath, fileName))  ;
	}
	
	public final synchronized static CSVIMap getInstance(String basePath, String fileName) throws IOException{
		if (SELF == null){
			SELF = new CSVIMap(basePath, fileName) ;
			SELF.init() ;
		}
		return SELF ;
	}
	
	private synchronized void init() throws IOException {
		CsvReader reader = new CsvReader(new FileReader(file)) ;
		String[] thisLine ;
		while((thisLine = reader.readLine()) != null){
			memory.put(thisLine[0], thisLine[1]) ;
		}
	}

	public synchronized void put(String key, String path) throws IOException{
		CsvWriter writer = new CsvWriter(new FileWriter(file, true)) ;
		writer.writeLine(new String[] {key, path}) ;
		writer.close() ;
		memory.put(key, path) ;
	}
	
	public String getValue(String key){
		return memory.get(key) ;
	}
	
	public boolean contains(String key){
		return memory.containsKey(key) ;
	}
	
	public synchronized boolean remove(String key) throws IOException{
		String value = memory.remove(key) ;
		rewriteFile() ;
		
		
		return value != null ;
	}

	private void rewriteFile() throws IOException {
		CsvWriter writer = new CsvWriter(new FileWriter(file, false)) ;
		for (Entry<String, String> entry : memory.entrySet()) {
			writer.writeLine(new String[]{entry.getKey(), entry.getValue()}) ;
		}
		writer.close() ;
	}

}
