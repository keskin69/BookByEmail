package yellowzebra.ui;

import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class TaggedText extends JTextField {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5381636841677946788L;
	String tag = null;

	public TaggedText(String tag, boolean err) {
		this.tag = tag;
		setError(err);
	}

	public void setError(boolean err) {
		if (err) {
			setBorder(new LineBorder(Color.red, 1, true));
		} else {
			setBorder(new LineBorder(Color.gray, 1, true));
		}
	}
}
