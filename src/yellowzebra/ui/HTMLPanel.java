package yellowzebra.ui;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.swing.JTextPane;

import yellowzebra.util.Logger;

public class HTMLPanel extends JTextPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1702853075565622156L;

	public HTMLPanel() {
		super();
		setEditable(false);
	}

	public void setContent(Message message) {
		this.removeAll();

		try {
			if (message.getContentType().contains("multipart")) {
				Multipart multipart = (Multipart) message.getContent();
				this.setContent(multipart);
			} else if (message.getContentType().contains("TEXT/PLAIN")) {
				setContentType("text/plain");
				setText(message.getContent().toString());
			} else if (message.getContentType().contains("TEXT/HTML")) {
				setContentType("text/html");
				setText(message.getContent().toString());
			} else {
				setContentType(message.getContentType());
				setText(message.getContent().toString());
			}

			this.validate();
		} catch (MessagingException e) {
			Logger.exception(e);
		} catch (IOException e) {
			Logger.exception(e);
		}
	}

	private void setContent(Multipart multipart) throws MessagingException, IOException {
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			String disposition = bodyPart.getDisposition();
			if (disposition != null
					&& (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {

			} else {
				if (bodyPart.getContentType().contains("TEXT/HTML")) {
					setContentType("text/html");
				} else if (bodyPart.getContentType().contains("TEXT/PLAIN")) {
					setContentType("text/plain");
				} else {
					setContentType(bodyPart.getContentType());
				}

				setText(bodyPart.getContent().toString());
			}
		}
	}
}
