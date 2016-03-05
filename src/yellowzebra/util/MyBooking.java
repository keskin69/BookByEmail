package yellowzebra.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.swagger.client.model.Booking;
import io.swagger.client.model.CustomField;
import io.swagger.client.model.Customer;
import io.swagger.client.model.PeopleNumber;
import io.swagger.client.model.Product;
import yellowzebra.booking.EventTools;
import yellowzebra.booking.ProductTools;

public class MyBooking {
	public String voucherNumber = null;
	public String details = null;
	public String agent = null;
	public String shortAgentName = null;
	public String pickup = null;
	public Date tourDate = null;
	public String tourTime = null;
	public Booking booking = null;
	public String originalContent = null;

	public MyBooking() {
		booking = new Booking();
		booking.setInitialPayments(null);
		booking.setCouponCodes(null);
		booking.setOptions(null);
		booking.setResources(null);
		booking.setBookingNumber(null);
	}

	// convert mybooking object to standard booking object
	public Booking getBooking() throws BookingException {
		setProduct();

		// first last - AgentShort
		booking.getCustomer().setFirstName(booking.getCustomer().getFirstName() + " "
				+ booking.getCustomer().getLastName() + " - " + shortAgentName);

		String vStr = "";
		try {
			String v[] = voucherNumber.split(",");
			vStr = v[0];
		} catch (Exception ex) {
			vStr = voucherNumber;
		}

		booking.getCustomer().setLastName(vStr);

		// Set customly created field for storing original text message
		if (ConfigReader.getInstance().getProperty("storeOriginal").toUpperCase().equals("YES")) {
			ArrayList<CustomField> customFields = new ArrayList<CustomField>();
			CustomField origMsg = new CustomField();
			origMsg.setName("Original Booking");
			origMsg.setValue(originalContent);
			customFields.add(origMsg);
			booking.getCustomer().setCustomFields(customFields);
		}

		return booking;
	}

	private void setProduct() throws BookingException {
		String product = booking.getProductName();
		String productId = ProductTools.getInstance().getProductId(product);

		if (productId == null) {
			throw new BookingException("\"" + product + "\" cannot be found in available tour list");
		} else {
			booking.setProductId(productId);
			Product.TypeEnum prodType = ProductTools.getInstance().getProductType(product);

			if (prodType == Product.TypeEnum.FIXED) {
				String eventId = new EventTools().getEventId(productId, tourDate, tourTime);

				if (eventId == null) {
					throw new BookingException("\"" + product + "\" is not avaiable at " + getTourDateTime());
				}

				booking.setEventId(eventId);

			} else {
				try {
					booking.setStartTime(MailConfig.DEFAULT_DATE.parse(getTourDateTime()));
				} catch (ParseException e) {
					throw new BookingException("Date/Time not correct " + getTourDateTime());
				}
			}
		}

	}

	public String getTourDateTime() {
		return MailConfig.SHORTDATE.format(tourDate) + " " + tourTime;
	}

	public void dump() {
		System.out.println("Agent:" + agent + "-" + voucherNumber);
		System.out.println("Product: " + booking.getProductName());
		System.out.println("ProductId: " + booking.getProductId());

		if (booking.getEventId() != null) {
			System.out.println("Event Id: " + booking.getEventId());
		}

		System.out.println("Start Time: " + getTourDateTime());

		for (PeopleNumber num : booking.getParticipants().getNumbers()) {
			System.out.println("Participant: " + num.getPeopleCategoryId() + ":" + num.getNumber());
		}
		
		Customer customer = booking.getCustomer();
		System.out.println("Customer:\n" + customer.getFirstName() + " " + customer.getLastName());
		System.out.println("E-mail: " + customer.getEmailAddress());

		if (customer.getPhoneNumbers() != null) {
			try {
				System.out.println("Phone:" + customer.getPhoneNumbers().get(0).getNumber());
			} catch (IndexOutOfBoundsException ex) {

			}
		}

		System.out.println("Pickup:\n" + pickup);
		System.out.println("Details:\n" + details);
	}
}
