package yellowzebra.util;

import java.io.IOException;
import java.io.Reader;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class MyHtml2Text extends HTMLEditorKit.ParserCallback {
	StringBuffer s;

	public MyHtml2Text() {
	}

	public void parse(Reader in) throws IOException {
		s = new StringBuffer();
		ParserDelegator delegator = new ParserDelegator();
		delegator.parse(in, this, Boolean.TRUE);
	}

	public void handleText(char[] text, int pos) {
		s.append(text);
		s.append("\n");
	}

	public String getText() {
		return s.toString();
	}
}