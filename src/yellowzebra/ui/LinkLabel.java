package yellowzebra.ui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;

public class LinkLabel extends JLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url = null;

	public LinkLabel(String txt) {
		super(txt);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if (url != null) {
						Desktop.getDesktop().browse(new URI(url));
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
	}

	public void setText(String txt) {
		super.setText(" " + txt);
		url = null;
		paintImmediately(getVisibleRect());
	}

	public void setText(String txt, final String url) {
		super.setText(txt);
		this.url = url;

		if (url != null) {
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
		paintImmediately(getVisibleRect());
	}
}
