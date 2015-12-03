package yellowzebra.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import io.swagger.client.model.Booking;
import yellowzebra.mail.AParser;
import yellowzebra.mail.MailReader;
import yellowzebra.util.Config;

public class ParserController implements Runnable {
	private static DefaultTableModel model = null;
	private static JPanel panel = null;

	public ParserController(DefaultTableModel model, JPanel panel) {
		ParserController.model = model;
		ParserController.panel = panel;
	}

	public static void refreshMailList() {
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
				String content = msg.getContent().toString();
				String date = Config.DEFAULT_DATE.format(msg.getReceivedDate()).toString();
				
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

	public static void postBooking() {
		Booking booking = component2Booking();
	}

	public static void fillContent(String msg, String parser) {
		Booking booking = null;

		Class<?> c;
		try {
			c = Class.forName(parser);
			AParser p = (AParser) c.newInstance();
			booking = p.parse(msg);
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

		if (booking != null) {
			booking2Component(booking);
		}
	}

	private static Booking component2Booking() {
		return null;
	}

	// Create the Swing components regarding the booking content
	private static void booking2Component(Booking booking) {
		panel.removeAll();

		TitledBorder title;
		title = BorderFactory.createTitledBorder(booking.getBookingNumber());
		panel.setBorder(title);

		JLabel lblNewLabel = new JLabel("New label");
		panel.add(lblNewLabel);
		JTextField textField = new JTextField();
		panel.add(textField);

		panel.repaint();
	}

	public void run() {
		// refresh the mail list in every minute once
		try {
			Thread.sleep(1 * 60 * 1000);
			refreshMailList();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
