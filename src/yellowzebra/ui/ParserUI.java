package yellowzebra.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
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

public class ParserUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1022086231912876450L;
	private JButton btnRefresh = null;
	private DefaultTableModel model = null;
	private JTable tblMail = null;
	private JPanel pnlContent = null;
	private JTextArea txtMail;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		WebLookAndFeel.install();

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParserUI frame = new ParserUI();
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
		setBounds(100, 100, 900, 500);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		System.out.println(System.getProperty("user.dir"));
		ImageIcon img = new ImageIcon("yellow-zebra.jpg");
		setIconImage(img.getImage());

		JPanel pnlTop = new JPanel();
		contentPane.add(pnlTop, BorderLayout.CENTER);
		GridBagLayout gbl_pnlTop = new GridBagLayout();
		// gbl_pnlTop.columnWidths = new int[] { 1, 4 };
		gbl_pnlTop.columnWeights = new double[] { 1.0, 1.8 };
		gbl_pnlTop.rowWeights = new double[] { 1.5, 1.0 };
		pnlTop.setLayout(gbl_pnlTop);

		pnlContent = new JPanel();
		pnlContent.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		GridBagConstraints gbc_pnlContent = new GridBagConstraints();
		gbc_pnlContent.insets = new Insets(0, 0, 5, 0);
		gbc_pnlContent.anchor = GridBagConstraints.NORTHEAST;
		gbc_pnlContent.fill = GridBagConstraints.BOTH;
		gbc_pnlContent.gridx = 1;
		gbc_pnlContent.gridy = 0;
		pnlTop.add(pnlContent, gbc_pnlContent);
		pnlContent.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JScrollPane pnlTable = new JScrollPane();
		pnlTable.setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		pnlTable.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
		pnlTable.setPreferredSize(new Dimension(100, Integer.MAX_VALUE));
		GridBagConstraints gbc_pnlTable = new GridBagConstraints();
		gbc_pnlTable.gridheight = 2;
		gbc_pnlTable.fill = GridBagConstraints.BOTH;
		gbc_pnlTable.insets = new Insets(0, 0, 0, 5);
		gbc_pnlTable.anchor = GridBagConstraints.WEST;
		gbc_pnlTable.gridx = 0;
		gbc_pnlTable.gridy = 0;
		pnlTop.add(pnlTable, gbc_pnlTable);

		tblMail = new JTable();
		tblMail.setFillsViewportHeight(true);
		model = new DefaultTableModel(new Object[][] {},
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
		tblMail.setModel(model);
		pnlTable.setRowHeaderView(tblMail);
		pnlTable.setViewportView(tblMail);
		tblMail.removeColumn(tblMail.getColumnModel().getColumn(3));
		tblMail.removeColumn(tblMail.getColumnModel().getColumn(3));

		tblMail.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				// generate the components from the message content
				// TODO warn the user
				if (tblMail.getSelectedRow() >= 0) {
					parseMail();
				}
			}
		});

		JPanel pnlButton = new JPanel();
		contentPane.add(pnlButton, BorderLayout.SOUTH);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshList();
			}
		});
		btnRefresh.setToolTipText("Refresh E-Mail List");
		pnlButton.add(btnRefresh);

		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parseMail();
			}
		});
		btnReset.setToolTipText("Reset all the modifications made on the form");
		pnlButton.add(btnReset);

		JButton btnCreate = new JButton("Create Booking");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ParserController.postBooking();
			}
		});
		btnCreate.setToolTipText("Create a booking with provided information");
		pnlButton.add(btnCreate);

		// init controller thread
		ParserController controller = new ParserController(model, pnlContent);

		JScrollPane scrContent = new JScrollPane();
		scrContent.setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		GridBagConstraints gbc_scrContent = new GridBagConstraints();
		gbc_scrContent.fill = GridBagConstraints.BOTH;
		gbc_scrContent.gridx = 1;
		gbc_scrContent.gridy = 1;
		pnlTop.add(scrContent, gbc_scrContent);

		txtMail = new JTextArea();
		scrContent.setViewportView(txtMail);
		txtMail.setVerifyInputWhenFocusTarget(false);
		txtMail.setEditable(false);
		txtMail.setWrapStyleWord(true);
		Thread t = new Thread(controller);
		t.start();
	}

	private void parseMail() {
		String msg = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 4);
		String parser = (String) tblMail.getModel().getValueAt(tblMail.getSelectedRow(), 3);
		ParserController.fillContent(msg, parser);
		txtMail.setText(msg);
	}

	private void refreshList() {
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		btnRefresh.setEnabled(false);
		ParserController.refreshMailList();
		btnRefresh.setEnabled(true);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
}
