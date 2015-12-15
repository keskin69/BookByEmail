package yellowzebra.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.model.Customer;
import io.swagger.client.model.Participants;
import io.swagger.client.model.PeopleNumber;
import yellowzebra.booking.CreateBooking;
import yellowzebra.util.Logger;
import yellowzebra.util.MyBooking;
import yellowzebra.util.ParserUtils;

public class Expedia extends AParser {
	private  static final DateFormat EXPEDIA_DATE = new SimpleDateFormat("yyyy/MM/dd");
	
	public Expedia() {
		subjectReg = "Expedia - Booking report";
		fromReg = "Notifications@expediacustomer.com";
		agent = "Expedia";

		core();
	}

	public String trimBody(String msg) {
		// Main message body
		msg = skipUntil(msg, "BOOKING REPORT");
		msg = skipUntil(msg, "Booking");

		return msg;
	}

	public MyBooking parse(String subject, String msg) {
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\expedia.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String line = null;
		String token[] = null;
		String field = null;

		msg = trimBody(msg);

		// Setting customer
		Customer cus = new Customer();

		line = findLine(msg, "Primary Redeemer:");
		token = split(line, ",");
		cus.setPhoneNumbers(ParserUtils.setPhone(token[1]));
		cus.setEmailAddress(token[2]);
		cus.setCustomFields(null);

		token = token[0].split(" ", 2);
		cus.setFirstName(token[0].trim());
		cus.setLastName(token[1].trim());

		booking.setCustomer(cus);

		// date
		line = findLine(msg, "Valid Days:");
		token = split(line, "-");
		field = token[0];
		Date date = null;
		try {
			date = EXPEDIA_DATE.parse(field);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// product
		line = findLine(msg, "Item:");
		token = split(line, "-");
		String product = token[0];
		booking.setProductName(product);

		// time
		token = split(token[1], " ");
		String time = token[0];

		if (token[1].equals("PM")) {
			time = String.valueOf(12 + new Integer(time).intValue());
		}

		time += ":00";
		setProduct(product, date, time);

		// set participants
		line = findLine(msg, "Travellers:");
		line = strip(line, "-- ");
		token = split(line, ",");

		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();

		for (String str : token) {
			String t[] = split(str, " ");
			number.setNumber(new Integer(t[1]));
			number.setPeopleCategoryId(ParserUtils.getCustomerType(t[0]));
			peopleList.add(number);
		}
		participants.setNumbers(peopleList);
		participants.setDetails(null);
		booking.setParticipants(participants);

		line = findLine(msg, "Voucher #:");
		token = split(line, "Itin");
		booking.voucherNumber = token[0];
		booking.setTitle(booking.agent + "-" + booking.voucherNumber);

		return booking;
	}

	public static void main(String[] args) {
		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\expedia.txt");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Expedia parser = new Expedia();
		MyBooking booking = parser.parse(null, msg);
		// parser.dump(booking);
		try {
			CreateBooking.postBooking(booking);
		} catch (ApiException e) {
			Logger.err(e.getMessage());
		}

	}
}
