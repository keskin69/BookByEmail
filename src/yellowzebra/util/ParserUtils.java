package yellowzebra.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
			// handle exception
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

		if (in.startsWith("ADULT")) {
			return "Cadults";
		} else if (in.startsWith("CHILDREN")) {
			return "Cchildren";
		} else if (in.startsWith("INFANT")) {
			return "Cinfants";
		} else if (in.startsWith("STUDENT")) {
			return "Cstudent";
		} else {
			return "Cadults";
		}

	}

	public static void writeObject(Object obj, String fileName) {
		try {
			FileOutputStream fout = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(obj);
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Object readObject(String fileName) {
		Object obj = null;
		try {
			FileInputStream fin = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fin);
			try {
				obj = ois.readObject();
				ois.close();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}
}
