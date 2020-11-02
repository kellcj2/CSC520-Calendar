import Calendar.Calendar;
import Calendar.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.util.converter.LocalTimeStringConverter;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.collections.ObservableList;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;

public class Display extends Application {
	private static Calendar cal;

	public Display() {}

	private Scene showEvents(Stage stage) {
		Button addButton = new Button("Add Event");

		// handler for "Add Event" button
		EventHandler<ActionEvent> addEvent = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					Scene addScene = addModifyEvent(stage, -1);
					stage.setScene(addScene);
					stage.show();
				}
			};

		// handler for "Modify Event" button, get eventId from button's userdata
		EventHandler<ActionEvent> modifyEvent = new EventHandler<ActionEvent>() {
				@Override 
				public void handle(ActionEvent e) {
					Object o = e.getSource();
					if(o instanceof Button) {
						int id = ((int)((Button) o).getUserData()); // get id from button
						Scene modScene = addModifyEvent(stage, id);
						stage.setScene(modScene);
						stage.show();
					}
				}				
			};

		// handler for viewing specific event
		EventHandler<ActionEvent> viewEventButton = new EventHandler<ActionEvent>() {
				@Override 
				public void handle(ActionEvent e) {
					Object o = e.getSource();
					if(o instanceof Button) {
						int id = ((int)((Button) o).getUserData()); // get id from button
						Scene viewScene = viewEvent(stage, id);
						stage.setScene(viewScene);
						stage.show();
					}					
				}
			};

		addButton.setOnAction(addEvent);
		
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.addRow(0, addButton);

		// display all events in calendar
		ArrayList<Event> allEvents = cal.getAllEvents();
		for(int i=0; i < cal.getNumEvents(); i++) {
			// get data from the current event
			Event ev = allEvents.get(i);
			DateTimeFormatter f = DateTimeFormatter.ofPattern("dd LLLL, yyyy  ");
			Label lName = new Label(ev.getName());
			lName.setFont(new Font("Arial", 18));
			Label lDate = new Label(ev.getDate().format(f));
			Label lTime = new Label(ev.getTime().toString());

			Button modify = new Button("Modify");
			modify.setUserData(ev.getId()); // keep track of event id in button
			modify.setOnAction(modifyEvent);

			Button view = new Button("View");
			view.setUserData(ev.getId());
			view.setOnAction(viewEventButton);

			// add this event to root
			root.addRow(i+1, lName, lDate, lTime, view, modify);
		}
		
		return new Scene(root, 600, 600);		
	}

	/* Name: addModifyEvent
	   Desc: creates scene for adding a new event or modifying
	         an existing one
	   Param: Stage stage - main stage for adding Scenes
	          int id - (If == -1), add a new Event to calendar.
			           Otherwise, lookup Event by id and modify it.
	   Return: a Scene for adding / modifying events			          
	 */
	private Scene addModifyEvent(Stage stage, int id) {
		final Event modEvent = cal.getEvent(id);
		Button submit, delete;
		TextField name, desc;
		DatePicker datePicker;
		// data for modifying event
		String eventName = "", eventDesc = "";
		LocalDate eventDate = null;
		LocalTime eventTime = null;
		
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setPrefWidth(400);
		Label error = new Label(); // for invalid inputs
		error.setTextFill(Color.RED);

		if(id == -1) { // add a new event
			submit = new Button("Add Event");
		} else { // modify existing event
			submit = new Button("Confirm Modification");
			eventName = modEvent.getName();
			eventDesc = modEvent.getDescription();			
			eventDate = modEvent.getDate();
			eventTime = modEvent.getTime();
		}

		name = new TextField(eventName);
		desc = new TextField(eventDesc);
		datePicker = new DatePicker(eventDate); // date picker
		// add AM/PM buttons
		ComboBox<String> hours = new ComboBox<String>();
		ObservableList<String> list = hours.getItems();
		for(int i = 1; i <= 12; i++) // add 1-12 to hours
			list.add(String.valueOf(i));

		ComboBox<String> minutes = new ComboBox<String>();
		list = minutes.getItems();
		for(int i = 0; i < 60; i += 15) // add 0,15,30,45 to minutes
			list.add(String.valueOf(i));
		
		ComboBox<String> ampm = new ComboBox<String>();
		ampm.getItems().addAll("AM", "PM"); // AM PM option
		
		if(eventTime != null) { // load time if modifying event
			minutes.setValue(String.valueOf(eventTime.getMinute()));
			if(modEvent.getTime().getHour() > 12) {
				hours.setValue(String.valueOf(eventTime.getHour() - 12));
				ampm.setValue("PM");
			} else {
				hours.setValue(String.valueOf(eventTime.getHour()));
				ampm.setValue("AM");
			}
		}
		
		// "Confirm" button handler: verify non-empty, go to Show Events Scene
		EventHandler<ActionEvent> confirm = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					String eName = name.getText();
					String eDesc = desc.getText();
					LocalDate eDate = datePicker.getValue();
					String eAMPM = ampm.getValue();
					int eHour = -1, eMin = -1;
					if(hours.getValue() != null) // convert to int
						eHour = Integer.parseInt(hours.getValue());
					if(minutes.getValue() != null)
						eMin = Integer.parseInt(minutes.getValue());

					if(eName.isEmpty()) // check for blank fields
						error.setText("Name is empty");
					else if(eDesc.isEmpty())
						error.setText("Description is empty");
					else if(eDate == null)
						error.setText("Date is empty");
					else if(eHour == -1)
						error.setText("Hour is empty");
					else if(eMin == -1)
						error.setText("Minute is empty");
					else if(eAMPM == null)
						error.setText("AM/PM is empty");
					else { // all good
						if(eAMPM == "PM") // convert to 24 hour clock
							eHour += 12;
						LocalTime eTime = LocalTime.of(eHour, eMin, 0);
						Event event = new Event(eName, eDesc, eDate, eTime);
						if(modEvent != null) // modifying event, remove old event
							cal.removeEvent(id);
						cal.addEvent(event); // add event
						
						// go back to "Show Events"
						Scene sEvents = showEvents(stage);
						stage.setScene(sEvents);
						stage.show();
					}
				} 
			};

		EventHandler<ActionEvent> deleteAction = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					cal.removeEvent(id); // remove event
					Scene sEvents = showEvents(stage); // go back to "Show Events"
					stage.setScene(sEvents);
					stage.show();
				}
			};
		
		delete = new Button("Delete Event");
		// handle button events
		submit.setOnAction(confirm);
		delete.setOnAction(deleteAction);
		// add rows to GridPane
		gp.addRow(0, new Label("Event Name:"), name);
		gp.addRow(1, new Label("Description:"), desc);
		gp.addRow(2, new Label("Date:"), datePicker);
		gp.addRow(3, new Label("Hour:"), hours);
		gp.addRow(4, new Label("Minute:"), minutes);
		gp.addRow(5, new Label("AM/PM:"), ampm);
		gp.addRow(6, submit, error);
		if(id != -1) // only delete if modifying existing event
			gp.addRow(7, delete);

		return new Scene(gp, 600, 600);
	}

	
	private Scene viewEvent(Stage stage, int id) {
		final Event event = cal.getEvent(id);
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(40);
		gp.setVgap(10);
		
		Button goBack = new Button("Go Back");
		EventHandler<ActionEvent> backToShowEvents = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					Scene sEvents = showEvents(stage); // go back to "Show Events"
					stage.setScene(sEvents);
					stage.show();
				}
			};

		goBack.setOnAction(backToShowEvents); // handle button

		// add event details to GridPane
		gp.addRow(0, new Label("Event Name:"),
				  new Label(event.getName()));
		gp.addRow(1, new Label("Description:"),
				  new Label(event.getDescription()));
		gp.addRow(2, new Label("Date:"),
				  new Label(event.getDate().toString()));
		gp.addRow(3, new Label("Time:"),
				  new Label(event.getTime().toString()));
		gp.addRow(4, goBack);
		
		return new Scene(gp, 600, 600);
	}
	
	
	@Override
	public void start(Stage stage) {
        Scene baseScene = showEvents(stage); // load the "Show Events" Scene
		stage.setScene(baseScene);
		stage.setTitle("CSC 520 Calendar Application");
        stage.show();
	}

	public static void main(String [] args) {
		cal = new Calendar();
		cal.addEvent(new Event("test", "description...",
							   LocalDate.now(), LocalTime.of(6,0)));
		launch(); // loads start() function
	}

	
}
