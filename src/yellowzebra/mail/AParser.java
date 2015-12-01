package yellowzebra.mail;

import javax.mail.Message;

import io.swagger.client.ApiException;
import io.swagger.client.model.Booking;
import yellowzebra.booking.CreateBooking;
import yellowzebra.util.Logger;

public abstract class AParser implements IParser {
	public String subjectReg = null;
	public String fromReg = null;

	public boolean isApplicable(String subject, String from) {
		if ((subject != null) && (from != null)) {
			if (subject.matches(subjectReg) && from.matches(fromReg)) {
				return true;
			}
		}

		return false;
	}

	public final boolean postBooking(Message msg) {
		Booking booking = parse(msg);

		if (booking != null) {
			try {
				Logger.log("Posting new booking");
				CreateBooking.postBooking(booking);
				
				return true;
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}

		return false;
	}

}
