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
import io.swagger.client.model.StreetAddress;
import yellowzebra.ui.ParserUI;
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
		folder = "GYG";

		core();
	}

	public void trimBody(String msg) {
		content = msg;
		skipAfter("Option:");
		getLine();
	}

	public MyBooking parse(String subject, String msg) throws Exception {
		String line = null;
		String token[] = null;

		trimBody(msg);

		line = getLine();
		String product = split(line, "\\(")[0];
		mybooking.booking.setProductName(product);
		
		skipAfter("Date:");
		line = getNextLine();
		token = split(line, ",");

		Date date = null;
		try {
			date = GETTY_DATE.parse(token[0]);
			mybooking.tourDate = date;
		} catch (ParseException e) {
			Logger.err("Wrong date format: " + token[0]);
			return null;
		}

		String time = split(token[1], " ")[0];
		mybooking.tourTime = time;

		// Participants
		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();
		skipAfter("Number of participants:");
		line = getNextLine();
		token = split(line, " ");
		number.setNumber(Integer.parseInt(token[0]));
		number.setPeopleCategoryId("Cadults");
		peopleList.add(number);

		line = getNextLine();
		if (!line.startsWith("Reference")) {
			token = split(line, " ");
			number.setNumber(Integer.parseInt(token[0]));
			number.setPeopleCategoryId("Cchildren");
			peopleList.add(number);
		}

		participants.setNumbers(peopleList);
		participants.setDetails(null);
		mybooking.booking.setParticipants(participants);

		// voucher
		line = getLine();
		mybooking.voucherNumber = line;

		// Customer
		Customer customer = new Customer();

		skipAfter("Main customer:\n");
		line = getLine();
		token = line.split(" ", 2);
		customer.setFirstName(token[0].trim());
		customer.setLastName(token[1].trim());

		String str = "";
		while (true) {
			line = getLine();
			if (line.contains("@")) {
				break;
			}

			str += line;
		}
		StreetAddress address = new StreetAddress();
		address.setAddress1(str);
		customer.setStreetAddress(address);

		customer.setEmailAddress(line);

		line = getNextLine();
		customer.setPhoneNumbers(ParserUtils.setPhone(line));

		customer.setCustomFields(null);
		mybooking.booking.setCustomer(customer);

		// Title
		mybooking.booking.setTitle(mybooking.agent + "-" + mybooking.voucherNumber);

		return mybooking;

	}

	public static void main(String[] args) {
		ParserUI.init();

		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\getty.html");
			msg = ParserUtils.html2Text(msg);
		} catch (IOException e1) {
			Logger.exception(e1);
		}

		Getty parser = new Getty();
		MyBooking booking;
		try {
			booking = parser.parse("", msg);
			booking.dump();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
