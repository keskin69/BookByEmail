package yellowzebra.ui;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import yellowzebra.parser.AParser;
import yellowzebra.util.ClassFinder;
import yellowzebra.util.ConfigReader;
import yellowzebra.util.Logger;

public class MailReader {
	private static MailReader instance = null;
	private static Store store = null;
	private static ConfigReader props = null;
	private static final List<Class<?>> classes = ClassFinder.find("yellowzebra.parser");
	private static String destFolder = null;

	public static MailReader getInstance() {
		if (instance == null) {
			instance = new MailReader();
		}

		return instance;
	}

	private MailReader() {
		props = ConfigReader.getInstance();
		destFolder = (String) ConfigReader.getInstance().get("processed.folder");
		Session session = Session.getDefaultInstance(props, null);

		try {
			store = session.getStore("imaps");
		} catch (NoSuchProviderException e) {
			Logger.err("Cannot create IMAP store");
		}

		connect();
	}

	private static void connect() {
		try {
			// 1) make gmail less secure
			// https://www.google.com/settings/security/lesssecureapps
			// 2) make imap enabled
			store.connect(props.getProperty("mail.smtp.host"), props.getProperty("user"),
					props.getProperty("password"));

			Logger.log("Connected to e-mail server");
		} catch (NoSuchProviderException e) {
			Logger.err("Cannot access e-mail server");
		} catch (MessagingException e) {
			Logger.err(e.getMessage(), "Cannot read smtp.properties file");
		}
	}

	public ArrayList<Entry<String, Message>> getMailList() throws MessagingException {
		ArrayList<Entry<String, Message>> list = new ArrayList<Entry<String, Message>>();

		if (!store.isConnected()) {
			connect();
		}

		if (store.isConnected()) {
			Logger.log("Retriving e-mails");
			Folder inbox = store.getFolder("Inbox");
			inbox.open(Folder.READ_WRITE);

			Message[] messages = inbox.getMessages();
			for (Message message : messages) {
				String from = null;
				for (Address a : message.getFrom()) {
					from = ((InternetAddress) a).getAddress();
					break;
				}

				String parser = canParse(message.getSubject(), from);
				if (parser != null) {
					list.add(new SimpleEntry<String, Message>(parser, message));
				}
			}
		}

		return list;

	}

	private static String canParse(String subject, String from) {
		for (Class<?> c : classes) {
			if (c.getSuperclass() != null) {
				if (c.getSuperclass().getName().endsWith("AParser")) {
					try {
						AParser parser = (AParser) c.newInstance();
						if (parser.isApplicable(subject, from)) {
							Logger.log("Parsing message with " + c.getName());
							return c.getCanonicalName();
						}
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return null;
	}

	public static void moveMail(Message msg) throws Exception {
		if (!store.isConnected()) {
			connect();
		}

		if (store.isConnected()) {
			Folder inbox = store.getFolder("Inbox");
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
