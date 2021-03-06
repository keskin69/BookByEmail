package yellowzebra.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.border.LineBorder;

import yellowzebra.util.Logger;

public class SpringPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5022909574876820687L;
	public SpringLayout layout = null;
	private static int HEIGHT = 23;
	private int row = 0;
	private String productName = null;

	public SpringPanel() {
		layout = new SpringLayout();
		setLayout(layout);
	}

	public String getProductName() {
		return productName;
	}

	public void addCombo(String label, String choice, String[] products) {
		choice = choice.trim();
		JLabel lbl = new JLabel(label);
		final JComboBox<String> combo = new JComboBox<String>(products);
		for (int i = 0; i < products.length; i++) {
			if (products[i].toUpperCase().equals(choice.toUpperCase())) {
				combo.setSelectedIndex(i);
				break;
			}
		}

		if (combo.getSelectedIndex() != 0) {
			addRow("Tour Name", choice);
			productName = choice;
		} else {
			addRow(lbl, null);
			row -= 10;
			addRow(combo, null);
			row += 10;

			Logger.err("Product \"" + choice + "\" cannot be found in the available tour list of Bookeo");
			combo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					productName = (String) combo.getSelectedItem();
				}
			});
		}
	}

	public void addRow(String key, String value) {
		JLabel lbl = new JLabel(key);
		TaggedText txt = null;

		if (value != null) {
			txt = new TaggedText(key, value);
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

	public String getValue(String tag) {
		for (Component comp : this.getComponents()) {
			if (comp instanceof TaggedText) {
				TaggedText tComp = (TaggedText) comp;
				if (tComp.tag.equals(tag)) {
					return tComp.getText();
				}
			}
		}

		return null;
	}

	public void addRow(JComponent comp1, JComponent comp2) {
		if (comp1 instanceof JLabel) {
			((JLabel) comp1).setText(((JLabel) comp1).getText() + ":");
			comp1.setForeground(Color.DARK_GRAY);
		}

		if (comp2 instanceof JTextArea) {
			comp2.setBorder(new LineBorder(Color.gray, 1, true));
		}

		layout.putConstraint(SpringLayout.NORTH, comp1, 10 + row, SpringLayout.NORTH, this);
		layout.putConstraint(SpringLayout.WEST, comp1, 10, SpringLayout.WEST, this);
		add(comp1);

		if (comp2 != null) {
			Dimension d = comp2.getPreferredSize();
			int offset = 6;

			if (comp2 instanceof TaggedText) {
				row += 5;
				comp2.setPreferredSize(new Dimension((int) (d.getWidth() * 1.06), (int) d.getHeight()));
				offset = 1;
			}

			layout.putConstraint(SpringLayout.NORTH, comp2, offset + row, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.WEST, comp2, 5, SpringLayout.EAST, comp1);

			add(comp2);
		}

		row += HEIGHT;
	}
}
