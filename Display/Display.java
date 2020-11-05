import Calendar.Calendar;
import Calendar.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.util.converter.LocalTimeStringConverter;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.collections.ObservableList;

import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Display extends Application {
	private static Calendar cal;
	private static Stage globalStage;

	// handler for "Modify Event" button, get eventId from button's userdata
	EventHandler<ActionEvent> modifyEvent = new EventHandler<ActionEvent>() {
			@Override 
			public void handle(ActionEvent e) {
				Object o = e.getSource();
				if(o instanceof Button) {
					int id = ((int)((Button) o).getUserData()); // get id from button
					Scene modScene = addModifyEvent(globalStage, id);
					globalStage.setScene(modScene);
					globalStage.show();
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
					Scene viewScene = viewEvent(globalStage, id);
					globalStage.setScene(viewScene);
					globalStage.show();
				}					
			}
		};

	// go back to main screen: "Show Events"
	EventHandler<ActionEvent> backToShowEvents = new EventHandler<ActionEvent>() { 
			@Override 
			public void handle(ActionEvent e) {
				Scene sEvents = showEvents(globalStage); // go back to "Show Events"
				globalStage.setScene(sEvents);
				globalStage.show();
			}
		};

	public Display() {} // empty constructor

	private Scene showEvents(Stage stage) {
		Button addButton = new Button("Add Event");
		Button viewByBut = new Button("View Events By");
		Button saveBut = new Button("Save Calendar");
		Button loadBut = new Button("Load Calendar");

		// handler for "Add Event" button
		EventHandler<ActionEvent> addEvent = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					Scene addScene = addModifyEvent(stage, -1);
					stage.setScene(addScene);
					stage.show();
				}
			};
		// handler for viewing events by day/week/month
		EventHandler<ActionEvent> viewEventsByBut = new EventHandler<ActionEvent>() {
				@Override 
				public void handle(ActionEvent e) {
					Scene viewBy = viewByPrompt(stage);
					stage.setScene(viewBy);
					stage.show();
				}			
			};
		// handler for save button
		EventHandler<ActionEvent> saveCal = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					Scene s = saveCalendar(stage);
					stage.setScene(s);
					stage.show();
				}
			};
		// handler for load button
		EventHandler<ActionEvent> loadCal = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					Scene l = loadCalendar(stage);
					stage.setScene(l);
					stage.show();
				}
			};

		addButton.setOnAction(addEvent); // handle buttons
		viewByBut.setOnAction(viewEventsByBut);
		saveBut.setOnAction(saveCal);
		loadBut.setOnAction(loadCal);

		BorderPane bp = new BorderPane();
		GridPane root = new GridPane();
		HBox hb = new HBox();

		root.setAlignment(Pos.CENTER);
		root.setHgap(20);
		hb.setSpacing(15);
		hb.getChildren().addAll(addButton, viewByBut,
								saveBut, loadBut); // buttons in top left
		
		bp.setTop(hb);
		bp.setCenter(root); // events in center
		bp.setMargin(hb, new Insets(20, 0, 0, 20)); // top left margin

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
			root.addRow(i, lName, lDate, lTime, view, modify);
		}

		return new Scene(bp, 600, 600);		
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
		Button submit, delete, goBack;
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
				if(modEvent.getTime().getHour() == 12) // noon
					ampm.setValue("PM");
				else
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
						if(eAMPM == "PM" && eHour != 12) // convert to 24 hour clock
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
		/*
		EventHandler<ActionEvent> backToShowEvents = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					Scene sEvents = showEvents(stage); // go back to "Show Events"
					stage.setScene(sEvents);
					stage.show();
				}
			};
		*/
		delete = new Button("Delete Event");
		goBack = new Button("Go Back");
		
		// handle button events
		submit.setOnAction(confirm);
		delete.setOnAction(deleteAction);
		goBack.setOnAction(backToShowEvents); // handle button
		
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
		gp.addRow(8, goBack);

		return new Scene(gp, 600, 600);
	}

	private Scene viewEvent(Stage stage, int id) {
		final Event event = cal.getEvent(id);
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(40);
		gp.setVgap(10);
		
		Button goBack = new Button("Go Back");
		goBack.setOnAction(backToShowEvents); // handle button

		// add event details to GridPane
		gp.addRow(0, new Label("Event Name:"), new Label(event.getName()));
		gp.addRow(1, new Label("Description:"), new Label(event.getDescription()));
		gp.addRow(2, new Label("Date:"), new Label(event.getDate().toString()));
		gp.addRow(3, new Label("Time:"), new Label(event.getTime().toString()));
		gp.addRow(4, goBack);
		
		return new Scene(gp, 600, 600);
	}
	
	private Scene viewByPrompt(Stage stage) {
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setVgap(15);

		Label error = new Label();
		error.setTextFill(Color.RED);
		
		DatePicker dateP = new DatePicker();
		
		ComboBox<String> options = new ComboBox<String>();
		options.getItems().addAll("Day", "Week", "Month");


		EventHandler<ActionEvent> submit = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					if(options.getValue() == null)
						error.setText("Select an option");
					else if(dateP.getValue() == null)
						error.setText("Please select a date");
					else { // all good
						Scene viewByScene = viewBy(stage, options.getValue(),
												   dateP.getValue());
						stage.setScene(viewByScene);
						stage.show();
					}
				}				
			};

		Button vBut = new Button("Submit");
		Button back = new Button("Go Back");
		vBut.setOnAction(submit);
		back.setOnAction(backToShowEvents);
		
		gp.addRow(0, new Label("View Events by Day, Week, or Month"));
		gp.addRow(1, options, dateP);
		gp.addRow(2, back, vBut);
		gp.addRow(3, error);
		return new Scene(gp, 600, 600);
	}

	private Scene viewBy(Stage stage, String by, LocalDate d) {
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		ArrayList<Event> events = new ArrayList<Event>();
		String viewLabelText = "Viewing all Events for ";
		
		switch(by) {
		case "Day":
			viewLabelText += "Day";
			events = cal.getEventsForDay(d.getMonthValue(), d.getDayOfMonth());
			break;
		case "Week":
			viewLabelText += "Week";
			events = cal.getWeekOfEventsFor(d.getMonthValue(), d.getDayOfMonth());
			break;
		case "Month":
			viewLabelText += "Month";
			events = cal.getEventsForMonth(d.getMonthValue());
			break;
		default:
			System.out.println("uh oh");
			System.exit(-1);
			break;
		}

		Label l = new Label(viewLabelText + " of " + d.toString());
		gp.addRow(0, l);
		gp.addRow(1, new Label("")); // hack
		gp.setColumnSpan(l, 5);
		
		// loop through events and display them
		for(int i=0; i < events.size(); i++) {
			// get data from the current event
			Event ev = events.get(i);
			DateTimeFormatter f = DateTimeFormatter.ofPattern("dd LLLL, yyyy  ");
			Label lName = new Label(ev.getName());
			lName.setFont(new Font("Arial", 18));
			Label lDate = new Label(ev.getDate().format(f));
			Label lTime = new Label(ev.getTime().toString());

			Button view = new Button("View");
			view.setUserData(ev.getId());
			view.setOnAction(viewEventButton);

			Button modify = new Button("Modify");
			modify.setUserData(ev.getId());
			modify.setOnAction(modifyEvent);

			// add this event to root
			gp.addRow(i+2, lName, lDate, lTime, view, modify);
		}

		Button back = new Button("Go Back");		
		back.setOnAction(backToShowEvents);
		gp.addRow(events.size()+2, back);
		gp.setHgap(20);
		
		return new Scene(gp, 600, 600);
	}

	private Scene saveCalendar(Stage stage) {
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);

		// create filechooser to save file
		FileChooser fc = new FileChooser();
		fc.setTitle("Select file to save to");
		fc.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
		File file = fc.showSaveDialog(stage);

		if(file == null) // no file selected
			return showEvents(globalStage); // go back
		
		try {
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(cal.getAllEventsHashMap()); // write hashmap to file
			o.close();
			f.close();
			
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("\nError saving Calendar to file");
			System.exit(-1);
		}

		Button back = new Button("Go Back");
		back.setOnAction(backToShowEvents);
		gp.addRow(0, new Label("Successfully saved to file"));
		gp.addRow(1, back);
		
		return new Scene(gp, 600, 600);
	}

	// suppress warning for cast to hashmap
	@SuppressWarnings("unchecked")
	private Scene loadCalendar(Stage stage) {
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		Label result = new Label("Succesfully read Calendar from file");

		// create filechooser to save file
		FileChooser fc = new FileChooser();
		fc.setTitle("Load Calendar File");
		fc.getExtensionFilters().addAll(new ExtensionFilter("All Files", "*.*"));
		File file = fc.showOpenDialog(stage);

		if(file == null) // no file selected
			return showEvents(globalStage); // go back

		try {
			ObjectInputStream o =
				new ObjectInputStream(new FileInputStream(file));
			// read the hashmap from the file
			HashMap<Integer, Event> savedCal = (HashMap<Integer, Event>) o.readObject();
			// update cal
			cal = new Calendar(savedCal);

		} catch (EOFException eof) {
			System.out.println("\nError with file");
			System.exit(-1);
		} catch (IOException | ClassNotFoundException ex) {
			//ex.printStackTrace();
			result = new Label("Error: Could not read Calendar");
		}

		Button back = new Button("Go Back");
		back.setOnAction(backToShowEvents);
		gp.addRow(0, result);
		gp.addRow(1, back);
			
		return new Scene(gp, 600, 600);
	}
	
	@Override
	public void start(Stage stage) {
		globalStage = stage;
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
