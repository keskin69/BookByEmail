package yellowzebra.parser;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.model.Booking;
import io.swagger.client.model.Customer;
import io.swagger.client.model.Participants;
import io.swagger.client.model.PeopleNumber;
import io.swagger.client.model.Product.TypeEnum;
import yellowzebra.booking.CreateBooking;
import yellowzebra.booking.EventTools;
import yellowzebra.booking.ProductTools;
import yellowzebra.util.Logger;
import yellowzebra.util.MailConfig;
import yellowzebra.util.MyBooking;
import yellowzebra.util.ParserUtils;

public class ExpediaParser extends AParser {
	public final String subjectReg = "Expedia - Booking report";
	public final String fromReg = "Notifications@expediacustomer.com";

	public boolean isApplicable(String subject, String from) {
		if (subject.equals(subjectReg) && from.equals(fromReg)) {
			return true;
		}

		return false;
	}

	public String trimBody(String msg) {
		// Main parsing
		msg = skipUntil(msg, "BOOKING REPORT");
		msg = skipUntil(msg, "Booking");

		return msg;
	}

	public MyBooking parse(String msg) {
		MyBooking booking = new MyBooking();
		booking.agent = "Expedia";
		String line = null;
		String token[] = null;
		String field = null;

		msg = trimBody(msg);

		// Setting customer
		Customer cus = new Customer();

		line = findLine(msg, "Primary Redeemer:");
		token = split(line, ",");
		cus.setPhoneNumbers(setPhone(token[1]));
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
		String date = null;
		try {
			date = MailConfig.SHORTDATE.format(MailConfig.MAIL_DATE.parse(field));
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
		String productId = ProductTools.getInstance().getProductId(product);

		if (productId != null) {
			booking.setProductId(productId);

			TypeEnum prodType = ProductTools.getInstance().getProductType(product);
			if (prodType == TypeEnum.FIXED) {
				String eventId = new EventTools().getEventId(productId, date, time);
				booking.setEventId(eventId);
			} else {
				try {
					Date startDate = MailConfig.DEFAULT_DATE.parse(date + " " + time);
					booking.setStartTime(startDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		booking.setInitialPayments(null);
		booking.setCouponCodes(null);
		booking.setOptions(null);
		booking.setResources(null);

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
			number.setPeopleCategoryId(getCustomerType(t[0]));
			peopleList.add(number);
		}
		participants.setNumbers(peopleList);
		participants.setDetails(null);
		booking.setParticipants(participants);
		booking.setBookingNumber(null);

		line = findLine(msg, "Voucher #:");
		token=split(line, "Itin");
		booking.voucherNumber = token[0];
		booking.setTitle(booking.agent + "-" +  booking.voucherNumber);
		
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

		ExpediaParser parser = new ExpediaParser();
		Booking booking = parser.parse(msg);
		// parser.dump(booking);
		try {
			CreateBooking.postBooking(booking);
		} catch (ApiException e) {
			Logger.err(e.getMessage());
		}

	}
}
