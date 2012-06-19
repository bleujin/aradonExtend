package net.ion.radon.impl.let.vfs;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to generate HTTP dates.
 * 
 * @author Remy Maucherat
 */
public final class FastHttpDateFormat {

	// -------------------------------------------------------------- Variables

	/**
     *
     */
	protected static final int CACHE_SIZE = Integer.parseInt(System.getProperty("org.apache.tomcat.util.http.FastHttpDateFormat.CACHE_SIZE", "1000"));

	/**
	 * HTTP date format.
	 */
	protected static final SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

	/**
	 * The set of SimpleDateFormat formats to use in getDateHeader().
	 */
	protected static final SimpleDateFormat formats[] = { new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US),
			new SimpleDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US) };

	/**
     *
     */
	protected final static TimeZone gmtZone = TimeZone.getTimeZone("GMT");

	/**
	 * GMT timezone - all HTTP dates are on GMT
	 */
	static {

		format.setTimeZone(gmtZone);

		formats[0].setTimeZone(gmtZone);
		formats[1].setTimeZone(gmtZone);
		formats[2].setTimeZone(gmtZone);

	}

	/**
	 * Instant on which the currentDate object was generated.
	 */
	protected static long currentDateGenerated = 0L;

	/**
	 * Current formatted date.
	 */
	protected static String currentDate = null;

	/**
	 * Formatter cache.
	 */
	protected static final ConcurrentHashMap<Long, String> formatCache = new ConcurrentHashMap<Long, String>(CACHE_SIZE);

	/**
	 * Parser cache.
	 */
	protected static final ConcurrentHashMap<String, Long> parseCache = new ConcurrentHashMap<String, Long>(CACHE_SIZE);

	// --------------------------------------------------------- Public Methods

	/**
	 * Get the current date in HTTP format.
	 * 
	 * @return the formatted date
	 */
	public static final String getCurrentDate() {

		long now = System.currentTimeMillis();
		if (now - currentDateGenerated > 1000)
			synchronized (format) {
				if (now - currentDateGenerated > 1000) {
					currentDateGenerated = now;
					currentDate = format.format(new Date(now));
				}
			}
		return currentDate;

	}

	/**
	 * Get the HTTP format of the specified date.
	 * 
	 * @param value
	 * @param threadLocalformat
	 * @return the formatted date
	 */
	public static final String formatDate(long value, DateFormat threadLocalformat) {

		Long longValue = new Long(value);
		String cachedDate = formatCache.get(longValue);
		if (cachedDate != null)
			return cachedDate;

		String newDate = null;
		Date dateValue = new Date(value);
		if (threadLocalformat != null) {
			newDate = threadLocalformat.format(dateValue);
			updateFormatCache(longValue, newDate);
		} else
			synchronized (formatCache) {
				synchronized (format) {
					newDate = format.format(dateValue);
				}
				updateFormatCache(longValue, newDate);
			}
		return newDate;

	}

	/**
	 * Try to parse the given date as a HTTP date.
	 * 
	 * @param value
	 * @param threadLocalformats
	 * @return the date value
	 */
	public static final long parseDate(String value, DateFormat[] threadLocalformats) {

		Long cachedDate = parseCache.get(value);
		if (cachedDate != null)
			return cachedDate.longValue();

		Long date = null;
		if (threadLocalformats != null) {
			date = internalParseDate(value, threadLocalformats);
			updateParseCache(value, date);
		} else
			synchronized (parseCache) {
				date = internalParseDate(value, formats);
				updateParseCache(value, date);
			}
		if (date == null)
			return -1L;
		return date.longValue();

	}

	/**
	 * Parse date with given formatters.
	 * 
	 * @param value
	 * @param theFormats
	 * @return the date value
	 */
	private static final Long internalParseDate(String value, DateFormat[] theFormats) {
		Date date = null;
		for (int i = 0; date == null && i < theFormats.length; i++)
			try {
				date = theFormats[i].parse(value);
			} catch (ParseException e) {
				// Do nothing.
			}
		if (date == null)
			return null;
		return new Long(date.getTime());
	}

	/**
	 * Update cache.
	 * 
	 * @param key
	 * @param value
	 */
	private static void updateFormatCache(Long key, String value) {
		if (value == null)
			return;
		if (formatCache.size() > CACHE_SIZE)
			formatCache.clear();
		formatCache.put(key, value);
	}

	/**
	 * Update cache.
	 * 
	 * @param key
	 * @param value
	 */
	private static void updateParseCache(String key, Long value) {
		if (value == null)
			return;
		if (parseCache.size() > CACHE_SIZE)
			parseCache.clear();
		parseCache.put(key, value);
	}

}
