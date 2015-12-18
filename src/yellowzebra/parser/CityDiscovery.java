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
import yellowzebra.util.MyBooking;
import yellowzebra.util.ParserUtils;

public class CityDiscovery extends AParser {
	private static final DateFormat CITY_DATE = new SimpleDateFormat("yyyy-mm-dd");

	public CityDiscovery() {
		subjectReg = "Booking ref";
		fromReg = "confirmation@city-discovery.com";
		agent = "CityDiscover";

		core();
	}

	public void trimBody(String msg) {
		content = msg;
		skipAfter("Customer details :");
	}

	public MyBooking parse(String subject, String msg) {
		String line = null;
		String token[] = null;

		trimBody(msg);

		// Setting customer
		Customer cus = new Customer();

		line = getNextLine();
		token = split(line, ":");
		token = token[1].split(" ", 2);
		cus.setFirstName(token[0].trim());
		cus.setLastName(token[1].trim());

		line = getLine(); // phone
		token = split(line, ":");
		cus.setPhoneNumbers(ParserUtils.setPhone(token[1]));

		cus.setCustomFields(null);
		booking.setCustomer(cus);

		// tour
		skipAfter("Name of the Tour :");
		line = getLine();
		token = line.split(" ", 2);
		String product = token[1];

		skipAfter("Date and time of the Tour :");
		line = getNextLine();
		Date date = null;
		try {
			date = CITY_DATE.parse(line);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		line = getNextLine();
		String time = line;
		setProduct(product, date, time);

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

		skipAfter("Number of children :");
		line = getLine();
		token = split(line, " ");
		if (Integer.parseInt(token[0]) > 0) {
			number.setNumber(Integer.parseInt(token[0]));
			number.setPeopleCategoryId("Cadults");
			peopleList.add(number);
		}

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
		booking.setParticipants(participants);

		// others
		skipAfter("Address of customer for pick up (if possible) :");
		line = getNextLine();
		booking.pickup = line;

		skipAfter("Customer Comment:");
		line = getNextLine();
		if (!line.startsWith("Arrival date")) {
			booking.details = line;
		}

		booking.voucherNumber = split(subject, " ")[2];
		booking.setTitle(booking.agent + "-" + booking.voucherNumber);

		return booking;

	}

	public static void main(String[] args) {
		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\city.html");
			msg = ParserUtils.html2Text(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		CityDiscovery parser = new CityDiscovery();
		MyBooking booking = parser.parse("Booking ref EVEL403406", msg);
		booking.dump();
	}
}
