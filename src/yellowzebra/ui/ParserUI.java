package yellowzebra.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import yellowzebra.mail.MailReader;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;

public class ParserUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1022086231912876450L;
	private JPanel contentPane;
	private JTable tblMail;
	private JTextField textField;
	private static ArrayList<Entry<String, Message>> list = null;
	private JButton btnRefresh = null;
	private JButton btnReset = null;
	private JButton btnCreate = null;

	private static void setUI() {
	}

	private void refreshMailList() {
		if (true) {
			DefaultTableModel model = (DefaultTableModel) tblMail.getModel();
			model.addRow(new Object[] { "212", "2222", "dddd" });
		} else {

			btnRefresh.setEnabled(false);
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

					// DefaultTableModel model = (DefaultTableModel)
					// tblMail.getModel();
					// model.addRow(new Object[] { from, subject, parser });
				}
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			btnRefresh.setEnabled(true);
		}
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ParserUI frame = new ParserUI();
					setUI();
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
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel pnlTop = new JPanel();
		contentPane.add(pnlTop, BorderLayout.CENTER);
		GridBagLayout gbl_pnlTop = new GridBagLayout();
		gbl_pnlTop.columnWeights = new double[] { 1.0, 1.0 };
		gbl_pnlTop.rowWeights = new double[] { 1.0 };
		pnlTop.setLayout(gbl_pnlTop);

		JPanel pnlContent = new JPanel();
		GridBagConstraints gbc_pnlContent = new GridBagConstraints();
		gbc_pnlContent.insets = new Insets(0, 0, 5, 0);
		gbc_pnlContent.gridwidth = 5;
		gbc_pnlContent.fill = GridBagConstraints.BOTH;
		gbc_pnlContent.gridx = 1;
		gbc_pnlContent.gridy = 0;
		pnlTop.add(pnlContent, gbc_pnlContent);
		pnlContent.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JLabel lblNewLabel = new JLabel("New label");
		pnlContent.add(lblNewLabel);

		textField = new JTextField();
		pnlContent.add(textField);
		textField.setColumns(10);

		JScrollPane pnlTable = new JScrollPane();
		GridBagConstraints gbc_pnlTable = new GridBagConstraints();
		gbc_pnlTable.fill = GridBagConstraints.BOTH;
		gbc_pnlTable.insets = new Insets(0, 0, 5, 0);
		gbc_pnlTable.anchor = GridBagConstraints.WEST;
		gbc_pnlTable.gridx = 0;
		gbc_pnlTable.gridy = 0;
		pnlTop.add(pnlTable, gbc_pnlTable);

		tblMail = new JTable();
		tblMail.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
			}
		});
		tblMail.setFillsViewportHeight(true);
		tblMail.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Sender", "Subject", "Parser" }) {
			boolean[] columnEditables = new boolean[] { false, false, false };

			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		pnlTable.setRowHeaderView(tblMail);
		pnlTable.setViewportView(tblMail);

		JPanel pnlButton = new JPanel();
		contentPane.add(pnlButton, BorderLayout.SOUTH);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshMailList();
			}
		});
		btnRefresh.setToolTipText("Refresh E-Mail List");
		pnlButton.add(btnRefresh);

		btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnReset.setToolTipText("Reset all the modifications made on the form");
		pnlButton.add(btnReset);

		btnCreate = new JButton("Create Booking");
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCreate.setToolTipText("Create a booking with provided information");
		pnlButton.add(btnCreate);
	}

}
