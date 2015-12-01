package yellowzebra.util;

public class Logger {
	public static void log(String str) {
		System.out.println(str);
	}

	public static void err(String str) {
		System.out.println(str);
	}

	public static void err(String err, String str) {
		System.out.println(str);
		System.out.println("Check following exception" + "\n" + err);
	}
}
