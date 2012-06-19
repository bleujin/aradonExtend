package net.ion.radon.impl.let.webdav.back;

import java.io.UnsupportedEncodingException;

public class WebDavUtil {

	public static String escape(String fragment) {
		try {
			fragment = java.net.URLEncoder.encode(fragment, "UTF-8");
			// disallow "+" encoding of spaces -- it does not work for many WebDAV clients, whereas %20 encoding does work.
			fragment = replace(fragment, "+", "%20");
		} catch (final UnsupportedEncodingException unlikely) {
			throw new RuntimeException("Expected encoding UTF-8 not available");
		}
		return fragment;
	}

	static String replace(final String in, final String token, final String value) {
		// short circuit eliminates NPEs here
		if ((in == null) || (token == null) || (value == null) || "".equals(token) || // token is empty
				(in.indexOf(token) == -1))
			return in;
		int found;
		int last = 0;

		// allocate space for at least 1 replacement a second replacement may require reallocation of string buff size
		final StringBuilder newsb = new StringBuilder(in.length() + value.length() + 4);

		do {
			found = in.indexOf(token, last);
			if (found > -1) {
				newsb.append(in.substring(last, found));
				newsb.append(value);
				last = found + token.length();
			}
		} while (found > -1);
		newsb.append(in.substring(last));

		return newsb.toString();
	}

}
