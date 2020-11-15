package NotificationSystem;

import NotificationSystem.NotificationType;

import java.time.LocalDate;
import java.time.LocalTime;

import java.io.Serializable;

public class Notification implements Serializable {
    private String message;
    private int eventId;
    private LocalDate date;
    private LocalTime time;
    private NotificationType notifyType;

    public Notification() {
        this.message = "Default Notification Name";
        this.eventId = -1;
        this.date = LocalDate.now();
        this.time = LocalTime.now().plusSeconds(100);
        this.notifyType = NotificationType.DESKTOP;
    }

    public Notification(String message, int eId, LocalDate date, LocalTime time, NotificationType type) {
        this.message = message; 
        this.eventId = eId; 
        this.date = date; 
        this.time = time; 
        this.notifyType = type;
    }

	public void setEventId(int id) {
		this.eventId = id;
	}
    
    public void setMessage(String message) {
        this.message = message; 
    }

    public void setDate(LocalDate date) {
        this.date = date; 
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setNotifyType(NotificationType type) {
        this.notifyType = type;
    }

    public String getMessage() {
        return this.message; 
    }

    public int getEventId() {
        return this.eventId; 
    }

    public LocalDate getDate() {
        return this.date; 
    }

    public LocalTime getTime() {
        return this.time; 
    }

    public NotificationType getNotifyType() {
        return this.notifyType; 
    }

    //bool -> time to show notification PogU
    public boolean isTimeToNotify() {
        LocalDate todaysDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();
        if(this.date.equals(todaysDate) && isTimeEqualWithoutNano(nowTime)) {
            return true;
        }
        return false;
    }
    
    //check if times are equal, can't use normal equals
    //since we cant guarantee nano seconds will be exact 
    private boolean isTimeEqualWithoutNano(LocalTime otherTime) {
        if(this.time.getHour() != otherTime.getHour()) {
            return false;
        } else if(this.time.getMinute() != otherTime.getMinute()) {
            return false; 
        } else if(this.time.getSecond() != otherTime.getSecond()) {
            return false;
        }

        return true;
    }

    public String toString() {
        return "Message: " + this.message + ", Event Id: " + this.eventId 
                 + ", "+ this.date.toString() + ", " + this.time.toString();
    }

}

