package yellowzebra.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import io.swagger.client.model.Booking;
import io.swagger.client.model.PeopleNumber;
import io.swagger.client.model.PhoneNumber;
import yellowzebra.booking.ProductTools;
import yellowzebra.parser.AParser;
import yellowzebra.util.BookingException;
import yellowzebra.util.Logger;
import yellowzebra.util.MailConfig;
import yellowzebra.util.MyBooking;
import yellowzebra.util.ParserFactory;

public class ParserController implements Runnable {
	private static MailTable tbl = null;
	private static SpringPanel panel = null;
	private static boolean isPaused = true;
	private static MyBooking myBooking = null;
	private static String currentFolder = null;

	public ParserController(MailTable tbl, SpringPanel panel) {
		ParserController.tbl = tbl;
		ParserController.panel = panel;
	}

	public static void refreshMailList() {
		tbl.clearTable();

		ArrayList<Entry<String, Message>> list = null;
		try {
			list = MailReader.getInstance().getMailList();
			for (Entry<String, Message> e : list) {
				String parser = e.getKey();
				Message msg = (Message) e.getValue();

				String token[] = parser.split("\\.");
				String from = token[token.length - 1];
				String subject = msg.getSubject();
				String date = MailConfig.DEFAULT_DATE.format(msg.getReceivedDate()).toString();

				tbl.addRow(new Object[] { from, subject, date, parser, msg });
			}
		} catch (MessagingException e) {
			Logger.exception(e);
		}
	}

	public static Booking getBooking() throws BookingException {
		Booking booking = null;

		if (myBooking != null) {
			// first set the date/time since it maybe changed
			try {
				String dateTime = panel.getValue("Booking Time");
				String token[] = dateTime.split(" ");
				myBooking.tourTime = token[1];
				myBooking.tourDate = MailConfig.SHORTDATE.parse(token[0]);
			} catch (Exception e) {
				throw new BookingException("Please check \"Booking Time\" it should be in yyyy-mm-dd hh:mm format");
			}
			
			// set the product name because it my be changed
			myBooking.booking.setProductName(panel.getProductName());
			
			booking = myBooking.getBooking();
		}

		return booking;
	}

	public static void fillContent(String subject, String msg, String parserName) throws Exception {
		AParser parser = ParserFactory.getInstance().getParser(parserName);
		if (parser != null) {
			currentFolder = parser.folder;
			myBooking = parser.parse(subject, msg);
			booking2Component(myBooking);
		}
	}

	public static void moveMail() {
		Message msg = (Message) tbl.getColumn(4);
		try {
			MailReader.moveMail(msg, currentFolder);
			tbl.removeSelected();
		} catch (Exception e1) {
			Logger.err("Cannot move e-mail to another folder");
		}
	}

	public static void isPaused(boolean p) {
		isPaused = p;
	}

	// Create the Swing components regarding the booking content inside the
	// e-mail message
	private static void booking2Component(MyBooking mybooking) {
		panel.reset();

		JTextField txt = null;
		String str = "";
		JLabel lbl = null;

		panel.addRow("Title", mybooking.booking.getTitle());
		panel.addRow("Tour Agent", mybooking.agent);
		panel.addRow("Voucher Number(s)", mybooking.voucherNumber);
		panel.addCombo("Tour Name", mybooking.booking.getProductName(), ProductTools.getInstance().getProducts());
		panel.addRow("Booking Time", mybooking.getTourDateTime());
		panel.addRow("Participant Information", null);

		for (PeopleNumber n : mybooking.booking.getParticipants().getNumbers()) {
			lbl = new JLabel(n.getPeopleCategoryId().toString().substring(1));
			txt = new JTextField(n.getNumber().toString());
			panel.addRow(lbl, txt);
		}

		panel.addRow("Customer Name", mybooking.booking.getCustomer().getFirstName());
		panel.addRow("Last Name", mybooking.booking.getCustomer().getLastName());

		if (mybooking.booking.getCustomer().getStreetAddress() != null) {
			lbl = new JLabel("Address");
			JTextArea txa = new JTextArea(mybooking.booking.getCustomer().getStreetAddress().getAddress1());
			panel.addRow(lbl, txa);
		}

		panel.addRow("E-Mail", mybooking.booking.getCustomer().getEmailAddress());

		List<PhoneNumber> list = mybooking.booking.getCustomer().getPhoneNumbers();
		if (list != null) {
			if (list.size() > 0) {
				panel.addRow("Phone Number", list.get(0).getNumber());
			}
		}

		// details
		if (mybooking.pickup != null) {
			if (!myBooking.pickup.equals("")) {
				mybooking.pickup = "Pickup: " + mybooking.pickup.replaceAll(String.valueOf((char) 160), " ").trim();
				str = mybooking.pickup + "\n";
			}
		}

		if (mybooking.details != null) {
			if (!mybooking.details.equals("")) {
				mybooking.details = "Details: " + mybooking.details.replaceAll(String.valueOf((char) 160), " ").trim();
				str += mybooking.details;
			}
		}

		if (str != null) {
			if (str.length() > 0) {
				lbl = new JLabel("Details/Notes");

				if (str.endsWith("\n")) {
					str = str.substring(0, str.length() - 1);
				}

				JTextArea txa = new JTextArea(str);
				panel.addRow(lbl, txa);
			}
		}

		panel.setLayout();
	}

	public void run() {
		// refresh the mail list in every minute once
		while (true) {
			try {
				if (!isPaused) {
					refreshMailList();
				}
				Thread.sleep(1 * 60 * 1000);
			} catch (InterruptedException e) {
				Logger.exception(e);
			}
		}
	}

}
