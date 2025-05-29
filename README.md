# SDF Simulateur de Drones Français (French Drone Simulator)
This project is a submarine drone simulator written in Java by a group of students from the University of Toulon. The main goal was to make an API for a submarine drone simulator that runs with JMonkey Engine. In this project you will find a JMonkey Engine client/server application that runs the simulation, a REST API to connect to the simulation and manage it, and an implementation of a controler that uses Ardupilot and QGround Control to control the drone through MAVLink protocol. The application is designed so that it is easy to extend and add new features, such as new sensors, new drones, or new controlers.


# Installation
## Prerequisites
- JDK 21 or higher
- Maven
- A system running Linux for the client Ardupilot and QGround Control client

## Launching the server
Note : The server can run on both Linux and Windows, but the client with Ardupilot and QGround Control is only supported on Linux. The simulator can run on mode "cloud" or "local", the "cloud" mode means that the JMonkey Renderer of the client runs on the server machine and send the video stream to the client, while the "local" mode means that the JMonkey Renderer runs on the client machine and connects to the server to get the simulation data. The Game Master chose the mode individually for each pilot, but the server machine must have a GUI OS and a GPU to run the JMonkey Renderer in "cloud" mode.
1. If it is not already done, clone this repository
2. Open a terminal and navigate to the root directory of the project
3. Launch the server with the following command:

Linux:
   ```bash
   mvn exec:java -Dexec.mainClass="fr.univtln.infomath.dronsim.server.manager.Manager"
   ```

Windows:
```bash
   mvn exec:java "-Dexec.mainClass=fr.univtln.infomath.dronsim.server.manager.Manager"
```

The REST API server will start and listen on port 8080 by default.

## Launching the client with Ardupilot and QGround Control
