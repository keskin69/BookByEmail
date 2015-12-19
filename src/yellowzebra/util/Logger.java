package yellowzebra.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import yellowzebra.ui.LinkLabel;

public class Logger {
	public static LinkLabel label = null;
	private static PrintWriter writer = null;
	private static final DateFormat TIMEFORMAT = new SimpleDateFormat("HH:mm:ss");

	public static void init() {

		if (writer == null) {
			try {
				String logFile = System.getProperty("java.io.tmpdir") + "\\parser.log";
				System.out.println("Logging started in " + logFile);
				writer = new PrintWriter(logFile, "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close() {
		writer.close();
	}

	private static void write2Log(String str) {
		writer.println(TIMEFORMAT.format(new Date()) + ">" + str);
		writer.flush();
	}

	public static void log(String str) {
		if (label == null) {
			System.out.println(str);
		} else {
			label.setText(str);
		}

		write2Log(str);
	}

	public static void err(String str) {
		if (label == null) {
			System.out.println("ERROR:" + str);
		} else {
			label.setError(str);
			System.out.println("ERROR:" + str);
		}

		write2Log(str);
	}

	public static void exception(Exception ex) {
		err(ex.getMessage());
		write2Log(ex.getMessage());
		write2Log(ex.getStackTrace().toString());
	}
}
