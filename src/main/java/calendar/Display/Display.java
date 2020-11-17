import Calendar.*;
import NotificationSystem.*;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javafx.util.converter.LocalTimeStringConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.util.Callback;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.stage.Popup;
import javafx.scene.Scene;
import javafx.collections.ObservableList;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.geometry.Insets;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Display extends Application {
	private static Calendar cal;
	private static Stage globalStage;
	private static NotificationSystem notifySys;
	private DatePicker calendar;
	// size of day boxes in calendar view
	private final int CALDAY_X = 25;
	private final int CALDAY_Y = 100;

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
				calendar.hide();
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
				// go back to "Show Events"
				Scene sEvents = showEvents(globalStage, cal.getAllEvents());
				globalStage.setScene(sEvents);
				globalStage.show();
			}
		};
	// hander for showing the events in calendar view
	EventHandler<ActionEvent> showCalHandler = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				showCalendar(globalStage, cal.getAllEvents());
			}
		};
	// handler for save button
	EventHandler<ActionEvent> saveH = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Scene s = saveCalendar(globalStage);
				globalStage.setScene(s);
				globalStage.show();
			}
		};
	// handler for load button
	EventHandler<ActionEvent> loadH = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Scene l = loadCalendar(globalStage);
				globalStage.setScene(l);
				globalStage.show();
			}
		};

	
	public Display() {
		//cal = new Calendar();
		//notifySys = new NotificationSystem();

		//launch(); // loads start() function
	} 

	// converts LocalTime from 24 hour time to 12 hour String.
	// ex: 15:30 -> '03:30 PM'
	private String timeTo12Hour(LocalTime t) {
		String time = "";
		if(t.getHour() <= 12) // AM time, don't need to modify anything
			time = t.toString() + " AM";

		else {  // PM time, convert to 12 hour
			String minute = Integer.toString(t.getMinute());
			String hour = Integer.toString(t.getHour() - 12);
			if(minute.length() == 1) // add '0' in front of minute
				minute = "0" + minute;
			if(hour.length() == 1) // add '0' in front of hour
				hour = "0" + hour;
			
			time = hour + ":" +	minute + " PM";
		}
		return time;		
	}
	
	// creates the top menubar
	private MenuBar buildMenuBar(Stage stage) {
		// create menu items and add them
		Menu file = new Menu("File");
		MenuItem saveItem = new MenuItem("Save Calendar");
		MenuItem loadItem = new MenuItem("Load Calendar");
		MenuItem quitItem = new MenuItem("Quit");
		
		Menu view = new Menu("View");
		MenuItem eventsItem = new MenuItem("Events");
		MenuItem filter = new MenuItem("Filter");

		Menu options = new Menu("Options");
		MenuItem setEmail = new MenuItem("Set Email Address");

		// hander for quit item
		EventHandler<ActionEvent> quitH = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					System.exit(0);
				}
			};
		// handler for filter item
		EventHandler<ActionEvent> filterH = new EventHandler<ActionEvent>() {
				@Override 
				public void handle(ActionEvent e) {
					Scene viewBy = viewByPrompt(stage);
					stage.setScene(viewBy);
					stage.show();
				}			
			};
		EventHandler<ActionEvent> emailH = new EventHandler<ActionEvent>() {
				@Override 
				public void handle(ActionEvent e) {
					Scene c = configScene(stage);
					stage.setScene(c);
					stage.show();
				}
			};

		// add handlers for menu buttons
		saveItem.setOnAction(saveH);
		loadItem.setOnAction(loadH);
		quitItem.setOnAction(quitH);
		filter.setOnAction(filterH);
		eventsItem.setOnAction(backToShowEvents);
		setEmail.setOnAction(emailH);
		
		file.getItems().addAll(saveItem, loadItem, quitItem);
		view.getItems().addAll(eventsItem, filter);
		options.getItems().addAll(setEmail);
		
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(file, view, options);
		return menuBar;
	}

	// searches events for a specific name
	private Scene searchName(Stage stage, String name) {
		//calendar.hide();
		// search for events containing name
		if(name.length() == 0)
			return showEvents(stage, cal.getAllEvents());
		
		ArrayList<Event> find = new ArrayList<Event>();
		for(Event e : cal.getAllEvents()) {
			if(e.getName().toLowerCase().contains(name.toLowerCase()))
				find.add(e);
		}
		return showEvents(stage, find); // go to showevents screen
	}

	// builds the DatePicker, displays events in calendar view
	private void showCalendar(Stage stage, ArrayList<Event> events) {
		//DatePicker datePicker = new DatePicker();
		calendar.setShowWeekNumbers(false);
		calendar.setDayCellFactory(new Callback<DatePicker,DateCell>(){
				@Override
				public DateCell call(DatePicker param) {
					return new DateCell(){
						@Override
						public void updateItem(LocalDate item, boolean empty){
							super.updateItem(item, empty);
							StackPane cell_pane = new StackPane();
							ArrayList<Event> todaysEvents = cal.getEventByDate(item);
							Label dayNum =
								new Label(String.valueOf(item.getDayOfMonth()));
							// -----------------------------------------
							// add events of one day
							VBox vb = new VBox();
							vb.getChildren().add(dayNum);
							ScrollPane sp = new ScrollPane();
							for(Event e : todaysEvents) {
								Button view = new Button(e.getName());
								view.setUserData(e.getId());
								view.setOnAction(viewEventButton);
								vb.getChildren().add(view);
							}
							sp.setPrefSize(CALDAY_X, CALDAY_Y);
							sp.setContent(vb);
							cell_pane.getChildren().addAll(sp);
							// -----------------------------------------
							setGraphic(cell_pane);
							setText("");
						}
					};
				}
			});
		
		calendar.show(); // open up the calendar
	}

	// shows all events passed in
	private Scene showEvents(Stage stage, ArrayList<Event> allEvents) {
		Button addButton = new Button("Add Event");
		//Button viewByBut = new Button("View Events By");
		// handler for "Add Event" button
		EventHandler<ActionEvent> addEvent = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					Scene addScene = addModifyEvent(stage, -1);
					stage.setScene(addScene);
					stage.show();
				}
			};

		addButton.setOnAction(addEvent); // handle buttons
		
		BorderPane bp = new BorderPane();
		GridPane root = new GridPane();
		root.setAlignment(Pos.CENTER);
		root.setHgap(30);
		root.setVgap(10);
		bp.setTop(buildMenuBar(stage)); // add menubar to top of borderpane
		bp.setCenter(root); // events in center

		int currentRow = 0;
		root.addRow(currentRow++, addButton); 
		
		if(allEvents.size() == 0)
			root.addRow(currentRow++, new Label("No Events Found"));
		else
			root.addRow(currentRow++, new Label("Event"), new Label("Date"),
						new Label("Time"));

		// display all events in calendar
		for(int i=0; i < allEvents.size(); i++) {
			// get data from the current event
			Event ev = allEvents.get(i);
			DateTimeFormatter f = DateTimeFormatter.ofPattern("dd LLLL, yyyy  ");
			Label lName = new Label(ev.getName());
			lName.setFont(new Font("Arial", 18));
			Label lDate = new Label(ev.getDate().format(f));
			Label lTime = new Label(timeTo12Hour(ev.getTime()));			

			// create buttons for this event
			Button modify = new Button("Modify");
			modify.setUserData(ev.getId()); // keep track of event id in button
			modify.setOnAction(modifyEvent);
			Button view = new Button("View");
			view.setUserData(ev.getId());
			view.setOnAction(viewEventButton);

			// add this event to root
			root.addRow(currentRow++, lName, lDate, lTime, view, modify);
		}
		Scene s = new Scene(bp, 600, 600);
		//s.getStylesheets().add("style.css");
		return s;
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
		TextField name, desc, nMsg;
		DatePicker datePicker;
		//NotificationType nType = NotificationType.DESKTOP;
		// data for modifying event
		String eventName = "", eventDesc = "", notMsg = "";
		LocalDate nDate = null, eventDate = null;
		LocalTime nTime = null, eventTime = null;
		
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setPrefWidth(400);
		Label error = new Label(); // for invalid inputs
		error.setTextFill(Color.RED);

		//final Notification notification =
		//notifySys.getNotificationById(modEvent.getId());

		// notification type dropdown
		ComboBox<String> nType = new ComboBox<String>();
		NotificationType [] nTypes = NotificationType.values();
		ObservableList<String> list = nType.getItems();
		for(int i=0; i < nTypes.length; i++)
			list.add(nTypes[i].toString());
		
		if(id == -1) { // add a new event
			submit = new Button("Add Event");
			nType.setValue(NotificationType.DESKTOP.toString());

		} else { // modify existing event
			submit = new Button("Confirm Modification");
			eventName = modEvent.getName();
			eventDesc = modEvent.getDescription();
			eventDate = modEvent.getDate();
			eventTime = modEvent.getTime();
			
			Notification n = notifySys.getNotificationById(modEvent.getId());
			if(n != null) {
				nType.setValue(n.getNotifyType().toString());
				nDate = n.getDate();
				nTime = n.getTime();
				notMsg = n.getMessage();
			}
		}

		name = new TextField(eventName);
		desc = new TextField(eventDesc);
		nMsg = new TextField(notMsg);
		datePicker = new DatePicker(eventDate); // date picker

		// add AM/PM buttons
		ComboBox<String> hours = new ComboBox<String>();
		list = hours.getItems();
		for(int i = 1; i <= 12; i++) // add 1-12 to hours
			list.add(String.valueOf(i));

		ComboBox<String> minutes = new ComboBox<String>();
		list = minutes.getItems();
		for(int i = 0; i < 60; i += 5) // add 5, 10, 15, etc to mins
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
					String nMsgText = nMsg.getText();
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
					else if(nMsgText.isEmpty())
						error.setText("Notification Text is empty");
					else { // all good
						if(eAMPM == "PM" && eHour != 12) // convert to 24 hour clock
							eHour += 12;
						LocalTime eTime = LocalTime.of(eHour, eMin, 0);
						Event event = new Event(eName, eDesc, eDate, eTime);
						LocalTime nTime = eTime;
						LocalDate nDate = eDate;
						
						if(modEvent != null) { // if modifying event
							Notification oldN =
								notifySys.getNotificationById(modEvent.getId());
							if(oldN != null)
								notifySys.remove(oldN); // remove old notification
							cal.removeEvent(id); // remove old event
						}
						
						Notification newNotification = new Notification
							(nMsgText, event.getId(), nDate, nTime,
							 NotificationType.valueOf(nType.getValue()));

						cal.addEvent(event); // add event
						notifySys.add(newNotification); // add notification
						
						// go back to "Show Events"
						Scene sEvents = showEvents(stage, cal.getAllEvents());
						stage.setScene(sEvents);
						stage.show();
					}
				} 
			};

		EventHandler<ActionEvent> deleteAction = new EventHandler<ActionEvent>() { 
				@Override 
				public void handle(ActionEvent e) {
					cal.removeEvent(id); // remove event
					// go back to "Show Events"
					Scene sEvents = showEvents(stage, cal.getAllEvents());
					stage.setScene(sEvents);
					stage.show();
				}
			};

		delete = new Button("Delete Event");
		goBack = new Button("Go Back");
		
		// handle button events
		submit.setOnAction(confirm);
		delete.setOnAction(deleteAction);
		goBack.setOnAction(backToShowEvents);

		int row = 0;
		// show event details
		gp.addRow(row++, new Label("Event Name:"), name);
		gp.addRow(row++, new Label("Description:"), desc);
		gp.addRow(row++, new Label("Date:"), datePicker);
		gp.addRow(row++, new Label("Hour:"), hours);
		gp.addRow(row++, new Label("Minute:"), minutes);
		gp.addRow(row++, new Label("AM/PM:"), ampm);
		gp.addRow(row++, new Label("")); // space
		gp.addRow(row++, new Label("Notification Info"));
		gp.addRow(row++, new Label("Notification Type:"), nType);
		gp.addRow(row++, new Label("Notification Description:"), nMsg);
		gp.addRow(row++, submit, error);
		if(id != -1) // modifying event, add delete button
			gp.addRow(row++, delete);
		gp.addRow(row++, goBack);

		return new Scene(gp, 600, 600);
	}

	// view a single event
	private Scene viewEvent(Stage stage, int id) {
		final Event event = cal.getEvent(id);
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setHgap(40);
		gp.setVgap(10);
		
		Button goBack = new Button("Go Back");
		goBack.setOnAction(backToShowEvents); // handle button

		int row = 0;
		// add event details to GridPane
		gp.addRow(row++, new Label("Event Name:"), new Label(event.getName()));
		gp.addRow(row++, new Label("Description:"), new Label(event.getDescription()));
		gp.addRow(row++, new Label("Date:"), new Label(event.getDate().toString()));
		gp.addRow(row++, new Label("Time:"), new Label(timeTo12Hour(event.getTime())));
		// add notification details to GridPane
		gp.addRow(row++, new Label("")); // space
		Notification n = notifySys.getNotificationById(event.getId());
		if(n != null) {
			Label nInfo = new Label("Notification Info");
			gp.addRow(row++, nInfo);
			gp.addRow(row++, new Label("Notification Type:"),
					  new Label(n.getNotifyType().toString()));
			gp.addRow(row++, new Label("Notification Message:"),
					  new Label(n.getMessage()));
			gp.setColumnSpan(nInfo, 2);
			gp.setHalignment(nInfo, HPos.CENTER);
		}
		gp.addRow(row++, goBack);
		return new Scene(gp, 600, 600);
	}

	// gives options for viewing events:
	// calendar view
	// search name
	// by day, week, month
	private Scene viewByPrompt(Stage stage) {
		System.out.println("asdf");		
		calendar = new DatePicker(); // global DatePicker
		calendar.setVisible(false);
		System.out.println("asdfff");		
		calendar.getStylesheets().add // set css style
			(getClass().getResource("style.css").toExternalForm());

		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		gp.setVgap(15);
		gp.setHgap(5);

		Label error = new Label();
		error.setTextFill(Color.RED);

		DatePicker dateP = new DatePicker();

		ComboBox<String> options = new ComboBox<String>();
		options.getItems().addAll("Day", "Week", "Month");

		TextField searchText = new TextField();

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
		EventHandler<ActionEvent> searchH = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					calendar.hide();
					if(searchText.getText().length() > 0) {
						Scene search = searchName(stage, searchText.getText());
						stage.setScene(search);
						stage.show();
					}
				}
			};


		Button vBut = new Button("Submit");
		Button back = new Button("Go Back");
		Button calBut = new Button("Calendar View");
		Button searchBut = new Button("Search Event Name");
		vBut.setOnAction(submit);
		back.setOnAction(backToShowEvents);
		calBut.setOnAction(showCalHandler);
		searchBut.setOnAction(searchH);

		Label viewLabel = new Label("View Events by Day, Week, or Month");
		int rowNum = 0;

		// add everything to gridpane
		gp.addRow(rowNum++, calBut);
		gp.addRow(rowNum++, new Label("")); // gap
		gp.addRow(rowNum++, searchText, searchBut);
		gp.addRow(rowNum++, new Label("")); // gap
		gp.addRow(rowNum++, viewLabel);
		gp.addRow(rowNum++, options, dateP);
		gp.addRow(rowNum++, back, vBut);
		gp.addRow(rowNum++, error);
		gp.addRow(rowNum++, calendar); // invisible
		// align stuff
		gp.setColumnSpan(viewLabel, 2);
		gp.setHalignment(viewLabel, HPos.CENTER);
		gp.setColumnSpan(calBut, 2);
		gp.setHalignment(calBut, HPos.CENTER);
		gp.setHalignment(vBut, HPos.CENTER);
		gp.setHalignment(back, HPos.CENTER);
		// add menu bar
		BorderPane bp = new BorderPane(); 
		bp.setTop(buildMenuBar(stage));
		bp.setCenter(gp);

		return new Scene(bp, 600, 600);
	}

	// views events by day, week, or month
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

		BorderPane bp = new BorderPane(); // add menu bar
		bp.setTop(buildMenuBar(stage));
		bp.setCenter(gp);
		return new Scene(bp, 600, 600);
	}

	// saves calendar to '.cal' file
	private Scene saveCalendar(Stage stage) {
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);

		// create filechooser to save file
		FileChooser fc = new FileChooser();
		fc.setTitle("Select file to save to");
		fc.getExtensionFilters().addAll(new ExtensionFilter("Calendar Files", "*.cal"));
		File file = fc.showSaveDialog(stage);

		if(file == null) // no file selected
			return showEvents(globalStage, cal.getAllEvents()); // go back
		
		try {
			String filename = file.getName();
			String ext = filename.substring(filename.lastIndexOf(".") + 1,
												  filename.length());
			if(!ext.equals("cal")) // set extension to '.cal'
				filename += ".cal";
			file = new File(filename);
			FileOutputStream f = new FileOutputStream(file);
			ObjectOutputStream o = new ObjectOutputStream(f);
			o.writeObject(cal.getAllEventsHashMap()); // write hashmap to file
			o.writeObject(notifySys.getNotifications());
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

	// loads calendar from selected '.cal' file
	@SuppressWarnings("unchecked") // suppress warning for cast to hashmap and list
	private Scene loadCalendar(Stage stage) {
		GridPane gp = new GridPane();
		gp.setAlignment(Pos.CENTER);
		Label result = new Label("Succesfully read Calendar from file");

		// create filechooser to save file
		FileChooser fc = new FileChooser();
		fc.setTitle("Load Calendar File");
		fc.getExtensionFilters().addAll(new ExtensionFilter("Calendar Files", "*.cal"));
		File file = fc.showOpenDialog(stage);

		if(file == null) // no file selected
			return showEvents(globalStage, cal.getAllEvents()); // go back

		try {
			ObjectInputStream o =
				new ObjectInputStream(new FileInputStream(file));
			// read the hashmap from the file
			HashMap<Integer, Event> savedCal = (HashMap<Integer, Event>) o.readObject();
			cal = new Calendar(savedCal); // update cal
			
			// read notification system from file
			List<Notification> ns = (List<Notification>) o.readObject();
			notifySys.setNotifications(ns);

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

	// reads from the config file, returns true if successful
	private boolean loadConfig() {
		String filename = "config.txt";
		File f = new File(filename);
		if(f.exists()) {
			try {
				FileReader fr = new FileReader(filename);
				BufferedReader br = new BufferedReader(fr);
				String email = br.readLine();
				br.close();

				notifySys.setEmail(email);
			
			} catch (IOException e) {
				System.err.println("Error reading config file");
				System.exit(-1);
			}
			return true; // successfully loaded config file
			
		} else
			return false; // file does not exist
	}

	// set email address for notifications in 'config.txt'
	private Scene configScene(Stage stage) {
		VBox v = new VBox();
		v.setSpacing(20);
		v.setAlignment(Pos.CENTER);
		HBox h = new HBox();
		h.setSpacing(20);
		h.setAlignment(Pos.CENTER);
		
		TextField emailField = new TextField();
		Button confirm = new Button("Confirm");
		Label error = new Label("");
		error.setTextFill(Color.RED);
		
		h.getChildren().addAll(new Label("Set Email for Notifications"),
							   emailField);
		v.getChildren().addAll(h, confirm, error);

		EventHandler<ActionEvent> confirmConfig = new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent e) {
					String email = emailField.getText();
					if(email.isEmpty()) {
						error.setText("Enter email address");
					} else { 
						try { // try writing email to config file
							FileWriter fw = new FileWriter("config.txt");
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write(email);
							bw.close();
							// set notification system to email
							notifySys.setEmail(email);
							
						} catch(IOException ex) {
							System.err.println("Error writing config file");
							System.exit(-1);
						}
						// go to start scene
						stage.setScene(startScene(stage));
						stage.show();
					}
				}
			};
				
		confirm.setOnAction(confirmConfig);
		return new Scene(v, 600, 600);
	}

	// initial scene, option to create new calendar or load
	private Scene startScene(Stage stage) {
		VBox vb = new VBox(10);
		vb.setAlignment(Pos.CENTER);
		
		Button newC = new Button("Create New Calendar");
		Button loadC = new Button("Load Calendar");
		newC.setOnAction(backToShowEvents);
		loadC.setOnAction(loadH);
		
		vb.getChildren().addAll(newC, loadC);
		return new Scene(vb, 600, 600);
	}

	// javafx start function
	@Override
	public void start(Stage stage) {
		globalStage = stage;
		stage.setTitle("CSC 520 Calendar Application");
    calendar = new DatePicker();
		Scene baseScene;
		if(!loadConfig()) // if config file doesn't exist
			baseScene = configScene(stage); // create it
		else // config file loaded successfully
			baseScene = startScene(stage);

		stage.setScene(baseScene);
		stage.show();
	}

	public static void main(String [] args) {
		cal = new Calendar();
		notifySys = new NotificationSystem();

		// test
		/*
		cal.addEvent(new Event("test1", "description...",
							   LocalDate.now(), LocalTime.of(6,0)));
		cal.addEvent(new Event("test2", "description...",
							   LocalDate.now(), LocalTime.of(6,30)));
		cal.addEvent(new Event("test3", "description...",
							   LocalDate.now(), LocalTime.of(7,30)));
		*/
		launch();
	}

	
}
