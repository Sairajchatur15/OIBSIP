# Task 1: Online Reservation System

## Objective

Build a desktop application that lets a user log in, reserve a train journey, retrieve a booking using its PNR, and cancel a reservation.

## Steps Performed

1. Created a Java Swing interface with Login, Book, and Cancel tabs.
2. Added SQLite tables for users, trains, and reservations.
3. Added seeded administrator and train records for initial use.
4. Implemented booking validation, automatic PNR creation, reservation lookup, and cancellation confirmation.

## Tools Used

- Java
- Java Swing
- JDBC
- SQLite and the SQLite JDBC driver

## Outcome

The application stores reservations locally and supports the full booking-to-cancellation flow. The default login is `admin` / `admin123`.

## Run

Ensure the SQLite JDBC driver is on the classpath, then compile and run:

```bash
javac ReservationSystem.java
java ReservationSystem
```
