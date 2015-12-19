package yellowzebra.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import io.swagger.client.ApiException;
import io.swagger.client.model.PeopleNumber;
import io.swagger.client.model.PhoneNumber;
import yellowzebra.booking.CreateBooking;
import yellowzebra.parser.AParser;
import yellowzebra.util.Logger;
import yellowzebra.util.MailConfig;
import yellowzebra.util.MyBooking;

public class ParserController implements Runnable {
	private static MailTable tbl = null;
	private static SpringPanel panel = null;
	private static boolean isPaused = true;
	private static MyBooking booking = null;

	public ParserController(MailTable tbl, SpringPanel panel) {
		ParserController.tbl = tbl;
		ParserController.panel = panel;
	}

	public static synchronized void refreshMailList() {
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

	public static synchronized void postBooking() throws ApiException {
		MyBooking booking = component2Booking();
		CreateBooking.postBooking(booking);
	}

	public static synchronized void fillContent(String subject, String msg, String parser) {
		Class<?> c;
		AParser p = null;
		try {
			c = Class.forName(parser);
			p = (AParser) c.newInstance();

		} catch (ClassNotFoundException e) {
			Logger.exception(e);
		} catch (InstantiationException e) {
			Logger.exception(e);
		} catch (IllegalAccessException e) {
			Logger.exception(e);
		}

		if (p != null) {
			booking = p.parse(subject, msg);
			booking2Component(booking);
		}
	}

	private static MyBooking component2Booking() {
		MyBooking finalBooking = booking;

		finalBooking.getCustomer()
				.setFirstName(booking.getCustomer().getFirstName() + " " + booking.getCustomer().getLastName());
		finalBooking.getCustomer().setLastName(booking.agent + "-" + booking.voucherNumber);

		return finalBooking;
	}

	public static void isPaused(boolean p) {
		isPaused = p;
	}

	// Create the Swing components regarding the booking content
	private static void booking2Component(MyBooking booking) {
		panel.reset();

		JTextField txt = null;
		String str = null;
		JLabel lbl = null;

		panel.addRow("Title", booking.getTitle());
		panel.addRow("Tour Agent", booking.agent);
		panel.addRow("Voucher Number(s)", booking.voucherNumber);
		panel.addRow("Tour Name", booking.getProductName());
		panel.addRow("Booking Time", booking.startTimeNote);
		panel.addRow("Participant Information", null);

		for (PeopleNumber n : booking.getParticipants().getNumbers()) {
			lbl = new JLabel(n.getPeopleCategoryId().toString().substring(1));
			txt = new JTextField(n.getNumber().toString());
			panel.addRow(lbl, txt);
		}

		panel.addRow("Customer Information", null);
		panel.addRow("Name", booking.getCustomer().getFirstName());
		panel.addRow("Last Name", booking.getCustomer().getLastName());

		if (booking.getCustomer().getStreetAddress() != null) {
			lbl = new JLabel("Address");
			JTextArea txa = new JTextArea(booking.getCustomer().getStreetAddress().getAddress1());
			panel.addRow(lbl, txa);
		}

		panel.addRow("E-Mail", booking.getCustomer().getEmailAddress());

		List<PhoneNumber> list = booking.getCustomer().getPhoneNumbers();
		if (list != null) {
			panel.addRow("Phone Number", list.get(0).getNumber());
		}

		// details
		str = booking.details;
		if (str != null) {
			str = str.replaceAll(String.valueOf((char) 160), " ").trim();
			if (str.length() > 0) {
				lbl = new JLabel("Details/Notes");
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
