package yellowzebra.util;

import io.swagger.client.model.Booking;
import io.swagger.client.model.Customer;

public class MyBooking extends Booking {
	public String voucherNumber = null;
	public String details = null;
	public String agent = null;
	public String pickup = null;

	public void dump() {
		System.out.println("Agent:" + agent + "-" + voucherNumber);
		System.out.println("Product: " + getProductName());
		System.out.println("ProductId: " + getProductId());

		if (getEventId() != null) {
			System.out.println("Event Id: " + getEventId());
		}

		try {
			System.out.println("Start Time: " + getStartTime().toString());
		} catch (NullPointerException ex) {

		}

		System.out.println("Participant: " + getParticipants().getNumbers().size());
		Customer customer = getCustomer();
		System.out.println("Customer:\n" + customer.getFirstName() + " " + customer.getLastName());
		System.out.println("E-mail: " + customer.getEmailAddress());

		if (customer.getPhoneNumbers() != null) {
			try {
				System.out.println("Phone:" + customer.getPhoneNumbers().get(0).getNumber());
			} catch (IndexOutOfBoundsException ex) {

			}
		}

		System.out.println("Details:\n" + details);
	}
}
