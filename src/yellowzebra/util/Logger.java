package yellowzebra.util;

import yellowzebra.ui.LinkLabel;

public class Logger {
	public static LinkLabel label = null;

	public static void log(String str) {
		if (label == null) {
			System.out.println(str);
		} else {
			label.setText(str);
		}
	}

	public static void err(String str) {
		if (label == null) {
			System.out.println("ERROR:" + str);
		} else {
			label.setError(str);
		}
	}

	public static void err(String err, String str) {
		if (label == null) {
			System.out.println(str);
			System.out.println("Check following exception:" + "\n" + err);
		} else {
			label.setError(str);
		}
	}
}
