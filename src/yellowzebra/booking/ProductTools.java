package yellowzebra.booking;

import java.util.ArrayList;

import io.swagger.client.ApiException;
import io.swagger.client.api.SettingsApi;
import io.swagger.client.model.Product;
import io.swagger.client.model.ProductList;

public class ProductTools extends ArrayList<Product> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 758440150183126121L;
	public static ProductTools instance = null;

	// Product list changes rarely
	// Make it as a singletons
	public static ProductTools getInstance() {
		if (instance == null) {
			instance = new ProductTools();
		}

		return instance;
	}

	private ProductTools() {
		SettingsApi settingsApi = new SettingsApi();
		ProductList list = new ProductList();

		try {
			list = settingsApi.settingsProductsGet(Product.TypeEnum.FIXED.toString(), 20, null, 1);
			String pageNavigationToken = list.getInfo().getPageNavigationToken();
			this.addAll(list.getData());

			for (int i = 2; i < list.getInfo().getTotalPages(); i++) {
				list = settingsApi.settingsProductsGet(Product.TypeEnum.FIXED.toString(), 20, pageNavigationToken, i);
				this.addAll(list.getData());
			}
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getProductId(String name) {
		for (Product p : this) {
			if (p.getName().equals(name)) {
				return p.getProductId();
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
			System.out.println(p.getName() + ":" + p.getProductId());
		}
	}

}
