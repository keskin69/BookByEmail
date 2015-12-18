package yellowzebra.util;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.border.LineBorder;

public class MailConfig {
	public static final DateFormat SHORTDATE = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DEFAULT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final DateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm");
	public static final LineBorder LineBorder = new LineBorder(new Color(0, 0, 0), 1, true);
}
