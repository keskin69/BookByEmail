package yellowzebra.parser;

import java.text.ParseException;
import java.util.Date;

import io.swagger.client.model.Product;
import yellowzebra.booking.EventTools;
import yellowzebra.booking.ProductTools;
import yellowzebra.util.Logger;
import yellowzebra.util.MailConfig;
import yellowzebra.util.MyBooking;

public abstract class AParser implements IParser {
	protected String subjectReg = null;
	protected String fromReg = null;
	protected String agent = null;
	protected MyBooking booking = null;

	public abstract String trimBody(String msg);

	protected void core() {
		booking = new MyBooking();
		booking.setInitialPayments(null);
		booking.setCouponCodes(null);
		booking.setOptions(null);
		booking.setResources(null);
		booking.setBookingNumber(null);
		booking.agent = agent;
	}

	public boolean isApplicable(String subject, String from) {
		if (subject.startsWith(subjectReg) && from.equals(fromReg)) {
			return true;
		}

		return false;
	}

	protected void setProduct(String product, Date date, String time) {
		booking.setProductName(product);
		String productId = ProductTools.getInstance().getProductId(product);

		if (productId == null) {
			Logger.err("\"" + product + "\" cannot be found in available tour names");
		}

		if (productId != null) {
			booking.setProductId(productId);

			Product.TypeEnum prodType = ProductTools.getInstance().getProductType(product);
			if (prodType == Product.TypeEnum.FIXED) {
				String eventId = new EventTools().getEventId(productId, date, time);
				if (eventId == null) {
					Logger.err("\"" + product + "\" is not avaiable at " + date + " " + time);
				}

				booking.setEventId(eventId);
			}

			try {
				Date startDate = MailConfig.DEFAULT_DATE.parse(MailConfig.SHORTDATE.format(date) + " " + time);
				booking.setStartTime(startDate);
			} catch (ParseException e) {
				Logger.err("\"" + product + "\" is not avaiable at " + date + " " + time);
			}
		}
	}

	public static String[] split(String str, String delim) {
		String token[] = str.split(delim);

		for (int i = 0; i < token.length; i++) {
			token[i] = token[i].trim();
		}

		return token;
	}

	public static String getLine(String msg) {
		String line = msg.substring(0, msg.indexOf("\n"));

		return line;
	}

	public static String skipUntil(String msg, String key) {
		int iS = msg.indexOf(key);
		int iE = msg.indexOf("\n", iS + key.length());

		return msg.substring(iE + 1);
	}

	public static String strip(String msg, String key) {
		int iS = msg.indexOf(key);

		return msg.substring(iS + key.length());
	}

	public static String findLine(String msg, String key) {
		int iS = msg.indexOf(key);
		int iE = msg.indexOf("\n", iS + 1);

		return msg.substring(iS + key.length() + 1, iE).trim();
	}
}
