package yellowzebra.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.swing.JTextPane;

import yellowzebra.util.Logger;
import yellowzebra.util.ParserUtils;

public class HTMLPanel extends JTextPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1702853075565622156L;

	public HTMLPanel() {
		super();
		setEditable(false);

		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() > 1) {
					openDialog();
				}
			}
		});
	}

	private void openDialog() {
		String txt = null;

		if (getContentType().equals("text/html")) {
			txt = ParserUtils.html2Text(getText());
		} else {
			txt = getText();
		}

		new ContentDialog(txt);
	}

	public void setContent(Message message) {
		try {
			if (message.getContentType().contains("multipart")) {
				Multipart multipart = (Multipart) message.getContent();
				setContent(multipart);
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

		} catch (MessagingException e) {
			Logger.exception(e);
		} catch (IOException e) {
			Logger.exception(e);
		}
	}

	private void setContent(Multipart multipart) throws MessagingException, IOException {
		String str = "";
		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			String disposition = bodyPart.getDisposition();
			if (disposition != null
					&& (disposition.equalsIgnoreCase(Part.ATTACHMENT) || disposition.equalsIgnoreCase(Part.INLINE))) {
				continue;
			} else {
				if (bodyPart.getContentType().contains("TEXT/HTML")) {
					setContentType("text/html");
					// setText(bodyPart.getContent().toString());
					str += bodyPart.getContent();
				}
			}
		}

		setText("<HTML>" + str + "</HTML>");
	}
}
