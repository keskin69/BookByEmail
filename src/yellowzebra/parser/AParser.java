package yellowzebra.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;

import io.swagger.client.ApiException;
import io.swagger.client.model.Customer;
import io.swagger.client.model.PhoneNumber;
import io.swagger.client.model.PhoneNumber.TypeEnum;
import yellowzebra.booking.CreateBooking;
import yellowzebra.util.Logger;
import yellowzebra.util.MyBooking;

public abstract class AParser implements IParser {
	public String subjectReg = null;
	public String fromReg = null;

	public List<PhoneNumber> setPhone(String phone) {
		ArrayList<PhoneNumber> list = new ArrayList<PhoneNumber>();
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.setNumber(phone.trim());
		phoneNumber.setType(TypeEnum.MOBILE);
		list.add(phoneNumber);

		return list;
	}

	public String trimBody(String msg) {
		return msg;
	}

	public void dump(MyBooking booking) {
		System.out.println("Product:");
		System.out.println(booking.getProductId() + "\t" + booking.getEventId());
		System.out.println(booking.getStartTime().toString());
		Customer customer = booking.getCustomer();
		System.out.println("Customer:\n" + customer.getFirstName() + " " + customer.getLastName());
		System.out.println(customer.getEmailAddress() + "\t" + customer.getPhoneNumbers().get(0).getNumber());
	}

	public boolean isApplicable(String subject, String from) {
		if ((subject != null) && (from != null)) {
			if (subject.matches(subjectReg) && from.matches(fromReg)) {
				return true;
			}
		}

		return false;
	}

	public final boolean postBooking(Message msg) {
		MyBooking booking = null;
		try {
			booking = parse(msg.getContent().toString());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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

	public static String getCustomerType(String in) {
		in = in.toUpperCase().trim();

		if (in.startsWith("ADULTS")) {
			return "Cadults";
		} else if (in.startsWith("CHILDREN")) {
			return "Cchildren";
		} else if (in.startsWith("INFANTS")) {
			return "Cinfants";
		}

		Logger.err("Unknown customer type" + in);

		return null;
	}

	public static String[] split(String str, String delim) {
		String token[] = str.split(delim);

		for (int i = 0; i < token.length; i++) {
			token[i] = token[i].trim();
		}

		return token;
	}

	public static String skipUntil(String msg, String key) {
		int iS = msg.indexOf(key);
		int iE = msg.indexOf("\n", iS + 1);

		return msg.substring(iE + 1);
	}

	public static String strip(String msg, String key) {
		int iS = msg.indexOf(key);

		return msg.substring(iS+key.length());
	}

	public static String findLine(String msg, String key) {
		int iS = msg.indexOf(key);
		int iE = msg.indexOf("\n", iS + 1);

		return msg.substring(iS + key.length() + 1, iE).trim();
	}
}
