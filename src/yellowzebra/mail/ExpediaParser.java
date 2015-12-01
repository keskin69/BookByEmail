package yellowzebra.mail;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;

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

	public Booking parse(Message msg) {
		Booking booking = new Booking();
		
		// TODO Auto-generated method stub
		try {
			System.out.println(msg.getContent());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
