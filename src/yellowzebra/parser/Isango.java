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
import yellowzebra.ui.ParserUI;
import yellowzebra.util.Logger;
import yellowzebra.util.MyBooking;
import yellowzebra.util.ParserUtils;

public class Isango extends AParser {
	private static final DateFormat ISAGO_DATE = new SimpleDateFormat("dd MMM yyyy");

	public Isango() {
		subjectReg = "Booking Confirmation - ISA";
		fromReg = "support@isango.com";
		agent = "Isango";
		folder = "ISG";

		core();
	}

	public void trimBody(String msg) {
		content = msg;

		skipAfter("Product Name\n");
	}

	public MyBooking parse(String subject, String msg) throws Exception {
		String line = null;
		String token[] = null;

		trimBody(msg);

		String product = getLine();
		mybooking.booking.setProductName(product);

		skipAfter("Start date of travel\n");
		line = getLine();

		Date date = null;
		try {
			date = ISAGO_DATE.parse(line);
			mybooking.tourDate = date;
		} catch (ParseException e) {
			Logger.err("Wrong date format " + line);
		}

		skipAfter("Start Time\n");
		line = getLine();
		token = split(line, " ");
		String time = token[0].substring(0, 2) + ":" + token[0].substring(2, 4);
		mybooking.tourTime = time;

		// Customer
		Customer customer = new Customer();
		skipAfter("Lead Passenger Name\n");
		line = getLine();
		token = line.split(" ", 2);
		customer.setFirstName(token[0].trim());
		customer.setLastName(token[1].trim());

		skipAfter("Lead Passenger Email\n");
		line = getLine();
		customer.setEmailAddress(line);

		skipAfter("Lead Passenger Phone\n");
		line = getLine();
		customer.setPhoneNumbers(ParserUtils.setPhone(line));
		customer.setCustomFields(null);

		mybooking.booking.setCustomer(customer);

		// Participants
		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();

		skipAfter("No of Adult Passengers\n");
		line = getLine();
		number.setNumber(Integer.parseInt(line));
		number.setPeopleCategoryId("Cadults");
		peopleList.add(number);

		skipAfter("No of Child Passengers\n");
		line = getLine();
		try {
			number.setNumber(Integer.parseInt(line));
			number.setPeopleCategoryId("Cchildren");
			peopleList.add(number);
		} catch (NumberFormatException e) {
			// continue
		}

		participants.setNumbers(peopleList);
		participants.setDetails(null);
		mybooking.booking.setParticipants(participants);

		skipAfter("Special Request\n");
		int idx = content.indexOf("End customer Total price:");
		mybooking.details = content.substring(0, idx).trim();

		// Voucher
		token = split(subject, " ");
		mybooking.voucherNumber = token[token.length - 1];
		mybooking.booking.setTitle(mybooking.agent + "-" + mybooking.voucherNumber);

		return mybooking;
	}

	public static void main(String[] args) {
		ParserUI.init();

		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\isango.html");
			msg = ParserUtils.html2Text(msg);
		} catch (IOException e1) {
			Logger.exception(e1);
		}

		Isango parser = new Isango();
		MyBooking booking;
		try {
			booking = parser.parse("Booking Confirmation - ISA357761", msg);
			booking.dump();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
