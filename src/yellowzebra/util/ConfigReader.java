package yellowzebra.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.w3c.dom.Element;

public class ConfigReader extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ConfigReader instance = null;
	private static String file = null;

	public static void init(String file) {
		ConfigReader.file = file;
		new ConfigReader();
	}

	public static ConfigReader getInstance() {
		if (instance == null) {
			instance = new ConfigReader();
		}

		return instance;
	}

	public int getInt(String key) {
		return new Integer(getProperty(key)).intValue();
	}

	public String getProperty(String key) {
		String out = super.getProperty(key);

		if (out == null) {
			//Logger.err(key + " not defined in configuration file ");
		} else if (out.startsWith("ENC(")) {
			out = decrypt(out.substring(4, out.length() - 1));
		}

		return out;
	}

	private ConfigReader() {
		try {
			File jarFile = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
			InputStream in = new FileInputStream(jarFile.getAbsolutePath() + "\\..\\..\\" + file);
			load(in);
		} catch (IOException e) {
			Logger.err("Cannot read properties file " + file);
		} catch (NullPointerException ex) {
			Logger.err("Cannot read properties file " + file);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static final String PASSWORD = "YELLOW";

	private final static String decrypt(String str) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(PASSWORD);

		return encryptor.decrypt(str);
	}

	public static String encrypt(String in) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(PASSWORD);

		return encryptor.encrypt(in);
	}

	public static String readValue(Element e, String field) {
		String value = null;
		try {
			value = e.getElementsByTagName(field).item(0).getTextContent();

			if (value.startsWith("ENC(")) {
				value = decrypt(value.substring(4, value.length() - 1));
			}
		} catch (NullPointerException ex) {
			value = "";
		}

		return value;
	}

	public static void main(String[] args) {
		System.out.println("ENC(" + ConfigReader.encrypt("") + ")");
	}
}
