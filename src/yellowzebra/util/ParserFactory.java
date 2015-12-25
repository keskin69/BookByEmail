package yellowzebra.util;

import java.util.ArrayList;
import java.util.List;


import yellowzebra.parser.AParser;

public class ParserFactory {
	private static ParserFactory instance = null;
	private static ArrayList<AParser> parserArray = null;

	public static ParserFactory getInstance() {
		if (instance == null) {
			instance = new ParserFactory();
		}

		return instance;
	}

	public AParser getParser(String parserName) {
		for (AParser parser : parserArray) {
			if (parser.getClass().getName().equals(parserName)) {
				return parser;
			}
		}

		return null;
	}

	public String isParsable(String subject, String from) {
		for (AParser parser : parserArray) {
			if (parser.isApplicable(subject, from)) {
				return parser.getClass().getName();
			}
		}

		return null;
	}

	private ParserFactory() {
		parserArray = new ArrayList<AParser>();
		System.out.println("Searching for available parsers");
		
		//List<Class<?>> classes = ClassFinder.find("yellowzebra");
		List<Class<?>> classes = ClassFinder.getClassesForPackage("yellowzebra.parser");
		
		for (Class<?> c : classes) {
			if (c.getSuperclass() != null) {
				if (c.getSuperclass().getName().endsWith("AParser")) {
					try {
						AParser parser = (AParser) c.newInstance();
						System.out.println("Parser for " + parser.getClass().getName() + " found.");
						parserArray.add(parser);
					} catch (InstantiationException e) {
						Logger.exception(e);
					} catch (IllegalAccessException e) {
						Logger.exception(e);
					}
				}
			}
		}
	}
}
