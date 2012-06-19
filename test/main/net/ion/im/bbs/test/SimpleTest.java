package net.ion.im.bbs.test;

import net.ion.radon.TestAradonExtend;

import org.restlet.engine.util.Base64;

public class SimpleTest extends TestAradonExtend {

	public void setUp() throws Exception {
		super.setUp();
	}
	
	public void testBase64() {
		String password = new String(Base64.encode("kjh5660".getBytes(), false));
		System.out.println(password);
	}
	
}
