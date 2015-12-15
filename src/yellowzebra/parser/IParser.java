package yellowzebra.parser;

import yellowzebra.util.MyBooking;

public interface IParser {
	boolean isApplicable(String subject, String from);

	String DELIM = ",";

	MyBooking parse(String subject, String msg);
}
