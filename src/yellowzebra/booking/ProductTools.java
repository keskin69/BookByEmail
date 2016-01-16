package yellowzebra.booking;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.Configuration;
import io.swagger.client.api.SettingsApi;
import io.swagger.client.model.Product;
import io.swagger.client.model.Product.TypeEnum;
import io.swagger.client.model.ProductList;
import yellowzebra.util.ConfigReader;
import yellowzebra.util.Logger;
import yellowzebra.util.MailConfig;

public class ProductTools extends ArrayList<Product> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 758440150183126121L;
	private static ProductTools instance = null;
	private static String productArray[] = null;

	// Product list changes rarely
	// Make it as a singletons
	public static ProductTools getInstance() {
		if (instance == null) {
			// String prodSer = System.getProperty("java.io.tmpdir") +
			// "\\product.ser";
			// if (new File(prodSer).exists()) {
			// instance = (ProductTools) ParserUtils.readObject(prodSer);
			// } else {
			instance = new ProductTools();
			// ParserUtils.writeObject(instance, prodSer);
			// }
		}

		return instance;
	}

	public String[] getProducts() {
		return productArray;
	}

	private ProductTools() {
		Logger.log("Reading Tour names from Bookeo");
		SettingsApi settingsApi = new SettingsApi();
		ProductList list = new ProductList();

		try {
			list = settingsApi.settingsProductsGet(null, 20, null, 1);
			String pageNavigationToken = list.getInfo().getPageNavigationToken();
			this.addAll(list.getData());

			for (int i = 2; i <= list.getInfo().getTotalPages(); i++) {
				list = settingsApi.settingsProductsGet(null, 20, pageNavigationToken, i);
				this.addAll(list.getData());
			}
		} catch (ApiException e) {
			Logger.err(e.getMessage());
			Logger.exception(e);
		}

		productArray = new String[this.size() + 1];
		productArray[0] = "";
		int i = 1;
		for (Product p : this) {
			productArray[i++] = p.getName();
		}

		//Arrays.sort(productArray);
	}

	public String getProductId(String name) {
		for (Product p : this) {
			if (p.getName().equals(name)) {
				return p.getProductId();
			}
		}

		return null;
	}

	public TypeEnum getProductType(String name) {
		for (Product p : this) {
			if (p.getName().equals(name)) {
				return p.getType();
			}
		}

		return null;
	}

	public int getDuration(String name) {
		for (Product p : this) {
			if (p.getName().equals(name)) {
				return p.getDuration().getHours() * 60 + p.getDuration().getMinutes();
			}
		}

		return 0;
	}

	public void dump() {
		for (Product p : this) {
			System.out.println(p.getName() + ":" + p.getProductId() + ":" + p.getType() + ":" + p.getProductCode());
		}
	}

	public static void main(String[] args) {
		ConfigReader.init("config.properties");

		// init Bookeo API
		String apiKey = ConfigReader.getInstance().getProperty("api_key");
		String secretKey = ConfigReader.getInstance().getProperty("secret_key");
		Configuration.setKey(apiKey, secretKey);

		Logger.init();
		ProductTools.getInstance().dump();
		System.out.println("Total:" + productArray.length);
	}
	
	public void test1() {
		String productId = ProductTools.getInstance().getProductId("Dinner Cruise with Live Music");
		TypeEnum prodType = ProductTools.getInstance().getProductType("Dinner Cruise with Live Music");

		if (prodType == TypeEnum.FIXED) {
			Date date;
			try {
				date = MailConfig.SHORTDATE.parse("2015-12-12");
				String eventId = new EventTools().getEventId(productId, date, "19:00");
				System.out.println(productId + ":" + eventId);
			} catch (ParseException e) {
				Logger.exception(e);
			}

		} else {
			System.out.println(productId);
		}
	}
}
