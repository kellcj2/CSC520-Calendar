#define compiler and compiler flag variables
JFLAGS = -g
JC = javac

.SUFFIXES: .java. .class

.java.class:  ; $(JC) $(JFLAGS) $*.java

#Classes is a macro for the java files and folders
#we add
CLASSES = \
		Calendar/Event.java\
		Calendar/Calendar.java\
		ClassDriver.java\

#Set default target for make
default: classes

#This target entry uses Suffix replacement within a macro:
# $(name:string1=string2) -> ClassDriver.java -> ClassDriver.class
#	
classes: $(CLASSES:.java=.class)

#Do literal substition for *.class and generate targets for them 
%.class: %.java
	$(JC) $(JFLAGS) $<

#RM is a predefined macro in make (RM = rm -f)
#Here we use the predefined marcro RM, remove all *.class in the
#current directory, then use suffix substitution to remove .class
#in sub directories. 
clean:
	$(RM) *.class $(CLASSES:.java=.class)

#Feel free to swap this for whatever works you
#People dont usually use make for java files but yolo
#Once we squeeze UI stuff in we can prob delete this
run:
	java ClassDriver
