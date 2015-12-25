package yellowzebra.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.swing.JFrame;
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
		this.setPreferredSize(new Dimension(200,120));
		
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

		ContentDialog.getInstance().setContent(txt);
	}

	public void setContent(Message message) {
		this.removeAll();
		
		try {
			if (message.getContentType().contains("multipart")) {
				Multipart multipart = (Multipart) message.getContent();
				setContent(multipart);
			} else if (message.getContentType().contains("TEXT/PLAIN")) {
				setContentType("text/plain");
				setText(message.getContent().toString());
			} else if (message.getContentType().contains("TEXT/HTML")) {
				setContentType("text/html");
				setText("<HTML>" + message.getContent().toString() + "</HTML>");
			} else {
				setContentType(message.getContentType());
				setText(message.getContent().toString());
			}

		} catch (MessagingException e) {
			Logger.exception(e);
		} catch (IOException e) {
			Logger.exception(e);
		}
		
		validate();
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
					str += bodyPart.getContent();
				}
			}
		}

		setText("<HTML>" + str + "</HTML>");
	}

	public static void main(String[] args) {
		try {
			String str = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\test.html");
			JFrame frm = new JFrame();
			frm.setLayout(new BorderLayout());
			frm.setSize(500, 500);
			HTMLPanel htmlView = new HTMLPanel();
			frm.add(htmlView);
			htmlView.setContentType("text/html");
			htmlView.setText(str);

			frm.setVisible(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
