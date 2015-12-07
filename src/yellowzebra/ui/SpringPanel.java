package yellowzebra.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
		setBorder(new LineBorder(new Color(130, 135, 144), 1, true));
		layout = new SpringLayout();
		setLayout(layout);
	}

	public void reset() {
		removeAll();
		row = 0;

		revalidate();
		getTopLevelAncestor().validate();
	}

	public void addRow(JComponent comp1, JComponent comp2) {
		if (comp2 == null) {
			row++;
			((JLabel) comp1).setForeground(Color.DARK_GRAY);
		}

		if (comp2 instanceof JTextField) {
			// ((JTextField) comp2).setPreferredSize(new Dimension((int)
			// (comp2.getWidth() * 1.2), comp2.getHeight()));
		}

		layout.putConstraint(SpringLayout.NORTH, comp1, 10 + (row * HEIGHT), SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, comp1, 10, SpringLayout.WEST, this);
		add(comp1);

		if (comp2 != null) {
			layout.putConstraint(SpringLayout.NORTH, comp2, 5 + (row * HEIGHT), SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.WEST, comp2, 5, SpringLayout.EAST, comp1);
			add(comp2);
		}

		row++;
	}
}
