package NotificationSystem;

import NotificationSystem.Notification;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class NotificationThread extends Thread {
    //Reference to ns cause we want to propagate
    //notify event back up there to be handled.
    //TODO: Refactor to a event emitter instead
    private NotificationSystem ns;

    public NotificationThread(NotificationSystem ns) {
        this.ns = ns;
    }

    public void run() {
        System.out.println("Notification Thread started");

        //Start thread loop every 1000 milliseconds
        while(true) {
            try {
                //sleep 1 second then run operations to check notifications 
                this.sleep(1000);

                //handle thread events
                this.threadLoop(); 
            } catch (InterruptedException e) {
                e.printStackTrace(); 
            }
        }
    }

    //Check if any events are ready to happen 
    public void threadLoop() {
        //Use synchronized to make notifications list thread safe 
        List<Notification> notifications = this.ns.getNotifications();
        synchronized(notifications) {
            Iterator<Notification> it = notifications.iterator(); // Must be in sync block

            //Check if any notifications are ready
            System.out.println(notifications.size() + " pending notifications..");
            while(it.hasNext()) {
                Notification n = it.next();
                System.out.println("\t[ " + n.toString() +"]");
                if(n.isTimeToNotify()) {
                    ns.notify(n);
                    
                    //Delete notif after
                    it.remove();
                }
            }
        }    
    }
}
