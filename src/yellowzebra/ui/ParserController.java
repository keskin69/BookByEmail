package yellowzebra.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.swing.JLabel;

import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;

import io.swagger.client.ApiException;
import io.swagger.client.model.Booking;
import io.swagger.client.model.PeopleNumber;
import io.swagger.client.model.PhoneNumber;
import yellowzebra.booking.CreateBooking;
import yellowzebra.parser.AParser;
import yellowzebra.util.MailConfig;
import yellowzebra.util.ParserUtils;

public class ParserController implements Runnable {
	private static DefaultTableModel model = null;
	private static SpringPanel panel = null;
	private static boolean isPaused = false;

	public ParserController(DefaultTableModel model, SpringPanel panel) {
		ParserController.model = model;
		ParserController.panel = panel;
	}

	public static synchronized  void refreshMailList() {
		model.setRowCount(0);

		ArrayList<Entry<String, Message>> list = null;
		try {
			list = MailReader.getInstance().getMailList();
			for (Entry<String, Message> e : list) {
				String parser = e.getKey();
				Message msg = (Message) e.getValue();

				String from = null;
				for (Address a : msg.getFrom()) {
					from = ((InternetAddress) a).getAddress();
					break;
				}
				String subject = msg.getSubject();
				// TODO
				// String content = msg.getContent().toString();
				String content = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\expedia.txt");
				String date = MailConfig.DEFAULT_DATE.format(msg.getReceivedDate()).toString();

				model.addRow(new Object[] { from, subject, date, parser, content });
			}
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static synchronized  void postBooking() {
		Booking booking = component2Booking();
		try {
			CreateBooking.postBooking(booking);
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static synchronized void fillContent(String msg, String parser) {
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
			Booking booking = p.parse(msg);
			booking2Component(booking);
		}
	}

	private static Booking component2Booking() {
		return null;
	}

	public static void isPaused(boolean p) {
		isPaused = p;
	}

	// Create the Swing components regarding the booking content
	private static void booking2Component(Booking booking) {
		panel.reset();
		
		JTextField txt = null;
		String str = null;
		JLabel lbl = null;

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
		lbl = new JLabel("Participant Information:");
		panel.addRow(lbl, null);

		for (PeopleNumber n : booking.getParticipants().getNumbers()) {
			lbl = new JLabel(n.getPeopleCategoryId().toString().substring(1));
			txt = new JTextField(n.getNumber().toString());
			panel.addRow(lbl, txt);
		}

		lbl = new JLabel("Customer Information:");
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

		panel.revalidate();
		panel.getTopLevelAncestor().validate();
	}

	public void run() {
		// refresh the mail list in every minute once
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
