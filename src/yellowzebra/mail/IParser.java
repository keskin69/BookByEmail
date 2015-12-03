package yellowzebra.mail;

import io.swagger.client.model.Booking;

public interface IParser {
	boolean isApplicable(String subject, String from);

	Booking parse(String msg);
}
