<p align="center">
  <img src="https://raw.githubusercontent.com/rslaanfareed/RideBookingSystem/main/ride.gif" width="600"/>
</p>
# RideFlow - Smart Ride Booking System

A Java Swing ride-booking application that simulates a modern ride-hailing service while demonstrating layered architecture and classic design patterns.

https://raw.githubusercontent.com/rslaanfareed/RideBookingSystem/main/ride.gif

## Features

- **Passenger Registration**: Create a passenger profile with name, email, phone, and wallet balance.
- **Modern Booking Flow**: Enter pickup/dropoff, use quick location shortcuts, preview fare, see ETA, and confirm booking.
- **Wallet Safety**: Booking checks wallet balance before confirming a ride.
- **Wallet Top-Up**: Add funds from Settings.
- **Ride Types**: Choose Standard or Premium rides.
- **Driver Assignment**: Assigns the highest-rated available driver for the selected ride type.
- **Ride Status and History**: Search rides, view status, complete trips, and rate drivers.
- **Dynamic Pricing**: Switch between Standard and Surge pricing with the Strategy pattern.
- **System Statistics**: Review passengers, drivers, ride count, recent rides, and driver availability.
- **Modern Swing Theme**: Shared colors, fonts, buttons, cards, and cleaner navigation through `AppTheme`.

## Architecture

### Layered Architecture

1. **Presentation Layer**
   - `controller/`: Swing screens for login, dashboard, ride request, ride status, settings, and statistics.
   - `ui/AppTheme.java`: Shared Swing theme helper.
   - `ui/ConsoleUI.java`: Legacy console interface.

2. **Business Logic Layer**
   - `RideBookingService.java`: Core ride booking workflow.
   - `FareCalculationService.java`: Fare strategy coordination.
   - `DriverManagementService.java`: Driver availability and assignment.
   - `PassengerManagementService.java`: Passenger registration and lookup.

3. **Data Layer**
   - `Passenger.java`
   - `Driver.java`
   - `Ride.java`
   - `RideType.java`
   - `RideStatus.java`

## Design Patterns

- **Factory Pattern**: `RideFactory` and `DriverFactory` create domain objects with generated IDs.
- **Observer Pattern**: Ride status observers receive notifications when ride state changes.
- **Strategy Pattern**: Standard and surge fare strategies can be switched at runtime.

## Project Structure

```text
src/main/java/com/ridebooking/
|-- RideBookingApp.java
|-- controller/
|-- factory/
|-- model/
|-- observer/
|-- service/
|-- strategy/
`-- ui/
```

## Running the Application

### Compile

```bash
javac -d build/classes src/main/java/com/ridebooking/**/*.java
```

On PowerShell, this form is reliable:

```powershell
javac -d build/classes (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
```

### Run

```bash
java -cp build/classes com.ridebooking.RideBookingApp
```

## Usage Guide

1. Register a passenger profile.
2. Open the dashboard and choose **Book Ride**.
3. Enter route details, select ride type, and review estimated fare/ETA.
4. Confirm the ride to assign a driver.
5. Complete the ride and rate the driver.
6. Use **Settings** to switch pricing strategy or add wallet funds.

## Sample Data

- **Standard Drivers**: Ahmed Ali, Fatima Khan
- **Premium Drivers**: Usman Hassan, Aisha Malik
- **Initial Passenger Wallet**: $1000

## Pricing

- **Standard Ride**: Base fare + $10/km
- **Premium Ride**: Higher base fare + $10/km
- **Surge Pricing**: 1.5x base fare + $12/km

## Suggested Next Features

- Driver-side dashboard for accepting and completing rides.
- Saved places such as Home, Work, University, and recent dropoffs.
- Promo codes or loyalty discounts using another Strategy implementation.
- Ride cancellation with cancellation fee rules.
- Persistent storage using files or a database so users and rides survive app restarts.
- Admin panel for adding/removing drivers and monitoring earnings.
