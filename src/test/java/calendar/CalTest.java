import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import Calendar.*;
import NotificationSystem.*;
import java.time.LocalDate;
import java.time.LocalTime;


public class CalTest {
	@Test
	public static void addEvent() {
		String name = "testName";
	    String desc = "test description";
		LocalDate d = LocalDate.now();
		LocalTime t = LocalTime.now();
		Event e = new Event(name, desc, d, t);
		assertEquals(e.getName(), name);
		assertEquals(e.getDescription(), desc);
		assertEquals(e.getDate(), d);
		assertEquals(e.getTime(), t);
	}

	
}
