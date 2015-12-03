package yellowzebra.parser;

import io.swagger.client.model.Booking;

public interface IParser {
	boolean isApplicable(String subject, String from);
	String DELIM = ",";
	Booking parse(String msg);
}
