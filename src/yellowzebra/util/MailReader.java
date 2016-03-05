package yellowzebra.util;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;

import com.sun.mail.gimap.GmailFolder;
import com.sun.mail.gimap.GmailMessage;
import com.sun.mail.gimap.GmailStore;

public class MailReader {
	private static MailReader instance = null;
	private static GmailStore store = null;
	private static ConfigReader props = null;
	private static String inFolder = null;
	private static GmailFolder inbox = null;

	public static MailReader getInstance() {
		if (instance == null) {
			instance = new MailReader();
			inFolder = ConfigReader.getInstance().getProperty("inbox");
		}

		return instance;
	}

	private MailReader() {
		props = ConfigReader.getInstance();
		Session session = Session.getDefaultInstance(props, null);

		try {
			store = (GmailStore) session.getStore("gimap");
			connect();
		} catch (NoSuchProviderException e) {
			Logger.err("Cannot connect to gmail");
		}
	}

	private static void connect() {
		String host = props.getProperty("mail.smtp.host");
		String user = props.getProperty("user");

		try {
			// 1) make gmail less secure
			// https://www.google.com/settings/security/lesssecureapps
			// 2) make imap enabled
			store.connect(host, user, props.getProperty("password"));

			Logger.log("Connected to e-mail server");
		} catch (NoSuchProviderException e) {
			Logger.err("Cannot access e-mail server");
		} catch (MessagingException e) {
			e.printStackTrace();
			Logger.err("Cannot authanticate " + user);
		}
	}

	public ArrayList<Entry<String, Message>> getMailList() throws MessagingException {
		ArrayList<Entry<String, Message>> list = new ArrayList<Entry<String, Message>>();

		if (!store.isConnected()) {
			connect();
		}

		if (store.isConnected()) {
			inbox = (GmailFolder) store.getFolder(inFolder);
			inbox.open(Folder.READ_WRITE);
			int number = ConfigReader.getInstance().getInt("number_of_mail");

			String from = null;
			int n = inbox.getMessageCount();
			if (number > n) {
				number = n - 1;
			}

			Message[] messages = inbox.getMessages(n - number, n);
			Logger.log("Scanning " + number + " recent e-mails out of " + (n - 1));
			for (Message message : messages) {
				for (Address a : message.getFrom()) {
					from = ((InternetAddress) a).getAddress();
					break;
				}

				boolean skip = false;
				GmailMessage gmsg = (GmailMessage) message;
				String lbl[] = gmsg.getLabels();
				for (String str : lbl) {
					// don't display mails labeled as "Follow up"
					if (str.equals("Follow up")) {
						skip = true;
					}

					if (str.equals("In")) {
						skip = true;
					}
					
					if (str.equals("IN")) {
						skip = true;
					}
				}

				if (message.getSubject() == null) {
					skip = true;
				}

				if (!skip) {
					String parser = ParserFactory.getInstance().isParsable(message.getSubject(), from);
					if (parser != null) {
						list.add(new SimpleEntry<String, Message>(parser, message));
					}
				}
			}
			
			Logger.log(list.size() + " booking mail found");
		}

		return list;
	}

	public static void addLabel(Message msg, String label) throws Exception {
		msg.setFlag(Flags.Flag.SEEN, true);
		inbox.setLabels(new Message[] { msg }, new String[] { label }, true);
	}

	public static void moveMail(Message msg, String destFolder) throws Exception {
		if (destFolder != null) {
			if (!store.isConnected()) {
				connect();
			}

			if (store.isConnected()) {
				Folder inbox = store.getFolder(inFolder);
				inbox.open(Folder.READ_WRITE);

				Folder dest = store.getFolder(destFolder);
				dest.open(Folder.READ_WRITE);

				Message[] msgArray = new Message[] { msg };

				inbox.copyMessages(msgArray, dest);
				msg.setFlag(Flags.Flag.DELETED, true);

				inbox.expunge();
				dest.close(true);
			}
		}
	}
}
