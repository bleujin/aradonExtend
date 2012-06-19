package net.ion.im.bbs.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;
import net.ion.im.bbs.util.Tail;

public class TailTest extends TestCase {

	private String logFilePath = "c:/CheckForNewUpdates.java";
	private Tail tail;
	
	public void setUp() {
		try {
			tail = new Tail(logFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void testLength() {
		File file = new File(logFilePath);
		assertEquals(file.length(), tail.getLength());
	}

	public void testRead_WithNewLine() throws IOException {
		System.out.println(tail.read(3));
	}

	public void testRead_WithBR() throws IOException {
		System.out.println(tail.read(3, "<br/>"));
	}	
}
