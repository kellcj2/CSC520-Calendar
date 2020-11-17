import static org.junit.Assert.*;
import org.junit.Test;

import Calendar.*;
import NotificationSystem.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class CalTest {
	@Test
	public void addEvent() {
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

    @Test
	public void removeEvent1() {
        Calendar cal = new Calendar();
        ArrayList<Event> events = new ArrayList<Event>();

        for(int i = 0; i < 100; i++) {
            Event e = new Event("Random Event " + i+1,  "Description " + i,
                                                    LocalDate.now(), LocalTime.now());
            cal.addEvent(e);
        }

        assertEquals(cal.getAllEvents().size(), 100);
	}

    @Test
    public void createNotifications() {
        NotificationSystem ns = new NotificationSystem();


        ArrayList<Notification> notifs = new ArrayList<Notification>();
        //Create 50 random notifs
        for(int i = 0; i < 50; i++) {
            notifs.add(new Notification("Random Notif " + i, i, LocalDate.now(), 
                            LocalTime.now().plusSeconds(10), NotificationType.DESKTOP));
            ns.add(notifs.get(i));
        }
        assertEquals(ns.getNotifications().size(), 50);
    
        //Remove 10 by notification
        for(int i = 0; i < 10; i++) {
            ns.remove(notifs.get(i));
        }
        assertEquals(ns.getNotifications().size(), 40);
        //assertEquals(50, 50);
    }

    @Test
    public void removeNotifByNotif() {
        NotificationSystem ns = new NotificationSystem();


        ArrayList<Notification> notifs = new ArrayList<Notification>();
        //Create 50 random notifs
        for(int i = 0; i < 650; i++) {
            notifs.add(new Notification("Random Notif " + i, i, LocalDate.now(), 
                            LocalTime.now().plusSeconds(10), NotificationType.DESKTOP));
            ns.add(notifs.get(i));
        }
    
        //Remove 10 by notification
        for(int i = 0; i < 100; i++) {
            ns.remove(notifs.get(i));
        }
        assertEquals(ns.getNotifications().size(), 550);
        //assertEquals(50, 50);
    }
	
    @Test
    public void removeNotifById() {
        NotificationSystem ns = new NotificationSystem();


        ArrayList<Notification> notifs = new ArrayList<Notification>();
        //Create 50 random notifs
        for(int i = 0; i < 100; i++) {
            notifs.add(new Notification("Random Notif " + i, i, LocalDate.now(), 
                            LocalTime.now().plusSeconds(10), NotificationType.DESKTOP));
            ns.add(notifs.get(i));
        }
    
        //Remove 10 by notification
        for(int i = 0; i < 80; i++) {
            ns.removeByEventId(i);
        }
        assertEquals(ns.getNotifications().size(), 20);
        //assertEquals(50, 50);
    }

    @Test
    public void modifyNotifyType() {
        NotificationSystem ns = new NotificationSystem();


        ArrayList<Notification> notifs = new ArrayList<Notification>();
        //Create 50 random notifs
        for(int i = 0; i < 100; i++) {
            notifs.add(new Notification("Random Notif " + i, i, LocalDate.now(), 
                            LocalTime.now().plusSeconds(10), NotificationType.DESKTOP));
            ns.add(notifs.get(i));
        }
    
        int numEmail = 0;

        //Change 80 of them to email type 
        for(int i = 0; i < 80; i++) {
            ns.modifyNotifyType(i , NotificationType.EMAIL);
        }

        
        // Count the number that are of type email 
        for(int i = 0; i < ns.getNotifications().size(); i++) {
            if(ns.getNotifications().get(i).getNotifyType() == NotificationType.EMAIL) {
                numEmail += 1;
            }
        }
        assertEquals(80, numEmail);
        //assertEquals(50, 50);
    }
}
