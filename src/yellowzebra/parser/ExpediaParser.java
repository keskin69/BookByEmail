package yellowzebra.parser;

import java.io.IOException;

import io.swagger.client.Config;
import io.swagger.client.model.Booking;
import io.swagger.client.model.Customer;
import yellowzebra.booking.EventTools;
import yellowzebra.booking.ProductTools;
import yellowzebra.util.ParserUtils;

public class ExpediaParser extends AParser {
	public final String subjectReg = "Expedia - Booking report";
	public final String fromReg = "Notifications@expediacustomer.com";
	private String time;
	private String product;

	public boolean isApplicable(String subject, String from) {
		if (subject.equals(subjectReg) && from.equals(fromReg)) {
			return true;
		}

		return false;
	}

	public String trimBody(String msg) {
		// Main parsing
		msg = skipUntil(msg, "BOOKING REPORT");
		msg = skipUntil(msg, "Booking");

		return msg;
	}

	public Booking parse(String msg) {
		Booking booking = new Booking();
		String line = null;
		String token[] = null;

		msg = trimBody(msg);

		// Setting customer
		Customer cus = new Customer();

		line = findLine(msg, "Primary Redeemer");
		line = strip(line, ":").trim();
		token = line.split(",");
		cus.setPhoneNumbers(setPhone(token[1]));
		cus.setEmailAddress(token[2].trim());

		token = token[0].split(" ", 2);
		cus.setFirstName(token[0].trim());
		cus.setLastName(token[1].trim());

		booking.setCustomer(cus);

		// date time
		line = findLine(msg, "Valid Days");
		line = strip(line, ":").trim();
		token = line.split("-");
		String date = token[0].trim();
		//Date d = Config.SHORTDATE.parse(source)
		//String date = Config.SHORTDATE.parse(date);
		String productId = ProductTools.getInstance().getProductId(product);
		String eventId = new EventTools().getEventId(productId, date, time);

		return booking;
	}

	public static void main(String[] args) {
		String msg;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\expedia.txt");
			ExpediaParser parser = new ExpediaParser();
			Booking booking = parser.parse(msg);
			parser.dump(booking);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
