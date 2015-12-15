package yellowzebra.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class MailConfig {
	public static final DateFormat SHORTDATE = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DEFAULT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final DateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm");
}
