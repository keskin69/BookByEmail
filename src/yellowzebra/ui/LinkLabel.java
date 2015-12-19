package yellowzebra.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;

import yellowzebra.util.Logger;

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
					Logger.exception(e1);
				} catch (URISyntaxException e1) {
					Logger.exception(e1);
				}

			}
		});
	}
	
	public void setError(String txt) {
		super.setText(" " + txt);
		setForeground(Color.RED);
	}

	public void setText(String txt) {
		setForeground(Color.BLACK);
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
