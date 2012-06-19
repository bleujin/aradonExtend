package net.ion.im.bbs.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

public class Tail {
	
	private String logFilePath;
	
	private File logFile;
	private LineNumberReader reader;
	
	private char[] buffer;
	private int bufferSize;
	
	public Tail(String logFilePath) throws FileNotFoundException {
		this(logFilePath, 1024);
	}
	
	public Tail(String logFilePath, int bufferSize) throws FileNotFoundException {
		this.logFilePath = logFilePath;
		this.bufferSize = bufferSize;
		
		init();
	}
	
	private void init() throws FileNotFoundException {
		this.logFile = new File(logFilePath);
		this.reader = new LineNumberReader(new FileReader(logFilePath));
		
		new File(logFilePath).length();
	}
	
	public String read(long bytes) throws IOException {
		return read(bytes, "\n");
	}
	
	public String read(long bytes, String delimiter) throws IOException {
		long minSize = bytes * 1024;
		long fileLength = getLength();
		long bytesToSkip = 0;
		
		StringBuilder builder = new StringBuilder();
		String line;
		
		if(fileLength > minSize) {
			bytesToSkip = fileLength - minSize;
		}
		
		reader.skip(bytesToSkip);
		while((line = reader.readLine()) != null) {
			builder.append(String.format("%s%s", line, delimiter));
		}
		
		reader.close();
		
		return builder.toString();
	}
	
	public long getLength() {
		return logFile.length();
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
}
