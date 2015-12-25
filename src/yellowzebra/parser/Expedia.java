package yellowzebra.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.model.Booking;
import io.swagger.client.model.Customer;
import io.swagger.client.model.Participants;
import io.swagger.client.model.PeopleNumber;
import yellowzebra.booking.CreateBooking;
import yellowzebra.ui.ParserUI;
import yellowzebra.util.BookingException;
import yellowzebra.util.Logger;
import yellowzebra.util.MyBooking;
import yellowzebra.util.ParserUtils;

public class Expedia extends AParser {
	private static final DateFormat EXPEDIA_DATE = new SimpleDateFormat("MMMM dd, yyyy");

	public Expedia() {
		subjectReg = "Daily Booking Report";
		fromReg = "notify@localexpertpartnercentral.com";
		agent = "Expedia";
		folder = "EXP";

		core();
	}

	public MyBooking parse(String subject, String msg) throws Exception {
		if (!subject.contains("(1)")) {
			throw new BookingException("This message contains more than 1 booking. It should be parsed manually.");
		}

		String line = null;
		String token[] = null;

		content = msg;

		// Main message body
		skipAfter("Ticket Type:");

		// set participants
		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();

		line = getLine();
		token = split(line, ",");
		for (String str : token) {
			if (!str.equals("")) {
				PeopleNumber number = new PeopleNumber();
				String t[] = split(str, " ");
				number.setNumber(Integer.parseInt(t[1]));
				number.setPeopleCategoryId(ParserUtils.getCustomerType(t[0]));
				peopleList.add(number);
			}
		}

		participants.setNumbers(peopleList);
		participants.setDetails(null);
		mybooking.booking.setParticipants(participants);

		// Setting customer
		Customer cus = new Customer();

		skipAfter("Primary Redeemer:");
		line = getLine();
		token = split(line, ",");
		cus.setPhoneNumbers(ParserUtils.setPhone(token[1]));
		cus.setEmailAddress(token[2]);
		cus.setCustomFields(null);

		token = token[0].split(" ", 2);
		cus.setFirstName(token[0].trim());
		cus.setLastName(token[1].trim());

		mybooking.booking.setCustomer(cus);

		// date
		skipAfter("Valid Day:");
		line = getLine();

		Date date = null;
		try {
			date = EXPEDIA_DATE.parse(line);
			mybooking.tourDate = date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// product
		skipAfter("Item:");
		line = getLine();
		token = split(line, "/");
		String product = token[0];
		mybooking.booking.setProductName(product);

		// time
		try {
			boolean isAM = true;

			int idx = line.indexOf(" AM ");
			if (idx == -1) {
				isAM = false;
				idx = line.indexOf(" PM ");
			}

			int i = idx - 1;
			while (line.charAt(i) != ' ') {
				i--;
			}

			String time = line.substring(i + 1, idx);

			if (time.length() == 4) {
				time = "0" + time;
			} else if (time.length() == 2) {
				time = time + ":00";
			} else if (time.length() == 1) {
				time = "0" + time + ":00";
			}

			if (!isAM) {
				token = split(time, ":");
				time = String.valueOf(12 + new Integer(token[0]).intValue()) + ":" + token[1];
			}

			mybooking.tourTime = time;
		} catch (Exception ex) {
			mybooking.tourTime = "hh:mm";
			throw new BookingException("Cannot parse event time properly.");
		}

		skipAfter("Voucher:");
		line = getLine();
		token = split(line, "Itin");
		mybooking.voucherNumber = token[0];
		mybooking.booking.setTitle(mybooking.agent + "-" + mybooking.voucherNumber);

		return mybooking;
	}

	public static void main(String[] args) {
		ParserUI.init();

		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\expedia.txt");
		} catch (IOException e1) {
			Logger.exception(e1);
		}

		Expedia parser = new Expedia();
		MyBooking mybooking;
		try {
			mybooking = parser.parse(null, msg);
			try {
				Booking finalBooking = mybooking.getBooking();
				mybooking.dump();

				CreateBooking.postBooking(finalBooking);
			} catch (ApiException e) {
				Logger.err(e.getMessage());
			} catch (BookingException e) {
				Logger.err(e.getMessage());
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}
