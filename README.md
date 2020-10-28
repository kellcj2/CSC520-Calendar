# CSC520-Calendar

I used to make to automate the stuff I was working with
Feel free to swap this for whatever works you
People dont usually use make for java files but yolo
Once we squeeze UI stuff in we can prob delete makefile

All of the class stuff is implemented with some new additions
The events array in calendar is completely private,
you can get in with some methods and even output a list for
some sequential needs with getAllEvents. Probably safer this way,
ids are abstracted away from design, trying to keep O(1) access
on Events for the calendar so I went with randomInt for now,
later on it can be hoisted into the Calendar or something
we shouldn't have any collisions though.

The driver gives a quick run down of how everything works in code

run:

make
make run or java ClassDriver
