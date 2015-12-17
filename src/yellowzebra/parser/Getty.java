package yellowzebra.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.swagger.client.model.Customer;
import io.swagger.client.model.Participants;
import io.swagger.client.model.PeopleNumber;
import yellowzebra.util.Logger;
import yellowzebra.util.MyBooking;
import yellowzebra.util.ParserUtils;

public class Getty extends AParser {
	private static final DateFormat GETTY_DATE = new SimpleDateFormat("dd MMMM yyyy");

	public Getty() {
		// System.out.println(GETTY_DATE.format(new Date()));

		subjectReg = "Booking - ";
		fromReg = "do-not-reply@getyourguide.com";
		agent = "Getty";

		core();
	}

	public MyBooking parse(String subject, String msg) {
		// try {
		// msg =
		// ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\getty.html");
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		String line = null;
		String token[] = null;

		msg = trimBody(msg);
		line = getLine(msg);
		String product = split(line, "\\(")[0];

		msg = skipUntil(msg, "Date:");
		line = getLine(msg);
		token = split(line, ",");

		Date date = null;
		try {
			date = GETTY_DATE.parse(token[0]);
		} catch (ParseException e) {
			Logger.err("Wrong date format: " + token[0]);
			return null;
		}

		String time = split(token[1], " ")[0];

		setProduct(product, date, time);

		// Participants
		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();
		msg = skipUntil(msg, "Number of participants:");
		line = getLine(msg);
		token = split(line, " ");
		number.setNumber(new Integer(token[0]));
		number.setPeopleCategoryId("Cadults");
		peopleList.add(number);
		
		msg = skipUntil(msg, line);
		line = getLine(msg);
		if (!line.startsWith("Reference")) {
			token = split(line, " ");
			number.setNumber(new Integer(token[0]));
			number.setPeopleCategoryId("Cchildren");
			peopleList.add(number);
		}
		
		participants.setNumbers(peopleList);
		participants.setDetails(null);
		booking.setParticipants(participants);
		
		// voucher
		msg = skipUntil(msg, "Reference");
		booking.voucherNumber = getLine(msg);
		
		// Customer
		Customer customer = new Customer();
		customer.setFirstName(token[0].trim());
		customer.setLastName(token[1].trim());
		customer.setEmailAddress(line);
		customer.setPhoneNumbers(ParserUtils.setPhone(line));
		customer.setCustomFields(null);
		booking.setCustomer(customer);

		// Title
		booking.setTitle(booking.agent + "-" + booking.voucherNumber);

		return booking;
	}

	public String trimBody(String msg) {
		// Main parsing
		msg = skipUntil(msg, "Option:");

		return msg;
	}

	public static void main(String[] args) {
		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\getty.html");
			msg = ParserUtils.html2Text(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Getty parser = new Getty();
		MyBooking booking = parser.parse("", msg);
		booking.dump();
	}
}
