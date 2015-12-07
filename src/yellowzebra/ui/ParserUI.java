package yellowzebra.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import com.alee.laf.WebLookAndFeel;
import javax.swing.JLabel;

public class ParserUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1022086231912876450L;
	private JButton btnRefresh = null;
	private JTable tblMail = null;
	private static SpringPanel pnlContent = null;
	private JTextArea txtMail;
	private JLabel lblStatus = null;

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

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParserUI frame = new ParserUI();

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
		setBounds(100, 100, 800, 600);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		ImageIcon img = new ImageIcon("yellow-zebra.jpg");
		setIconImage(img.getImage());

		JPanel pnlTop = new JPanel();
		contentPane.add(pnlTop, BorderLayout.CENTER);
		GridBagLayout gbl_pnlTop = new GridBagLayout();
		gbl_pnlTop.columnWeights = new double[] { 1.0, 1.2 };
		gbl_pnlTop.rowWeights = new double[] { 1.3, 1.0 };
		pnlTop.setLayout(gbl_pnlTop);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridheight = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
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

		JButton btnReset = new JButton("Reset");
		btnReset.setToolTipText("Reset all modifications made on the booking form");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parseMail();
			}
		});
		pnlButton.add(btnReset);

		JButton btnPost = new JButton("Create Booking");
		btnPost.setToolTipText("Create a booking with provided information");
		btnPost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParserController.postBooking();
			}
		});
		pnlButton.add(btnPost);

		tblMail.removeColumn(tblMail.getColumnModel().getColumn(3));
		tblMail.removeColumn(tblMail.getColumnModel().getColumn(3));

		tblMail.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				// TODO warn the user
				parseMail();
			}
		});

		pnlContent = new SpringPanel();
		GridBagConstraints gbc_scrComp = new GridBagConstraints();
		gbc_scrComp.fill = GridBagConstraints.BOTH;
		gbc_scrComp.insets = new Insets(0, 0, 5, 0);
		gbc_scrComp.gridx = 1;
		gbc_scrComp.gridy = 0;
		pnlTop.add(pnlContent, gbc_scrComp);

		JScrollPane scrContent = new JScrollPane();
		scrContent.setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		GridBagConstraints gbc_scrContent = new GridBagConstraints();
		gbc_scrContent.fill = GridBagConstraints.BOTH;
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

		lblStatus = new JLabel("Ready");
		pnlStatus.add(lblStatus);
	}

	private void parseMail() {
		if (tblMail.getSelectedRow() >= 0) {
			setStatus("Parsing selected e-mail to generate booking information");
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			String msg = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 4);
			String parser = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 3);
			ParserController.fillContent(msg, parser);
			txtMail.setText(msg);
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			setStatus("Ready");
		}
	}

	public void setStatus(String str) {
		lblStatus.setText(str);
		lblStatus.paintImmediately(lblStatus.getVisibleRect());
	}

	private void refreshList() {
		ParserController.isPaused(true);
		setStatus("Reading e-mails from the server");
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		btnRefresh.setEnabled(false);
		ParserController.refreshMailList();
		btnRefresh.setEnabled(true);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		setStatus("Ready");
		ParserController.isPaused(false);
	}
}
