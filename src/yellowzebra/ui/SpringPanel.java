package yellowzebra.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

public class SpringPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5022909574876820687L;
	public SpringLayout layout = null;
	private static int HEIGHT = 23;
	private int row = 0;

	public SpringPanel() {
		layout = new SpringLayout();
		setLayout(layout);
	}

	public void addRow(String key, String value) {
		JLabel lbl = new JLabel(key);
		JTextField txt = null;

		if (value != null) {
			txt = new JTextField(value);
		}

		addRow(lbl, txt);
	}

	public void reset() {
		removeAll();
		row = 0;

		revalidate();
		getTopLevelAncestor().validate();
		repaint();
	}

	public void setLayout() {
		revalidate();
		getTopLevelAncestor().validate();
		setPreferredSize(new Dimension(0, row + HEIGHT));
	}

	public void addRow(JComponent comp1, JComponent comp2) {
		if (comp2 == null) {
			row += HEIGHT / 2;
			((JLabel) comp1).setForeground(Color.DARK_GRAY);
		}

		if (comp1 instanceof JLabel) {
			((JLabel) comp1).setText(((JLabel) comp1).getText() + ":");
			comp1.setForeground(Color.DARK_GRAY);
		}

		if (comp2 instanceof JTextArea) {
			comp2.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		}

		layout.putConstraint(SpringLayout.NORTH, comp1, 10 + row, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, comp1, 10, SpringLayout.WEST, this);
		add(comp1);

		if (comp2 != null) {
			Dimension d = comp2.getPreferredSize();
			comp2.setPreferredSize(new Dimension((int) (d.getWidth() * 1.2), (int) d.getHeight()));
			layout.putConstraint(SpringLayout.NORTH, comp2, 5 + row, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.WEST, comp2, 5, SpringLayout.EAST, comp1);
			add(comp2);
		}

		row += HEIGHT;
	}
}
