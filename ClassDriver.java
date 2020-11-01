import Calendar.Calendar;
import Calendar.Event;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


class ClassDriver {
	public static void main(String[] args) {
		System.out.println("Driver testing classes...");
		//Create event, ID is automatically assigned randomly
		//To avoid collisions and keep lookup O(1)
		//Will be hoisted into calendar later on 
		Event e = new Event();

		//Can print events 
		System.out.println(e);
		

		//Create calendar
		Calendar c = new Calendar();
		
		//Print all events in calendar
		c.printEvents();

		//Add event from earlier 
		c.addEvent(e);

		//Print
		c.printEvents();
		
		//Remove by id
		c.removeEvent(e.getId());

		Event e2 = new Event("Rick & Morty", "meep", LocalDate.now(), LocalTime.now());
		c.addEvent(e2);

		c.printEvents();

		//Remove by event
		c.removeEvent(e2);

		//Print
		c.printEvents();
		
		Event e3 = new Event("Singularity", "Chicken Caesar Salad Day", LocalDate.now(), LocalTime.now());
		
		c.addEvent(e3);
		c.printEvents();
		for(int i = 0; i < 8; i++) {
			c.addEvent(new Event("Event" + i, "Garbage desc " + i, LocalDate.now(), LocalTime.now()));
		}
		c.printEvents();
		
		//Get all events as list
		List<Event> events = c.getAllEvents();

		System.out.println("Printing first three events");
		

		//Test events for week
		LocalDate d = LocalDate.now();

		//Create 50 daily events 
		for(int i = 0; i < 50; i++) {
			c.addEvent(new Event("Test " + i, "Test desc", d, LocalTime.now()));
			
			//increment date
			d = d.plusDays(1);
		}
		
		ArrayList<Event> week1 = c.getWeekOfEventsFor(11, 4);			
		ArrayList<Event> week2 = c.getWeekOfEventsFor(11, 10);			
		ArrayList<Event> week3 = c.getWeekOfEventsFor(11, 18);			
		ArrayList<Event> week4 = c.getWeekOfEventsFor(11, 28);			
		ArrayList<Event> week5 = c.getWeekOfEventsFor(12, 10);			

		System.out.println("WEEK 1: ");
		for(int i = 0; i < week1.size(); i++) {
			System.out.println(week1.get(i));
		}

		System.out.println("WEEK 2: ");
		for(int i = 0; i < week2.size(); i++) {
			System.out.println(week2.get(i));
		}

		System.out.println("WEEK 3: ");
		for(int i = 0; i < week3.size(); i++) {
			System.out.println(week3.get(i));
		}

		System.out.println("WEEK 4: ");
		for(int i = 0; i < week4.size(); i++) {
			System.out.println(week4.get(i));
		}

		System.out.println("WEEK 5: ");
		for(int i = 0; i < week5.size(); i++) {
			System.out.println(week5.get(i));
		}


		ArrayList<Event> eventsThisMonth = c.getEventsForMonth();
		System.out.println("ALL EVENTS: ");
		for(int i = 0; i < eventsThisMonth.size(); i++) {
			System.out.println(eventsThisMonth.get(i));
		}
		
	}

}

