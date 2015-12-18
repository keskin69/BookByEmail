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
	protected String content;

	public abstract void trimBody(String msg);

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
		} else {
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
			token[i] = token[i].replace(String.valueOf((char) 160), " ").trim();
		}

		return token;
	}

	public String getLine() {
		int idx = content.indexOf("\n");
		String line = content.substring(0, idx);
		content = content.substring(idx + 1);

		return line.trim();
	}

	public String getNextLine() {
		getLine();
		return getLine();
	}

	public void skipAfter(String key) {
		int iS = content.indexOf(key);

		content = content.substring(iS + key.length());
	}

	public String strip(String msg, String key) {
		int iS = msg.indexOf(key);

		return msg.substring(iS + key.length());
	}

}
