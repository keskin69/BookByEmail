package yellowzebra.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.alee.laf.WebLookAndFeel;

import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import yellowzebra.util.ConfigReader;
import yellowzebra.util.Logger;
import yellowzebra.util.ParserUtils;

public class ParserUI extends JFrame implements WindowStateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1022086231912876450L;
	private static JButton btnRefresh = null;
	private static MailTable tblMail = null;
	private static SpringPanel pnlContent = null;
	private static HTMLPanel txtMail;
	private static LinkLabel lblStatus = null;
	private static JLabel lblBooking = null;
	private static JButton btnPost = null;

	public void setIcon(String res) {
		BufferedImage myImg = null;
		InputStream imgStream = getClass().getResourceAsStream("/resources/" + res);
		try {
			myImg = ImageIO.read(imgStream);
		} catch (Exception e) {
			Logger.err("Cannot read image file " + res);
		}

		setIconImage(myImg);
	}

	public void postUI() {
		Logger.label = lblStatus;
		setIcon("blue_email.png");
		setVisible(true);

		// init controller thread
		ParserController controller = new ParserController(tblMail, pnlContent);
		Thread t = new Thread(controller);
		t.start();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		WebLookAndFeel.install();
		ConfigReader.init("config.properties");

		// init Bookeo API
		String apiKey = ConfigReader.getInstance().getProperty("api_key");
		String secretKey = ConfigReader.getInstance().getProperty("secret_key");
		Configuration.setKey(apiKey, secretKey);

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final ParserUI frame = new ParserUI();

					frame.postUI();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the frame.
	 */
	public ParserUI() {
		setTitle("Yellow Zebra Booking Tool V1.0");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 850, 650);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel pnlTop = new JPanel();
		contentPane.add(pnlTop, BorderLayout.CENTER);
		GridBagLayout gbl_pnlTop = new GridBagLayout();
		gbl_pnlTop.columnWeights = new double[] { 1.0, 1.9 };
		gbl_pnlTop.rowWeights = new double[] { 1.3, 1.0 };
		pnlTop.setLayout(gbl_pnlTop);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		pnlTop.add(panel, gbc_panel);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane pnlTable = new JScrollPane();
		panel.add(pnlTable, BorderLayout.CENTER);
		pnlTable.setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		pnlTable.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
		pnlTable.setPreferredSize(new Dimension(100, Integer.MAX_VALUE));

		tblMail = new MailTable(this);

		pnlTable.setRowHeaderView(tblMail);
		pnlTable.setViewportView(tblMail);

		JPanel pnlButton = new JPanel();
		panel.add(pnlButton, BorderLayout.SOUTH);

		btnRefresh = new JButton("Refresh");
		btnRefresh.setToolTipText("Refresh E-Mail List");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshList();
			}
		});
		pnlButton.add(btnRefresh);

		JButton btnReset = new JButton("Parse");
		btnReset.setToolTipText("Parse selected e-mail message");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parseMail();
			}
		});
		pnlButton.add(btnReset);

		btnPost = new JButton("Create Booking");
		btnPost.setToolTipText("Create a booking with provided information");
		btnPost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				postBooking();
			}
		});
		pnlButton.add(btnPost);

		tblMail.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				parseMail();
			}
		});

		JScrollPane scrBooking = new JScrollPane();
		scrBooking.setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		pnlTop.add(scrBooking, gbc_scrollPane);

		pnlContent = new SpringPanel();
		scrBooking.setViewportView(pnlContent);

		JScrollPane scrMailContent = new JScrollPane();
		scrMailContent.setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		GridBagConstraints gbc_scrContent = new GridBagConstraints();
		gbc_scrContent.gridwidth = 2;
		gbc_scrContent.fill = GridBagConstraints.BOTH;
		gbc_scrContent.insets = new Insets(0, 0, 5, 0);
		gbc_scrContent.gridx = 0;
		gbc_scrContent.gridy = 1;
		pnlTop.add(scrMailContent, gbc_scrContent);

		txtMail = new HTMLPanel();
		scrMailContent.setViewportView(txtMail);

		JPanel pnlStatus = new JPanel();
		pnlStatus.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		contentPane.add(pnlStatus, BorderLayout.SOUTH);
		pnlStatus.setLayout(new BorderLayout(0, 0));

		lblStatus = new LinkLabel("Ready");
		lblBooking = new JLabel("Last Booking Id: ");
		lblBooking.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				String bookingId = lblBooking.getText().split(": ")[1];
				StringSelection stringSelection = new StringSelection(bookingId);
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
			}
		});
		pnlStatus.add(lblStatus, BorderLayout.CENTER);
		pnlStatus.add(lblBooking, BorderLayout.EAST);

		addWindowStateListener(this);
	}

	private void parseMail() {
		if (tblMail.getSelectedRow() >= 0) {
			lblStatus.setText("Parsing selected e-mail to generate booking information");
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String subject = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 1);

			Message msg = tblMail.getSelectedMail();
			txtMail.setContent(msg);

			String parser = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 3);

			if (txtMail.getContentType().equals("text/plain")) {
				ParserController.fillContent(subject, txtMail.getText(), parser);
			} else {
				String con = ParserUtils.html2Text(txtMail.getText());
				ParserController.fillContent(subject, con, parser);
			}

			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void postBooking() {
		btnPost.setEnabled(false);
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		lblStatus.setText("Sending booking information to Booking engine");
		try {
			ParserController.postBooking();
			lblStatus.setText("Booking created");
		} catch (ApiException e) {
			if (e.getCode() != 201) {
				Logger.err(e.getMessage());
				lblStatus.setText(e.getMessage());
			} else {
				String tokens[] = e.getMessage().split("/");
				String bookingId = tokens[tokens.length - 1];

				StringSelection stringSelection = new StringSelection(bookingId);
				Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
				clpbrd.setContents(stringSelection, null);
				lblStatus.setText("<HTML>Booking successfully created <a href=>" + bookingId + "</a></HTML>",
						e.getMessage());
				lblBooking.setText("Last Booking: " + bookingId);

				// move processed e-mail
				Message msg = tblMail.getSelectedMail();
				try {
					MailReader.moveMail(msg);
					tblMail.removeSelected();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					Logger.err("Cannot move e-mail to another folder");
				}
			}
		}

		btnPost.setEnabled(true);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	}

	private void refreshList() {
		lblStatus.setText("Reading e-mails from the server");
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		btnRefresh.setEnabled(false);
		ParserController.refreshMailList();
		btnRefresh.setEnabled(true);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		lblStatus.setText("Ready");
	}

	public void windowStateChanged(WindowEvent e) {
		int state = e.getNewState();

		if (state == Frame.ICONIFIED) {
			ParserController.isPaused(false);
		} else {
			ParserController.isPaused(true);
		}
	}
}
