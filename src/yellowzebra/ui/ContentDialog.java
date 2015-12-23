package yellowzebra.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ContentDialog extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4212516058105280933L;

	public ContentDialog(String txt) {
		setLayout(new BorderLayout());

		JTextArea txa = new JTextArea(txt);
		add(txa, BorderLayout.CENTER);
		setSize(400, 400);
		setVisible(true);
	}
}
