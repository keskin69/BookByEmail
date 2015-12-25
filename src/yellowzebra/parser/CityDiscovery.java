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

public class CityDiscovery extends AParser {
	private static final DateFormat CITY_DATE = new SimpleDateFormat("yyyy-mm-dd");

	public CityDiscovery() {
		subjectReg = "Booking ref";
		fromReg = "confirmation@city-discovery.com";
		agent = "CityDiscover";
		folder = "CD";

		core();
	}

	public void trimBody(String msg) {
		content = msg;
		skipAfter("Customer details :");
	}

	public MyBooking parse(String subject, String msg) throws Exception {
		String line = null;
		String token[] = null;

		trimBody(msg);

		// Setting customer
		Customer cus = new Customer();

		line = getNextLine();
		token = split(line, ":");
		token = token[1].split(" ", 2);
		cus.setFirstName(token[0].trim());
		try {
			cus.setLastName(token[1].trim());
		} catch (ArrayIndexOutOfBoundsException ex) {
			cus.setLastName("");
		}

		line = getLine(); // phone
		token = split(line, ":");
		try {
			cus.setPhoneNumbers(ParserUtils.setPhone(token[1]));
		} catch (Exception ex) {
			cus.setPhoneNumbers(null);
		}

		cus.setCustomFields(null);
		mybooking.booking.setCustomer(cus);

		// tour
		skipAfter("Name of the Tour :");
		line = getLine();
		if (line.contains("- -")) {
			token = line.split("- -", 2);
		} else if (line.contains("--")) {
			token = line.split("--", 2);
		} else {
			token = line.split(" ", 2);
		}

		String product = token[1].trim();
		mybooking.booking.setProductName(product);

		skipAfter("Date and time of the Tour :");
		line = getLine();

		if (line.equals("")) {
			line = getLine();
		}

		String dStr = null;
		String tStr = null;
		if (line.contains("yyyy")) {
			token = split(line, " ");
			dStr = token[0];
			tStr = token[3];
		} else {
			dStr = line;
			line = getNextLine();
			tStr = line;
		}

		Date date = null;
		try {
			date = CITY_DATE.parse(dStr);
			mybooking.tourDate = date;
		} catch (ParseException e) {
			Logger.err("Date information cannot be retrieved");
			Logger.exception(e);
		}

		mybooking.tourTime = tStr;

		// participants
		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();

		skipAfter("Number of adults :");
		line = getLine();
		token = split(line, " ");
		number.setNumber(Integer.parseInt(token[0]));
		number.setPeopleCategoryId("Cadults");
		peopleList.add(number);

		number = new PeopleNumber();
		skipAfter("Number of children :");
		line = getLine();
		token = split(line, " ");
		if (Integer.parseInt(token[0]) > 0) {
			number.setNumber(Integer.parseInt(token[0]));
			number.setPeopleCategoryId("Cadults");
			peopleList.add(number);
		}

		number = new PeopleNumber();
		skipAfter("Number of infants :");
		line = getLine();
		token = split(line, " ");
		if (Integer.parseInt(token[0]) > 0) {
			number.setNumber(Integer.parseInt(token[0]));
			number.setPeopleCategoryId("Cinfants");
			peopleList.add(number);
		}

		participants.setNumbers(peopleList);
		participants.setDetails(null);
		mybooking.booking.setParticipants(participants);

		// others
		skipAfter("Address of customer");
		getLine();
		int idx = content.indexOf("Customer Comment");
		if (idx != -1) {
			mybooking.pickup = content.substring(0, idx);
		}

		skipAfter("Customer Comment");
		getLine();
		idx = content.indexOf("Arrival date");
		if (idx != -1) {
			mybooking.details = content.substring(0, idx);
		}

		mybooking.voucherNumber = split(subject, " ")[2];
		mybooking.booking.setTitle(mybooking.agent + "-" + mybooking.voucherNumber);

		return mybooking;

	}

	public static void main(String[] args) {
		ParserUI.init();
		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\city.html");
			msg = ParserUtils.html2Text(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		CityDiscovery parser = new CityDiscovery();
		MyBooking booking;
		try {
			booking = parser.parse("Booking ref EVEL403406", msg);
			booking.dump();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
