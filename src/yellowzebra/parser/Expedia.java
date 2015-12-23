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
	private static final DateFormat EXPEDIA_DATE = new SimpleDateFormat("yyyy/MM/dd");

	public Expedia() {
		subjectReg = "Daily Booking Report";
		fromReg = "notify@localexpertpartnercentral.com";
		agent = "Expedia";
		folder = "EXP";

		core();
	}


	public MyBooking parse(String subject, String msg) throws Exception {
		String line = null;
		String token[] = null;

		content = msg;

		// Main message body
		skipAfter("Ticket Type:");

		// set participants
		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();

		line = getLine();
		token = split(line, " ");
		for (String str : token) {
			if (!str.equals("")) {
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
		skipAfter("Valid Days:");
		line = getLine();
		token = split(line, "-");

		Date date = null;
		try {
			date = EXPEDIA_DATE.parse(token[0]);
			mybooking.tourDate = date;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// product
		skipAfter("Item:");
		line = getLine();
		token = split(line, "-");
		String product = token[0];
		mybooking.booking.setProductName(product);

		// time
		token = split(token[1], " ");
		String time = token[0];

		if (token[1].equals("PM")) {
			time = String.valueOf(12 + new Integer(time).intValue());
		}

		time += ":00";
		mybooking.tourTime = time;

		skipAfter("Voucher #:");
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
