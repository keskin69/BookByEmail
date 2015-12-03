package yellowzebra.mail;

import io.swagger.client.model.Booking;

public class ExpediaParser extends AParser {
	public final String subjectReg = "Expedia - Booking report";
	public final String fromReg = "Notifications@expediacustomer.com";

	public ExpediaParser() {

	}

	public boolean isApplicable(String subject, String from) {
		if (subject.equals(subjectReg) && from.equals(fromReg)) {
			return true;
		}

		return false;
	}

	public Booking parse(String msg) {
		Booking booking = new Booking();
		booking.setBookingNumber("123");
		return booking;
	}

}
