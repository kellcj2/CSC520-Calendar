import Calendar.Calendar;
import Calendar.Event;
import java.util.Date;
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

		Event e2 = new Event("Rick & Morty", "meep", new Date());
		c.addEvent(e2);

		c.printEvents();

		//Remove by event
		c.removeEvent(e2);

		//Print
		c.printEvents();
		
		Event e3 = new Event("Singularity", "Chicken Caesar Salad Day", new Date());
		
		c.addEvent(e3);
		c.printEvents();
		for(int i = 0; i < 8; i++) {
			c.addEvent(new Event("Event" + i, "Garbage desc " + i, new Date()));
		}
		c.printEvents();
		
		//Get all events as list
		List<Event> events = c.getAllEvents();

		System.out.println("Printing first three events");
		for(int i = 0; i < 3; i++) {
			System.out.println(events.get(i));
		}
		
	}

}
