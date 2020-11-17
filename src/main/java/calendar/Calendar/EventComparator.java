package Calendar;

import Calendar.Event;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;

// compares 2 events based on their localdate and localtime
public class EventComparator implements Comparator<Event> {
	@Override
	public int compare(Event e1, Event e2) {
		int result = e1.getDate().compareTo(e2.getDate());
		if(result == 0) // if dates are equal, look at time
			result = e1.getTime().compareTo(e2.getTime());
		return result;
	}

}
