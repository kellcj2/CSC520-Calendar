package Calendar;

import Calendar.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


public class Calendar {
	private HashMap<Integer, Event> events;

	public Calendar() {
		events = new HashMap<Integer, Event>();		
	}

	public boolean addEvent(Event e) {
		//Assume we can't add it
		boolean wasAdded = false;

		//Only add if key doesn't already exist for Event 
		if(!events.containsKey(e.getId())) {
			events.put(e.getId(), e);
			wasAdded = true;
		}

		return wasAdded;
	}

	public boolean removeEvent(int eventId) {
		//Assume we can't remove it
		boolean wasRemoved = false;

		//Only remove key if it exists
		if(events.containsKey(eventId)) {
			events.remove(eventId);
			wasRemoved = true;
		}

		return wasRemoved;
	}

	public boolean removeEvent(Event e) {
		return this.removeEvent(e.getId());
	}

	public Event getEvent(int eventId) {
		return events.get(eventId);
	}

	public List<Event>getAllEvents() {
		return new ArrayList<Event>(this.events.values()); 
	}

	public void printEvents() {
		System.out.print("All Events ("+ this.getNumEvents()  +") :");
		System.out.println("[");
		for(Map.Entry me : this.events.entrySet()) {
			System.out.println("\t" + me.getValue());
		}
		System.out.println("]");
	}

	public int getNumEvents() {
		return this.events.size();
	}
}

