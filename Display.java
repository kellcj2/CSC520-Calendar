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

import javafx.scene.text.Font;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;

public class Display extends Application {
	private static Calendar cal;

	public Display() {}

	private Scene showEvents(Stage stage) {
		Button addButton = new Button("Add Event");

		EventHandler<ActionEvent> addEvent = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					Scene addScene = addEvent(stage);
					stage.setScene(addScene);
					stage.show();
				} 
			};

		EventHandler<ActionEvent> modifyEvent = new EventHandler<ActionEvent>() {
				@Override 
				public void handle(ActionEvent e) {
					Object o = e.getSource();
					if(o instanceof Button) {
						int id = ((int)((Button) o).getUserData());
						Scene modScene = modifyEvent(stage, id);
						stage.setScene(modScene);
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
			Event ev = allEvents.get(i);
			
			DateTimeFormatter f = DateTimeFormatter.ofPattern("dd LLLL, yyyy  ");
			Label lName = new Label(ev.getName());
			lName.setFont(new Font("Arial", 18));
			Label lDate = new Label(ev.getDate().format(f));
			Label lTime = new Label(ev.getTime().toString());
			Button modify = new Button("Modify");
			modify.setUserData(ev.getId()); // keep track of event id
			modify.setOnAction(modifyEvent);
			root.addRow(i+1, lName, lDate, lTime, modify);
		}
		
		return new Scene(root, 600, 600);		
	}

	private Scene addEvent(Stage stage) {
		System.out.println("add event");
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setPrefWidth(400);
		Label error = new Label();
		error.setTextFill(Color.RED);
		
		Button submit = new Button("Confirm");
		TextField name = new TextField();
		TextField desc = new TextField();
		DatePicker datePicker = new DatePicker(); // date picker
		// add AM/PM buttons
		ComboBox<String> hours = new ComboBox<String>();
		ObservableList<String> list = hours.getItems();
		for(int i = 1; i <= 12; i++) // add options to hours
			list.add(String.valueOf(i));
		
		ComboBox<String> minutes = new ComboBox<String>();
		list = minutes.getItems();
		for(int i = 0; i < 60; i += 15) // add to minutes
			list.add(String.valueOf(i));
		
		ComboBox<String> ampm = new ComboBox<String>();
		ampm.getItems().addAll("AM", "PM");
		
		// add the event, go back to Show Events screen
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
						// add event to calendar
						if(eAMPM == "PM") // convert to 24 hour clock
							eHour += 12;
						LocalTime eTime = LocalTime.of(eHour, eMin, 0);
						Event event = new Event(eName, eDesc, eDate, eTime);
						cal.addEvent(event);
						// go back to "Show Events"
						Scene sEvents = showEvents(stage);
						stage.setScene(sEvents);
						stage.show();
					}
				} 
			};

		submit.setOnAction(confirm);
		
		gp.addRow(0, new Label("Event Name:"), name);
		gp.addRow(1, new Label("Description:"), desc);
		gp.addRow(2, new Label("Date:"), datePicker);
		gp.addRow(3, new Label("Hour:"), hours);
		gp.addRow(4, new Label("Minute:"), minutes);
		gp.addRow(5, new Label("AM/PM:"), ampm);
		gp.addRow(6, submit, error);

		return new Scene(gp, 600, 600);
	}

	// same code as addEvent but with event values filled in
	// dont know of a better way to do it, default parameters dont exist in java
	private Scene modifyEvent(Stage stage, int id) {
		System.out.println("modify event");
		Event modEvent = cal.getAllEventsHashMap().get(id);
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setPrefWidth(400);
		Label error = new Label();
		error.setTextFill(Color.RED);
		
		Button submit = new Button("Confirm Modification");
		Button delete = new Button("Delete Event");
		TextField name = new TextField(modEvent.getName());
		TextField desc = new TextField(modEvent.getDescription());
		DatePicker datePicker = new DatePicker(modEvent.getDate()); // date picker
		// add AM/PM buttons
		ComboBox<String> hours = new ComboBox<String>();
		ObservableList<String> list = hours.getItems();
		for(int i = 1; i <= 12; i++) // add options to hours
			list.add(String.valueOf(i));

		
		ComboBox<String> minutes = new ComboBox<String>();
		list = minutes.getItems();
		for(int i = 0; i < 60; i += 15) // add to minutes
			list.add(String.valueOf(i));
		minutes.setValue(String.valueOf(modEvent.getTime().getMinute()));
		
		ComboBox<String> ampm = new ComboBox<String>();
		ampm.getItems().addAll("AM", "PM");
		if(modEvent.getTime().getHour() > 12) {
			hours.setValue(String.valueOf(modEvent.getTime().getHour() - 12));
			ampm.setValue("PM");
		} else {
			hours.setValue(String.valueOf(modEvent.getTime().getHour()));
			ampm.setValue("AM");
		}
		
		// modify the event, go back to Show Events screen
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
						// modify event
						if(eAMPM == "PM") // convert to 24 hour clock
							eHour += 12;
						LocalTime eTime = LocalTime.of(eHour, eMin, 0);
						Event event = new Event(eName, eDesc, eDate, eTime);
						cal.removeEvent(id); // remove old event
						cal.addEvent(event); // add modified event
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
				
		submit.setOnAction(confirm);
		delete.setOnAction(deleteAction);
		
		gp.addRow(0, new Label("Event Name:"), name);
		gp.addRow(1, new Label("Description:"), desc);
		gp.addRow(2, new Label("Date:"), datePicker);
		gp.addRow(3, new Label("Hour:"), hours);
		gp.addRow(4, new Label("Minute:"), minutes);
		gp.addRow(5, new Label("AM/PM:"), ampm);
		gp.addRow(6, submit, error);
		gp.addRow(7, delete);

		return new Scene(gp, 600, 600);
	}

	@Override
	public void start(Stage stage) {
        Scene baseScene = showEvents(stage);
		stage.setScene(baseScene);
		stage.setTitle("CSC 520 Calendar Application");
        stage.show();
	}

	public static void main(String [] args) {
		cal = new Calendar();
		cal.addEvent(new Event("test", "description...",
							   LocalDate.now(), LocalTime.of(6,0)));
		launch();
	}

	
}
