package yellowzebra.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import io.swagger.client.ApiException;
import io.swagger.client.model.PeopleNumber;
import io.swagger.client.model.PhoneNumber;
import yellowzebra.booking.CreateBooking;
import yellowzebra.parser.AParser;
import yellowzebra.util.MailConfig;
import yellowzebra.util.MyBooking;

public class ParserController implements Runnable {
	private static DefaultTableModel model = null;
	private static SpringPanel panel = null;
	private static boolean isPaused = true;
	private static MyBooking booking = null;

	public ParserController(DefaultTableModel model, SpringPanel panel) {
		ParserController.model = model;
		ParserController.panel = panel;
	}

	public static synchronized void refreshMailList() {
		model.setRowCount(0);

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

				model.addRow(new Object[] { from, subject, date, parser, msg });
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (p != null) {
			booking = p.parse(subject, msg);
			booking2Component(booking);
		}
	}

	private static MyBooking component2Booking() {
		MyBooking finalBooking = booking;

		finalBooking.getCustomer()
				.setFirstName(booking.getCustomer().getLastName() + " " + booking.getCustomer().getLastName());
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

		// agent
		str = booking.agent;
		lbl = new JLabel("Tour Agent");
		txt = new JTextField(str);
		panel.addRow(lbl, txt);

		// voucher number
		str = booking.voucherNumber;
		lbl = new JLabel("Voucher Number(s)");
		txt = new JTextField(str);
		panel.addRow(lbl, txt);

		// booking info
		str = booking.getProductName();
		lbl = new JLabel("Tour Name");
		txt = new JTextField(str);
		panel.addRow(lbl, txt);

		// booking date
		str = MailConfig.DEFAULT_DATE.format(booking.getStartTime());
		lbl = new JLabel("Booking Time");
		txt = new JTextField(str);
		panel.addRow(lbl, txt);

		// number of person
		lbl = new JLabel("Participant Information");
		panel.addRow(lbl, null);

		for (PeopleNumber n : booking.getParticipants().getNumbers()) {
			lbl = new JLabel(n.getPeopleCategoryId().toString().substring(1));
			txt = new JTextField(n.getNumber().toString());
			panel.addRow(lbl, txt);
		}

		lbl = new JLabel("Customer Information");
		panel.addRow(lbl, null);

		// customer name
		lbl = new JLabel("Name");
		txt = new JTextField(booking.getCustomer().getFirstName());
		panel.addRow(lbl, txt);

		// customer lastname
		lbl = new JLabel("Last Name");
		txt = new JTextField(booking.getCustomer().getLastName());
		panel.addRow(lbl, txt);

		// customer e-mail
		lbl = new JLabel("E-Mail");
		txt = new JTextField(booking.getCustomer().getEmailAddress());
		panel.addRow(lbl, txt);

		// customer phone
		lbl = new JLabel("Phone Number");

		List<PhoneNumber> list = booking.getCustomer().getPhoneNumbers();
		if (list != null) {
			txt = new JTextField(list.get(0).getNumber());
		}
		panel.addRow(lbl, txt);

		// details
		str = booking.details;
		if (str != null && str.length() > 0) {
			lbl = new JLabel("Details/Notes");
			JTextArea txa = new JTextArea(str);
			txa.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
			panel.addRow(lbl, txa);
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
