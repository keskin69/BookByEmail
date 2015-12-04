package yellowzebra.booking;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.api.AvailabilityApi;
import io.swagger.client.model.MatchingSlotsSearchParameters;
import io.swagger.client.model.Product.TypeEnum;
import io.swagger.client.model.Slot;
import io.swagger.client.model.SlotList;
import yellowzebra.util.MailConfig;

public class EventTools {
	public EventTools() {

	}

	public String getEventId(String productId, String day, String hour) {
		Date startTime = null;

		try {
			startTime = MailConfig.SHORTDATE.parse(day);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		AvailabilityApi api = new AvailabilityApi();

		String pageNavigationToken = null;

		SlotList list = null;
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(startTime);
			c.add(Calendar.DATE, 1);
			Date endTime = c.getTime();

			list = api.availabilitySlotsGet(productId, startTime, endTime, 20, pageNavigationToken, 1);

			pageNavigationToken = list.getInfo().getPageNavigationToken();
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Slot s : list.getData()) {
			if (MailConfig.TIMEFORMAT.format(s.getStartTime()).equals(hour)) {
				return s.getEventId();
			}
		}

		return null;
	}
}
