package Calendar;
import java.time.LocalDate;
import java.time.LocalTime;

import java.util.Random;
public class Event {
	public String name;
	public String description;	
	public LocalDate date;
	public LocalTime time;
	private int id;
	
	public Event() {
		this.name = "Default Event Name";
		this.description = "default description";
		this.date = LocalDate.now();
		this.time = LocalTime.now();
		this.id = this.generateRandomId(); 
	}
	
	public Event(String name, String desc, LocalDate d, LocalTime t) {
		this.name = name;
		this.description = desc;
		this.date = d;
		this.time = t;
		this.id = this.generateRandomId();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}

	public LocalDate getDate() {
		return this.date;
	}

	public LocalTime getTime() {
		return this.time;
	}

	public int getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	public void setDate(LocalDate d) {
		this.date = d;
	}

	public void setTime(LocalTime t) {
		this.time = t;
	}
	
	public String toString() {
		String truncatedDescription = this.getDescription()
						.substring(0, Math.min(this.getDescription().length(), 10));
		return "[Event Id:" + this.getId() + ", Event Name:" + this.getName() + 
			   ",  Date:" + this.date + ", Desc Truncated: " +
			  	truncatedDescription + " ]"; 
	}

	private int generateRandomId() {
		return (new Random().nextInt(30000));
	}
}

