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

	public String trimBody(String msg) {
		// Main parsing
		msg = skipUntil(msg, "Product Name");

		return msg;
	}

	public MyBooking parse(String subject, String msg) {
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\isango.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String line = null;
		String token[] = null;

		msg = trimBody(msg);

		String product = getLine(msg);
		msg = skipUntil(msg, "Start date of travel");
		line = getLine(msg);

		Date date = null;
		try {
			date = ISAGO_DATE.parse(line);
		} catch (ParseException e) {
			Logger.err("Wrong date format " + line);
			return null;
		}

		msg = skipUntil(msg, "Start Time");
		line = getLine(msg);
		token = split(line, " ");
		String time = token[0].substring(0,2) + ":" + token[0].substring(2,4);
	
		setProduct(product, date, time);

		// Customer
		Customer customer = new Customer();
		msg = skipUntil(msg, "Lead Passenger Name");
		line = getLine(msg);
		token = line.split(" ", 2);
		customer.setFirstName(token[0].trim());
		customer.setLastName(token[1].trim());

		msg = skipUntil(msg, "Lead Passenger Email");
		line = getLine(msg);
		customer.setEmailAddress(line);

		msg = skipUntil(msg, "Lead Passenger Phone");
		line = getLine(msg);
		customer.setPhoneNumbers(ParserUtils.setPhone(line));
		customer.setCustomFields(null);

		booking.setCustomer(customer);

		// Participants
		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();

		msg = skipUntil(msg, "No of Adult Passengers");
		line = getLine(msg);
		number.setNumber(new Integer(line));
		number.setPeopleCategoryId("Cadults");
		peopleList.add(number);

		msg = skipUntil(msg, "No of Child Passengers");
		line = getLine(msg);
		if (!line.trim().equals("")) {
			number.setNumber(new Integer(line));
			number.setPeopleCategoryId("Cchildren");
			peopleList.add(number);
		}
		participants.setNumbers(peopleList);
		participants.setDetails(null);
		booking.setParticipants(participants);

		msg = skipUntil(msg, "Special Request");
		int idx = msg.indexOf("End customer Total price:");
		booking.details = msg.substring(0, idx).trim();

		// Voucher
		token = subject.split(" ");
		booking.voucherNumber = token[token.length - 1];
		booking.setTitle(booking.agent + "-" + booking.voucherNumber);

		return booking;
	}

	public static void main(String[] args) {
		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\isango.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Isango parser = new Isango();
		MyBooking booking = parser.parse("Booking Confirmation - ISA357761", msg);
		booking.dump();

		/*
		 * try { CreateBooking.postBooking(booking); } catch (ApiException e) {
		 * Logger.err(e.getMessage()); }
		 */
	}
}
