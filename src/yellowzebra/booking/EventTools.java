package yellowzebra.booking;

import java.util.Calendar;
import java.util.Date;

import io.swagger.client.ApiException;
import io.swagger.client.api.AvailabilityApi;
import io.swagger.client.model.Slot;
import io.swagger.client.model.SlotList;
import yellowzebra.util.Logger;
import yellowzebra.util.MailConfig;

public class EventTools {
	public String getEventId(String productId, Date startTime, String hour) {
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

			for (Slot s : list.getData()) {
				if (MailConfig.TIMEFORMAT.format(s.getStartTime()).equals(hour)) {
					return s.getEventId();
				}
			}
		} catch (ApiException e) {
			Logger.err(e.getMessage());
			Logger.exception(e);
		}

		return null;
	}
}
