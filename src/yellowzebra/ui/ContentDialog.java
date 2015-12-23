package yellowzebra.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ContentDialog extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4212516058105280933L;
	private static ContentDialog instance = null;
	private static JTextArea txa = null;

	public static ContentDialog getInstance() {
		if (instance == null) {
			instance = new ContentDialog();
		}

		return instance;
	}

	public void setContent(String txt) {
		txa.setText(txt);
		setVisible(true);
	}

	private ContentDialog() {
		setLayout(new BorderLayout());

		JScrollPane scr = new JScrollPane();
		add(scr, BorderLayout.CENTER);
		txa = new JTextArea();
		scr.setViewportView(txa);
		setSize(400, 400);
	}
}
