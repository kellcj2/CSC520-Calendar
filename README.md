# CSC520-Calendar

## Requirements
Maven 3.63 (other versions might work, tested only on 3.63)

## Download:
JavaFX requires different libraries based on the platform, so multiple jar files are provided.
(The alternative is to put all libraries in 1 jar file, but filesize becomes an issue)

* Windows Download: https://github.com/kellcj2/CSC520-Calendar/releases/download/1.0/calendar-1.0-WIN.jar
* Linux Download: https://github.com/kellcj2/CSC520-Calendar/releases/download/1.0/calendar-1.0-LINUX.jar

## Building:
 
* mvn clean
  * cleans ./target directory
* mvn compile
  * compiles the java source files
* mvn javafx:run 
  * runs the application
* mvn package    
  * creates .jar file in ./target
* mvn test       
  * runs test functions in ./src/test/java/calendar/CalTest.java
