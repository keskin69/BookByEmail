package yellowzebra.parser;

import java.io.IOException;

import yellowzebra.util.MyBooking;
import yellowzebra.util.ParserUtils;

public class Getty extends AParser {
	public Getty() {
		subjectReg = "Booking - ";
		fromReg = "do-not-reply@getyourguide.com";
		agent = "Getty";

		core();
	}

	public MyBooking parse(String subject, String msg) {
		// try {
		// msg =
		// ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\getty.html");
		// } catch (IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		String line = null;
		String token[] = null;

		msg = trimBody(msg);

		return booking;
	}

	public String trimBody(String msg) {
		// Main parsing
		msg = skipUntil(msg, "Great news! The following offer has been booked:\n");

		return msg;
	}

	public static void main(String[] args) {
		String msg = null;
		try {
			msg = ParserUtils.readFile("C:\\Mustafa\\workspace\\YellowParser\\getty.html");
			msg = ParserUtils.html2Text(msg);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Getty parser = new Getty();
		MyBooking booking = parser.parse("", msg);
		booking.dump();
	}
}
