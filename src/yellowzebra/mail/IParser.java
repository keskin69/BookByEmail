package yellowzebra.mail;

import javax.mail.Message;

import io.swagger.client.model.Booking;

public interface IParser {
	boolean isApplicable(String subject, String from);

	Booking parse(Message message);
}
