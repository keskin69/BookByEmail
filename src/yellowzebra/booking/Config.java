package yellowzebra.booking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Config {
	public static final DateFormat SHORTDATE = new SimpleDateFormat("yyyy-MM-dd");
	public static final String API_DATE = "yyyy-MM-dd'T'HH:mm:ssXXX";
	public static final DateFormat APIFORMAT = new SimpleDateFormat(API_DATE);
	public static final DateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm");
	public static final String API_KEY = "AW6C7FCAPXMJ4FF6X4JKK3250TNK33C151257FB555";
	public static final String SECRET_KEY = "jxjIeX8QFjltj1wY56UFDs5KAgAGCF72";
	public static final DateFormat URLDATE = new SimpleDateFormat("yyyy'%2d'MM'%2d'dd'T'HH'%3a'mm'%3a'ss'%2b02%3a00'");
	public static final String APIBASE = "https://api.bookeo.com/v2/bookings?apiKey=" + API_KEY + "&secretKey=" + SECRET_KEY;

}
