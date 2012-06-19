package net.ion.im.bbs.ldap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class LDAPFilter {
	
	private final static String BASE_FILTER = "(objectclass=person)";
	
	private static String getBaseFilter() {
		return BASE_FILTER;
	}
	
	public static String getFilterWithName(String value) throws UnsupportedEncodingException {
		String cnValue = (value == null ? "(cn=*)" : String.format("(cn=*%s*)", URLDecoder.decode(value, "UTF-8")));
		return String.format("(&%s%s)", getBaseFilter(), cnValue);
	}
	
	public static String getFilterWithID(String id) {
		String emailValue = (id == null ? "*" : id);
		return String.format("(&%s(mail=%s@i-on.net))", getBaseFilter(), emailValue);
	}
}
