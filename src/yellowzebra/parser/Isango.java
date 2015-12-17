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

public class Isango extends AParser {
	private static final DateFormat ISAGO_DATE = new SimpleDateFormat("dd MMM yyyy");

	public Isango() {
		subjectReg = "Booking Confirmation - ISA";
		fromReg = "support@isango.com";
		agent = "Isango";

		core();
	}

	public void trimBody(String msg) {
		content = msg;

		skipAfter("Product Name");
	}

	public MyBooking parse(String subject, String msg) {
		String line = null;
		String token[] = null;

		trimBody(msg);

		String product = getLine();
		skipAfter("Start date of travel");
		line = getLine();

		Date date = null;
		try {
			date = ISAGO_DATE.parse(line);
		} catch (ParseException e) {
			Logger.err("Wrong date format " + line);
			return null;
		}

		skipAfter("Start Time");
		line = getLine();
		token = split(line, " ");
		String time = token[0].substring(0, 2) + ":" + token[0].substring(2, 4);

		setProduct(product, date, time);

		// Customer
		Customer customer = new Customer();
		skipAfter("Lead Passenger Name");
		line = getLine();
		token = line.split(" ", 2);
		customer.setFirstName(token[0].trim());
		customer.setLastName(token[1].trim());

		skipAfter("Lead Passenger Email");
		line = getLine();
		customer.setEmailAddress(line);

		skipAfter("Lead Passenger Phone");
		line = getLine();
		customer.setPhoneNumbers(ParserUtils.setPhone(line));
		customer.setCustomFields(null);

		booking.setCustomer(customer);

		// Participants
		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();

		skipAfter("No of Adult Passengers");
		line = getLine();
		number.setNumber(new Integer(line));
		number.setPeopleCategoryId("Cadults");
		peopleList.add(number);

		skipAfter("No of Child Passengers");
		line = getLine();
		try {
			number.setNumber(new Integer(line));
			number.setPeopleCategoryId("Cchildren");
			peopleList.add(number);
		} catch (NumberFormatException e) {
			// continue
		}

		participants.setNumbers(peopleList);
		participants.setDetails(null);
		booking.setParticipants(participants);

		skipAfter("Special Request");
		int idx = content.indexOf("End customer Total price:");
		booking.details = content.substring(0, idx).trim();

		// Voucher
		token = split(subject, " ");
		booking.voucherNumber = token[token.length - 1];
		booking.setTitle(booking.agent + "-" + booking.voucherNumber);

		return booking;
	}

	public static void main(String[] args) {
		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\isango.html");
			msg = ParserUtils.html2Text(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Isango parser = new Isango();
		MyBooking booking = parser.parse("Booking Confirmation - ISA357761", msg);
		booking.dump();
	}
}
