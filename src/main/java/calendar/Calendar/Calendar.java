package Calendar;

import Calendar.Event;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Collections;
import java.util.Comparator;

public class Calendar {
	private HashMap<Integer, Event> events;

	public Calendar() {
		events = new HashMap<Integer, Event>();		
	}

	public Calendar(HashMap<Integer, Event> cal) {
		events = cal;
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

	// sorts events by LocalDate then LocalTime
	public ArrayList<Event> getAllEvents() {
		ArrayList<Event> sorted = new ArrayList<Event>(this.events.values());
		Comparator<Event> comp = new EventComparator();
		sorted.sort(comp);
		return sorted;
		//return new ArrayList<Event>(this.events.values()); 
	}
	
	//Gets events for a 7 day period specified by the month and day.
	public ArrayList<Event> getWeekOfEventsFor(int month, int day) {
		assert (month < 0 && month > 12) : "Day or Month not within Range";

		LocalDate dateSpecified = LocalDate.of(LocalDate.now().getYear(), month, day); 

		//get map of DAY -> Integer
		Map<DayOfWeek, Integer> DAY_MAP = this.getDayMap();

		LocalDate sunday = dateSpecified.minusDays(DAY_MAP.get(dateSpecified.getDayOfWeek()));
		LocalDate nextSaturday = sunday.plusDays(6);
		System.out.println(sunday);

		ArrayList<Event> eventsThisWeek = new ArrayList<Event>();

		//iterate through list and add any dates between sun and nextsat
		for(Event e: this.getAllEvents()) {
			int currentDay = e.date.getDayOfMonth();
			int currentMonth = e.date.getMonthValue();
			if(currentDay >= sunday.getDayOfMonth() && currentDay <= nextSaturday.getDayOfMonth()
					&& currentMonth == month) {
				eventsThisWeek.add(e);
			}
		}

		//garbage bb sort
		//sort by day
		for(int i = 0; i < eventsThisWeek.size(); i++) {
			for(int m = 0; m < eventsThisWeek.size(); m++) {
				if(eventsThisWeek.get(i).date.getDayOfMonth() < eventsThisWeek.get(m).date.getDayOfMonth()) {
					Event temp = eventsThisWeek.get(i);	
					eventsThisWeek.set(i, eventsThisWeek.get(m));	
					eventsThisWeek.set(m, temp);	
				}
			} 
		}
	
		return eventsThisWeek; 
	}	
	
	//Get all events for month value specified between range 1-12 inclusive
	public ArrayList<Event> getEventsForMonth(int month) {
		assert (month < 0 && month > 12) : "Day or Month not within Range";

		LocalDate now = LocalDate.now();
		ArrayList<Event> eventsThisMonth = new ArrayList<Event>();
		for(Event e : this.getAllEvents()) {
			if(e.date.getMonthValue() == month) {
				eventsThisMonth.add(e);
			}
		}
			
		//sort	
		for(int i = 0; i < eventsThisMonth.size(); i++) {
			for(int m = 0; m < eventsThisMonth.size(); m++) {
				if(eventsThisMonth.get(i).date.getDayOfMonth() < eventsThisMonth.get(m).date.getDayOfMonth()) {
					Event temp = eventsThisMonth.get(i);	
					eventsThisMonth.set(i, eventsThisMonth.get(m));	
					eventsThisMonth.set(m, temp);	
				}
			} 
		}
		return eventsThisMonth;
	}

	public ArrayList<Event> getEventsForDay(int month, int day) {
		ArrayList<Event> eventsToday = new ArrayList<Event>();
		for(Event e : this.getAllEvents()) {
			if(e.date.getMonthValue() == month
			   && e.date.getDayOfMonth() == day)
				eventsToday.add(e);
		}
		return eventsToday;
	}
	/*
	public ArrayList<Event> getEventsForMonth() {
		return this.getEventsForMonth(LocalDate.now().getMonthValue());
	}
	*/
	public HashMap<Integer, Event> getAllEventsHashMap() {
		return this.events;
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

	public ArrayList<Event> getEventByDate(LocalDate d) {
		ArrayList<Event> todaysEvents = new ArrayList<Event>();
		for(Event e : getAllEvents()) {
			if(e.getDate().isEqual(d))
				todaysEvents.add(e);
		}
		return todaysEvents;
	}

	private Map<DayOfWeek, Integer> getDayMap() {
		return Map.of(
			DayOfWeek.SUNDAY, 0,
			DayOfWeek.MONDAY, 1,
			DayOfWeek.TUESDAY, 2,
			DayOfWeek.WEDNESDAY, 3,
			DayOfWeek.THURSDAY, 4,
			DayOfWeek.FRIDAY, 5,
			DayOfWeek.SATURDAY, 6
		);	
	}
}

