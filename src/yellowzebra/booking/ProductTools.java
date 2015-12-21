package yellowzebra.booking;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.api.SettingsApi;
import io.swagger.client.model.Product;
import io.swagger.client.model.Product.TypeEnum;
import io.swagger.client.model.ProductList;
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
			instance = new ProductTools();
		}

		return instance;
	}

	public String[] getProducts() {
		return productArray;
	}

	private ProductTools() {
		SettingsApi settingsApi = new SettingsApi();
		ProductList list = new ProductList();

		try {
			list = settingsApi.settingsProductsGet(null, 20, null, 1);
			String pageNavigationToken = list.getInfo().getPageNavigationToken();
			this.addAll(list.getData());

			for (int i = 2; i < list.getInfo().getTotalPages(); i++) {
				list = settingsApi.settingsProductsGet(null, 20, pageNavigationToken, i);
				this.addAll(list.getData());
			}
		} catch (ApiException e) {
			Logger.exception(e);
		}

		productArray = new String[this.size() + 1];
		productArray[0] = "";
		int i = 1;
		for (Product p : this) {
			productArray[i++] = p.getName();
		}
		
		Arrays.sort(productArray);
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
			System.out.println(p.getName() + ":" + p.getProductId() + ":" + p.getType());
		}
	}

	public static void main(String[] args) {
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
