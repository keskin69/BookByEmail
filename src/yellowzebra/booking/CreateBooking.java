package yellowzebra.booking;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.JSON;
import io.swagger.client.api.BookingsApi;
import io.swagger.client.api.CustomersApi;
import io.swagger.client.model.Booking;
import io.swagger.client.model.Customer;
import io.swagger.client.model.Participants;
import io.swagger.client.model.PeopleNumber;
import io.swagger.client.model.Product.TypeEnum;
import yellowzebra.util.ConfigReader;
import yellowzebra.util.Logger;
import yellowzebra.util.MailConfig;

public class CreateBooking {

	private static Booking testBooking(Customer customer, String product, Date date, String time) {
		Booking booking = new Booking();

		String productId = ProductTools.getInstance().getProductId(product);
		TypeEnum prodType = ProductTools.getInstance().getProductType(product);
		booking.setProductId(productId);

		if (prodType == TypeEnum.FIXED) {
			String eventId = new EventTools().getEventId(productId, date, time);
			booking.setEventId(eventId);
		} else {
			Date startDate;
			try {
				startDate = MailConfig.DEFAULT_DATE.parse(MailConfig.SHORTDATE.format(date) + " " + time);
				booking.setStartTime(startDate);
			} catch (ParseException e) {
				Logger.exception(e);
			}
		}

		booking.setCustomer(customer);
		booking.setInitialPayments(null);
		booking.setCouponCodes(null);
		booking.setOptions(null);
		booking.setResources(null);

		Participants participants = new Participants();
		ArrayList<PeopleNumber> peopleList = new ArrayList<PeopleNumber>();
		PeopleNumber number = new PeopleNumber();
		number.setNumber(1);
		number.setPeopleCategoryId("Cadults");
		peopleList.add(number);
		participants.setNumbers(peopleList);
		participants.setDetails(null);
		booking.setParticipants(participants);
		booking.setBookingNumber(null);

		return booking;
	}

	public static void postBooking(Booking newBooking) throws ApiException {
		if (ConfigReader.getInstance().getProperty("post_enabled").toUpperCase().equals("YES")) {
			BookingsApi bookingApi = new BookingsApi();

			// update bookingApi String[] authNames = new String[] { "keyAuth",
			// "secretKey" };
			bookingApi.bookingsPost(newBooking, "", false, false, false, false);
		} else {
			System.out.println(new JSON().serialize(newBooking));
		}
	}

	public static Customer newCustomer(String name, String lastName, String eMail) {
		Customer customer = new Customer();
		customer.setFirstName(name);
		customer.setLastName(lastName);
		customer.setEmailAddress(eMail);
		customer.setPhoneNumbers(null);

		return customer;
	}

	public static void postCustomer(Customer newCustomer) {
		CustomersApi customerApi = new CustomersApi();
		try {
			// update customerApi String[] authNames = new String[] { "keyAuth",
			// "secretKey" };
			customerApi.customersPost(newCustomer);
		} catch (ApiException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Logger.init();

		// init Bookeo API
		ConfigReader.init("config.properties");
		String apiKey = ConfigReader.getInstance().getProperty("api_key");
		String secretKey = ConfigReader.getInstance().getProperty("secret_key");
		Configuration.setKey(apiKey, secretKey);

		Customer testCustomer = newCustomer("Custo5", "Santa5", "santa@gmail.com");
		// test.postCustomer(testCustomer);
		try {
			Booking booking = null;

			// booking = CreateBooking.createBooking(testCustomer, "Tour",
			// "2015-12-12", "09:00");
			Date date = MailConfig.SHORTDATE.parse("2015-12-22");
			booking = CreateBooking.testBooking(testCustomer, "Dinner Cruise with Live Music", date, "19:00");
			CreateBooking.postBooking(booking);

		} catch (ApiException e) {
			if (e.getCode() == 201) {
				Logger.log(e.getMessage());
			} else {
				Logger.err(e.getMessage());
			}
		} catch (ParseException e) {
			Logger.exception(e);
		}

		System.out.println("Posting done...");
	}
}