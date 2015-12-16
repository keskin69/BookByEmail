package yellowzebra.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import io.swagger.client.model.PhoneNumber;

public class ParserUtils {
	public static String html2Text(String html) {
		MyHtml2Text parser = new MyHtml2Text();
		try {
		    parser.parse(new StringReader(html));
		} catch (IOException ee) {
		  //handle exception
		}
		
		return parser.getText();
	}
	
	public static String readFile(String file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}

		reader.close();

		return stringBuilder.toString();
	}

	public static List<PhoneNumber> setPhone(String phone) {
		ArrayList<PhoneNumber> list = new ArrayList<PhoneNumber>();
		PhoneNumber phoneNumber = new PhoneNumber();
		phoneNumber.setNumber(phone.trim());
		phoneNumber.setType(PhoneNumber.TypeEnum.MOBILE);
		list.add(phoneNumber);

		return list;
	}

	public static String getCustomerType(String in) {
		in = in.toUpperCase().trim();

		if (in.startsWith("ADULTS")) {
			return "Cadults";
		} else if (in.startsWith("CHILDREN")) {
			return "Cchildren";
		} else if (in.startsWith("INFANTS")) {
			return "Cinfants";
		}

		Logger.err("Unknown customer type" + in);

		return null;
	}
}
