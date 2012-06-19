package net.ion.heeya.lang;

import junit.framework.TestCase;
import net.ion.framework.util.Debug;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.CharSetUtils;
import org.apache.commons.lang.RandomStringUtils;

public class TestArrayUtils extends TestCase{
	
	public void testArrayUtils() throws Exception {
		String [] test = {"aaa", "bbb", "ccc"};
		Debug.debug(ArrayUtils.contains(test, "aaa"));
		Debug.debug(ArrayUtils.indexOf(test, "bbb"));
	}
	
	public void testBoolean() throws Exception {
		boolean ok = false;
		Boolean [] array = {true, false, false, false};
		Debug.debug(BooleanUtils.xor(array));
		Debug.debug(BooleanUtils.toString(ok, "OK", "FAILED"));
	}
	
	public void testChar() throws Exception {
		String in = "abc123ddddeeeef";
		Debug.debug(CharSetUtils.delete(in, CharSet.ASCII_NUMERIC.toString()));
		Debug.debug(CharSetUtils.keep(in, CharSet.ASCII_NUMERIC.toString()));
		Debug.debug(CharSetUtils.squeeze(in, CharSet.ASCII_ALPHA.toString()));
	}
	
	public void testRandom() throws Exception {
		Debug.debug(RandomStringUtils.randomAscii(4));
		Debug.debug(RandomStringUtils.randomAlphabetic(4));
		Debug.debug(RandomStringUtils.randomNumeric(4));

	}

}
