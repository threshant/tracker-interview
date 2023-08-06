## Introduction

The GPS Tracker App utilizes the Android platform's location services to track user movement. It captures GPS coordinates along with the speed of movement, allowing users to monitor their routes, distances traveled, and other location-related information.

## Features

- Real-time tracking of user movement using GPS coordinates and speed.
- Records start and stop coordinates whenever the user begins or ends their movement.
- Works seamlessly in the background, even when the app is not actively in use.
- Provides a user-friendly interface to toggle tracking on and off.
- Displays a list of recorded locations with timestamps and other details.
- Enables users to view their movement history on a map interface.
- Prompts the user to enable GPS if it's turned off.
- Requests location permission to ensure accurate tracking.

## Usage

1. Launch the GPS Tracker App on your Android device.
2. Grant the app location permission when prompted.
3. Toggle the tracking switch to start or stop tracking your movement.
4. As you move, the app will record start and stop coordinates based on GPS speed.
5. You can view your location history and details within the app's interface.
6. The app continues tracking your movement even when running in the background.
7. To stop tracking, simply toggle the tracking switch off.

## How it works
1. It uses GPS to track the current speed, latitude and longitude
2. Keeps track of the initial data, if the speed is greater than 0.
3. Once the speed again reaches 0, it will push the gathered start and stop location the a firebase realtime database
4. Everything will be handled by the Foreground service
5. And the updated values will be displayed in a recyclerview with help of MutableliveData since it uses MVVM pattern

## Permissions

The GPS Tracker App requires the following permissions:

- Location: Used to access the device's GPS coordinates for tracking purposes.

Please note that the app will prompt you to grant the necessary permissions upon installation and first launch.
