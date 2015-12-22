package yellowzebra.ui;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

public class MailTable extends JTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7887092666504106151L;
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

	public void removeSelected() {
		model.removeRow(getSelectedRow());
	}

	public Object getColumn(int col) {
		return model.getValueAt(getSelectedRow(), col);
	}

	public void clearTable() {
		model.setRowCount(0);
	}

	public void addRow(Object obj[]) {
		model.addRow(obj);
	}

	public MailTable(final ParserUI frm) {
		super();
		setModel(model);

		model.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {

				if (model.getRowCount() >= 0) {
					frm.setIcon("red_email.png");
				} else {
					frm.setIcon("blue_email.png");
				}

				frm.btnRefresh.setText("Get Mails (" + model.getRowCount() + ")");
			}
		});

		// load icon
		frm.setIcon("blue_email.png");

		removeColumn(getColumnModel().getColumn(3));
		removeColumn(getColumnModel().getColumn(3));
		getColumnModel().getColumn(0).setPreferredWidth(100);
		getColumnModel().getColumn(1).setPreferredWidth(200);
		getColumnModel().getColumn(2).setPreferredWidth(110);

		setFillsViewportHeight(true);
	}
}
