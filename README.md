# OccupancyMeetingsVirtualRooms
This is my Last summer internship project at Quantylix Firm

This is an Android application that manages the events of the company rooms.
The application displays(in real time) the room state during  a work day.

The user can book the room for meetings as much time as he need depending on the room states.
Green state is Free and Red state is Busy

the user must identify to acces the app logging the email and password of the wanted room.
All the users and rooms are managed into the company server which is a ZIMBRA server.


This app uses 3 libs which are:

ION lib for network calls
Jsoup for managing html streams
Ical4j for posting meetings into ZIMBRA Server using CalDav protocols
