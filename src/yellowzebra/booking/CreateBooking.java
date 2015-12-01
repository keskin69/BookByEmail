package yellowzebra.booking;

import java.util.ArrayList;

import io.swagger.client.ApiException;
import io.swagger.client.api.BookingsApi;
import io.swagger.client.api.CustomersApi;
import io.swagger.client.model.Booking;
import io.swagger.client.model.Customer;
import io.swagger.client.model.Participants;
import io.swagger.client.model.PeopleNumber;

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

	private static Booking createBooking(Customer customer, String product, String date, String time) {
		Booking booking = new Booking();

		String productId = ProductTools.getInstance().getProductId(product);
		String eventId = new EventTools().getEventId(productId, date, time);

		booking.setProductId(productId);
		booking.setEventId(eventId);

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
		Customer testCustomer = CreateBooking.createCustomer("Custo2", "Santa2", "santa@gmail.com");
		// test.postCustomer(testCustomer);
		try {
			CreateBooking.postBooking(CreateBooking.createBooking(testCustomer, "Tour", "2015-12-02", "09:00"));
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}