package yellowzebra.parser;

import yellowzebra.util.MyBooking;

public interface IParser {
	boolean isApplicable(String subject, String from);
	MyBooking parse(String subject, String msg);
}
