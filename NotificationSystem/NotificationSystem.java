package NotificationSystem;

import NotificationSystem.Notification;
import NotificationSystem.NotificationThread;
import NotificationSystem.NotificationDelegate;
import NotificationSystem.NotificationType;

import java.util.List;
import java.util.ArrayList;
import java.lang.Thread;
import java.util.Collections;
import java.util.Iterator;

public class NotificationSystem extends Thread {
    private String email;
    private List<Notification> notifications;
    private NotificationThread notificationThread;
    private NotificationDelegate delegate;

    public NotificationSystem() {
        //Default value is my email feelsbadman
        this.email = "dougeemmanuel1@gmail.com"; 

        //Delegate actually responds to notification time
        //And starts that notification pipeline email/desktop
        this.delegate = new NotificationDelegate();

        //Create thread safe list, must use synchronized to
        //guarantee serial access
        this.notifications = Collections.synchronizedList(new ArrayList<Notification>());

        //Loops over and over and checks if any notifications are ready 
        this.notificationThread = new NotificationThread(this);

        //Start that thread
        this.notificationThread.start();
    }

    public NotificationSystem(String userEmail) {
        //Call defeault constructor
        this();

        //Set user email
        this.email = userEmail; 
    }

    //Set email after creation or in constructor
    public void setEmail(String email) {
        this.email = email;
    }

    //Synchronizd addition to notification list 
    public void add(Notification n) {
        //Access to list must be in synchronized to
        //be thread safe
        synchronized(this.notifications) {
            //add notification to list
            this.notifications.add(n);
        }
    }

    //Synchronizd deletion to notification list 
    public void remove(Notification n) {
        //Access to list must be in synchronized to
        //be thread safe
        synchronized(this.notifications) {
            this.notifications.remove(n);
        }
    }

    //Synchronized removal by eventId 
    public void removeByEventId(int eventId) {
        //Access to list must be in synchronized to
        //be thread safe
        synchronized(this.notifications) {
            Iterator<Notification> it = this.notifications.iterator();            
            
            while(it.hasNext()) {
                Notification n = it.next();
                if(n.getEventId() == eventId) {
                    it.remove();
                    break;
                }
            }
        }
    }

    //Synchronized removal by eventId 
    public void modifyNotifyType(int eventId, NotificationType nType) {
        synchronized(this.notifications) {
            Iterator<Notification> it = this.notifications.iterator();            
            
            while(it.hasNext()) {
                Notification n = it.next();
                if(n.getEventId() == eventId) {
                    n.setNotifyType(nType);
                    break;
                }
            }
        }
    }
    
    //Synchronized removal by eventId 
    public void modifyMessage(int eventId, String message) {
        synchronized(this.notifications) {
            Iterator<Notification> it = this.notifications.iterator();            
            
            while(it.hasNext()) {
                Notification n = it.next();
                if(n.getEventId() == eventId) {
                    n.setMessage(message);
                    break;
                }
            }
        }
    }
    
    //Return read only collection of notifications
    //Al other access is synchronized so this can't
    //be modified  
    //Could be used to display all upcoming notifications 
    public List<Notification> getNotifications() {
        //return Collections.unmodifiableList(this.notifications);
        return this.notifications;
    }

    //This notification should be displayed somehow!
    public void notify(Notification n) {
        //Actually notify the user 
        System.out.println("Notifying for notification: " + n.getMessage());
        if(n.getNotifyType() == NotificationType.DESKTOP) {
            this.delegate.notifyDesktop(n);
        } else if(n.getNotifyType() == NotificationType.EMAIL) {
            this.delegate.notifyEmail(n, this.email);
        } else {
            throw new java.lang.RuntimeException("No notification type selected. blowing up!");
        } 
    }
} 
