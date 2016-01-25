package yellowzebra.parser;

import yellowzebra.util.MyBooking;

public abstract class AParser implements IParser {
	protected String subjectReg = null;
	protected String fromReg = null;
	protected String agent = null;
	protected MyBooking mybooking = null;
	protected String content;
	public String folder = null;

	protected void core() {
		mybooking = new MyBooking();
		mybooking.agent = agent;
		mybooking.shortAgentName = folder;
	}

	public boolean isApplicable(String subject, String from) {
		if (subject.toUpperCase().startsWith(subjectReg.toUpperCase())
				&& from.toUpperCase().equals(fromReg.toUpperCase())) {
			return true;
		}

		return false;
	}

	public static String[] split(String str, String delim) {
		String token[] = str.split(delim);

		for (int i = 0; i < token.length; i++) {
			token[i] = token[i].replace(String.valueOf((char) 160), " ").trim();
		}

		return token;
	}

	public String getLine() {
		int idx = content.indexOf("\n");
		String line = content.substring(0, idx);
		content = content.substring(idx + 1);

		return line.trim();
	}

	public String getNextLine() {
		getLine();
		return getLine();
	}

	public void skipAfter(String key) {
		int iS = content.indexOf(key);

		content = content.substring(iS + key.length());
	}

	public String strip(String msg, String key) {
		int iS = msg.indexOf(key);

		return msg.substring(iS + key.length());
	}

}
