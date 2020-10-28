package Calendar;
import java.util.Date;
import java.util.Random;
public class Event {
	public String name;
	public String description;	
	public Date dateTime;
	private int id;
	
	public Event() {
		this.name = "Default Event Name";
		this.description = "";
		this.dateTime = new Date();
		this.id = this.generateRandomId(); 
	}
	
	public Event(String name, String desc, Date dt) {
		this.name = name;
		this.description = desc;
		this.dateTime = dt;
		this.id = this.generateRandomId();
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescription() {
		return this.description;
	}

	public Date getDateTime() {
		return this.dateTime;
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

	public void setDate(Date dt) {
		this.dateTime = dt;
	}
	
	public String toString() {
		String truncatedDescription = this.getDescription()
						.substring(0, Math.min(this.getDescription().length(), 10));
		return "[Event Id:" + this.getId() + ", Event Name:" + this.getName() + 
			   ",  Date:" + this.dateTime.toString() + ", Desc Truncated: " +
			  	truncatedDescription + " ]"; 
	}

	private int generateRandomId() {
		return (new Random().nextInt(30000));
	}
}

