package yellowzebra.parser;

import java.io.IOException;
import java.io.PrintWriter;
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

public class CityDiscovery2 extends AParser {
	private static final DateFormat CITY_DATE = new SimpleDateFormat("yyyy-MM-dd");

	public CityDiscovery2() {
		subjectReg = "Booking ref";
		fromReg = "customerservice@city-discovery.com";
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

		skipAfter("Mobile :");
		line = getLine();
		line = getLine(); // phone
		if (!line.startsWith("Country")) {
			try {
				cus.setPhoneNumbers(ParserUtils.setPhone(line));
			} catch (Exception ex) {
				cus.setPhoneNumbers(null);
			}
		}

		mybooking.booking.setCustomer(cus);

		// tour
		skipAfter("Name of the Tour :");
		line = getLine();
		token = line.split("-");

		String product = token[token.length - 1].trim();
		mybooking.booking.setProductName(product);

		skipAfter("Date and time of the Tour :");
		line = getLine();
		token = line.split(" ");
		String dStr = token[0];
		String tStr = token[2];

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
		PeopleNumber number = null;

		number = new PeopleNumber();
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
		try {
			if (Integer.parseInt(token[0]) > 0) {
				number.setNumber(Integer.parseInt(token[0]));
				number.setPeopleCategoryId("Cinfants");
				peopleList.add(number);
			}
		} catch (NumberFormatException ex) {

		}

		participants.setNumbers(peopleList);
		participants.setDetails(null);
		mybooking.booking.setParticipants(participants);

		// others
		skipAfter("Address of customer");
		getLine();
		int idx = content.indexOf("Customer Comment");
		if (idx > 0) {
			mybooking.pickup = content.substring(0, idx);
		}

		skipAfter("Customer Comment");
		getLine();
		idx = content.indexOf("Arrival date");
		if (idx > 3) {
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
			msg = ParserUtils.readFile("C:\\Users\\emuskes\\workspace\\YellowParser\\samples\\city_new1.html");
			PrintWriter writer = new PrintWriter("C:\\Users\\emuskes\\workspace\\YellowParser\\samples\\city_new1.txt",
					"UTF-8");
			msg = ParserUtils.html2Text(msg);
			writer.print(msg);
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		CityDiscovery2 parser = new CityDiscovery2();
		MyBooking booking;
		try {
			booking = parser.parse("Booking ref NICK447978", msg);
			booking.dump();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
