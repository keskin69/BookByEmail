package yellowzebra.booking;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.api.BookingsApi;
import io.swagger.client.api.CustomersApi;
import io.swagger.client.model.Booking;
import io.swagger.client.model.Customer;
import io.swagger.client.model.Participants;
import io.swagger.client.model.PeopleNumber;
import io.swagger.client.model.Product.TypeEnum;
import yellowzebra.util.Logger;
import yellowzebra.util.MailConfig;

public class CreateBooking {

	private static Customer createCustomer(String name, String lastName, String eMail) {
		Customer customer = new Customer();
		customer.setFirstName(name);
		customer.setLastName(lastName);
		customer.setEmailAddress(eMail);
		customer.setCustomFields(null);
		customer.setPhoneNumbers(null);

		return customer;
	}

	private static Booking createBooking(Customer customer, String product, Date date, String time) {
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
				startDate = MailConfig.DEFAULT_DATE.parse(date + " " + time);
				booking.setStartTime(startDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

	public static void postCustomer(Customer newCustomer) {
		CustomersApi customerApi = new CustomersApi();
		try {
			// update customerApi String[] authNames = new String[] { "keyAuth",
			// "secretKey" };
			customerApi.customersPost(newCustomer);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void postBooking(Booking newBooking) throws ApiException {
		BookingsApi bookingApi = new BookingsApi();

		// update bookingApi String[] authNames = new String[] { "keyAuth",
		// "secretKey" };
		bookingApi.bookingsPost(newBooking, "", false, false, false, false);

	}

	public static void main(String[] args) {
		Customer testCustomer = CreateBooking.createCustomer("Custo5", "Santa5", "santa@gmail.com");
		// test.postCustomer(testCustomer);
		try {
			Booking booking = null;

			// booking = CreateBooking.createBooking(testCustomer, "Tour",
			// "2015-12-12", "09:00");
			Date date = MailConfig.SHORTDATE.parse("2015-12-22");
			booking = CreateBooking.createBooking(testCustomer, "Dinner Cruise with Live Music", date, "19:00");
			CreateBooking.postBooking(booking);

		} catch (ApiException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			if (e.getCode() == 201) {
				Logger.log(e.getMessage());
			} else {
				Logger.err(e.getMessage());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Posting done...");
	}
}