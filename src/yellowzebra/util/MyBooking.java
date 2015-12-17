package yellowzebra.util;

import io.swagger.client.model.Booking;
import io.swagger.client.model.Customer;

public class MyBooking extends Booking {
	public String voucherNumber = null;
	public String details = null;
	public String agent = null;

	public void dump() {
		System.out.println("Agent:" + agent + "-" + voucherNumber);
		System.out.println("Product: " + getProductName());
		System.out.println("ProductId: " + getProductId());

		if (getEventId() != null) {
			System.out.println("Event Id: " + getEventId());
		}

		System.out.println("Start Time: " + getStartTime().toString());
		System.out.println("Participant: " + getParticipants().getNumbers().size());
		Customer customer = getCustomer();
		System.out.println("Customer:\n" + customer.getFirstName() + " " + customer.getLastName());
		System.out.println(customer.getEmailAddress() + "\t" + customer.getPhoneNumbers().get(0).getNumber());

		System.out.println("Details:\n" + details);
	}
}
