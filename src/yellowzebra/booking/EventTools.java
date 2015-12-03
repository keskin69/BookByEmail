package yellowzebra.booking;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.api.AvailabilityApi;
import io.swagger.client.model.Slot;
import io.swagger.client.model.SlotList;
import yellowzebra.util.Config;

public class EventTools {
	public EventTools() {

	}

	public String getEventId(String productId, String day, String hour) {
		AvailabilityApi api = new AvailabilityApi();

		String pageNavigationToken = null;
		Date startTime = null;

		try {
			startTime = Config.SHORTDATE.parse(day);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		SlotList list = null;
		try {
			try {
				startTime = Config.SHORTDATE.parse(day);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Calendar c = Calendar.getInstance();
			c.setTime(startTime);
			c.add(Calendar.DATE, 1);
			Date endTime = c.getTime();

			System.out.println(Config.APIFORMAT.format(startTime));
			System.out.println(Config.APIFORMAT.format(endTime));
			list = api.availabilitySlotsGet(productId, startTime, endTime, 20, pageNavigationToken, 1);
			pageNavigationToken = list.getInfo().getPageNavigationToken();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Slot s : list.getData()) {
			if (Config.TIMEFORMAT.format(s.getStartTime()).equals(hour)) {
				return s.getEventId();
			}

		}

		return null;
	}

}
