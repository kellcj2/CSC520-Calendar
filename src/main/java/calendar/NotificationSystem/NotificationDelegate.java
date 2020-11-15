package NotificationSystem;

import NotificationSystem.Notification;
//import NotificationSystem.ParameterStringBuilder;

import java.awt.*;
import java.awt.TrayIcon.MessageType;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import java.io.*;

public class NotificationDelegate {
    
    //Could maybe do some setup here if we change how desktop notifs work.
    public NotificationDelegate() {}

    //Execute desktop notification with notification object 
    void notifyDesktop(Notification n) {
        if(SystemTray.isSupported() == false)  {
            //TODO: What do we do if its not supported?
            //AWT is supported on Linux/Windows/Mac
            System.err.println("System tray not supported!");
        }

        //Following code borrowed from stack overflow boggers
        //https://stackoverflow.com/questions/34490218/how-to-make-a-windows-notification-in-java

        //Obtain only one instance of the SystemTray object
        SystemTray tray = SystemTray.getSystemTray();

        //If the icon is a file
        Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        //Alternative (if the icon is on the classpath):
        //Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("icon.png"));

        TrayIcon trayIcon = new TrayIcon(image, "Tray Demo");
        //Let the system resize the image if needed
        trayIcon.setImageAutoSize(true);
        //Set tooltip text for the tray icon
        trayIcon.setToolTip("System tray icon demo");
        
        //Amended here
        try {
            tray.add(trayIcon);
        } catch(AWTException e) {
           e.printStackTrace(); 
        }

        trayIcon.displayMessage("It is time for your event!", n.getMessage(), MessageType.INFO);
    }
    
    void notifyEmail(Notification n, String email) {
        //Build http client for sending request 
        HttpClient client = HttpClient.newBuilder().build();

        //Build requst with relevant data 
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:10000"))
            .POST(BodyPublishers.ofString(email + "," + n.getMessage()))
            .build();

        //Send asynchronous client request 
        client.sendAsync(request, BodyHandlers.discarding())
            .thenApply(HttpResponse::body)
            .thenAccept(System.out::println);
    }
}
