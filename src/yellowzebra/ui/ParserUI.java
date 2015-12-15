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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.alee.laf.WebLookAndFeel;

import io.swagger.client.ApiException;
import yellowzebra.util.ConfigReader;
import yellowzebra.util.Logger;

public class ParserUI extends JFrame implements WindowStateListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1022086231912876450L;
	private JButton btnRefresh = null;
	private static JTable tblMail = null;
	private static SpringPanel pnlContent = null;
	private JTextArea txtMail;
	private LinkLabel lblStatus = null;
	private JButton btnPost = null;

	private static DefaultTableModel model = new DefaultTableModel(new Object[][] {},
			new String[] { "Sender", "Subject", "Date", "Parser", "Message" }) {
		/**
				 * 
				 */
		private static final long serialVersionUID = -6699335251415484030L;
		boolean[] columnEditables = new boolean[] { false, false, false, false, false };

		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}
	};

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		WebLookAndFeel.install();

		ConfigReader.init("config.properties");

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParserUI frame = new ParserUI();

					tblMail.removeColumn(tblMail.getColumnModel().getColumn(3));
					tblMail.removeColumn(tblMail.getColumnModel().getColumn(3));
					tblMail.getColumnModel().getColumn(0).setPreferredWidth(110);
					tblMail.getColumnModel().getColumn(1).setPreferredWidth(240);
					tblMail.getColumnModel().getColumn(2).setPreferredWidth(110);

					model.addTableModelListener(new TableModelListener() {
						public void tableChanged(TableModelEvent e) {
							if (model.getRowCount() >= 0) {
								// TODO Change icon
							}
						}
					});

					// init controller thread
					ParserController controller = new ParserController(model, pnlContent);
					Thread t = new Thread(controller);
					t.start();
					frame.setVisible(true);
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
		setBounds(0, 0, 850, 600);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		ImageIcon img = new ImageIcon("yellow-zebra.jpg");
		setIconImage(img.getImage());

		JPanel pnlTop = new JPanel();
		contentPane.add(pnlTop, BorderLayout.CENTER);
		GridBagLayout gbl_pnlTop = new GridBagLayout();
		gbl_pnlTop.columnWeights = new double[] { 1.0, 1.5 };
		gbl_pnlTop.rowWeights = new double[] { 1.3, 1.0 };
		pnlTop.setLayout(gbl_pnlTop);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridheight = 2;
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

		tblMail = new JTable();
		tblMail.setFillsViewportHeight(true);
		tblMail.setModel(model);

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

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		pnlTop.add(scrollPane, gbc_scrollPane);

		pnlContent = new SpringPanel();
		scrollPane.setViewportView(pnlContent);

		JScrollPane scrContent = new JScrollPane();
		scrContent.setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		GridBagConstraints gbc_scrContent = new GridBagConstraints();
		gbc_scrContent.fill = GridBagConstraints.BOTH;
		gbc_scrContent.insets = new Insets(0, 0, 5, 0);
		gbc_scrContent.gridx = 1;
		gbc_scrContent.gridy = 1;
		pnlTop.add(scrContent, gbc_scrContent);

		txtMail = new JTextArea();
		scrContent.setViewportView(txtMail);
		txtMail.setEditable(false);
		txtMail.setWrapStyleWord(true);

		JPanel pnlStatus = new JPanel();
		pnlStatus.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		contentPane.add(pnlStatus, BorderLayout.SOUTH);
		pnlStatus.setLayout(new BorderLayout(0, 0));

		lblStatus = new LinkLabel("Ready");
		pnlStatus.add(lblStatus);

		addWindowStateListener(this);
	}

	private void parseMail() {
		if (tblMail.getSelectedRow() >= 0) {
			lblStatus.setText("Parsing selected e-mail to generate booking information");
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String subject = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 1);
			String msg = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 4);
			String parser = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 3);
			ParserController.fillContent(subject, msg, parser);
			txtMail.setText(msg);
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			lblStatus.setText("Ready");
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
				lblStatus.setText("<HTML>Booking created <a href=>" + bookingId + "</a></HTML>", e.getMessage());
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
