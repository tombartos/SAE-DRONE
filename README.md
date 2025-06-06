# Simulateur de Drone Français (French Drone Simulator)
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

3. Put the `data` directory at the root of the project (the directory where the `pom.xml` file is located).

4. Compile the project with the following command:
   ```bash
   mvn clean install
   ```

5. Launch the server with the following command:

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

Note: The client for the Pilots is only supported on Linux (WSL works if you select "cloud" mode). GMs and observers can use both Linux and Windows.

Requirements:
- Python 3.8 or higher
- A Linux system (Ubuntu is recommended) or Windows (WSL works, but native Windows is not recommended)
1. If it is not already done, clone this repository

2. Open a terminal and navigate to the root directory of the project

3. If you are using Ubuntu, you can use our install script :
   ```bash
   chmod +x install.sh
   ./install.sh
   ```
   If you are using another Linux distribution, you can look at the script and install the required packages manually.

4. Launch Ardupilot with the following command (you'll only need to do this once to configure it):
   ```bash
   source ./venv/bin/activate
   cd ardupilot
   ./Tools/autotest/sim_vehicle.py -v ArduSub --out=udp:127.0.0.1:14550 --console --map
   ```

5. Once the install is done and Ardupilot launch, use those commands in the Ardupilot terminal, that will let ardupilot read the gps position from the server :
    - Change the gps type to mavlink
    ```
    param set GPS1_TYPE 14
    ```
    - Change the captor for altitude reading to gps
    ```
    param set EK3_SRC1_POSZ 3
    ```

6. If it's not already done, put the `data` directory at the root of the project (the directory where the `pom.xml` file is located).

7. Compile the project with the following command:
   ```bash
   mvn clean install
   ```

8. Launch the client with the following command:
   ```bash
   mvn exec:java -Dexec.mainClass="fr.univtln.infomath.dronsim.client.launcher.App" -Dexec.args="http://SERVER_IP:8080/api/v1/"
   ```
   Where `SERVER_IP` is the IP address of the server machine. If you are running the server on the same machine, you can use `localhost`.

  Note for WSL: if you run the server on your Windows native system (wich is fine) and wants to connect with the client on WSL you must use the IP address of the Windows system in the WSL subnetwork , not `localhost`. You can find this IP address by running `ipconfig` in the Powershell terminal and looking for the `WSL (Hyper-V firewall)` interface, `IPv4 address`.

  IMPORTANT NOTE: In the actual state of the project, you might have issues to connect and run the simulator properly if they are not in the same subnetwork. If you have issues, try to run the server and the client on the same machine (localhost) or in the same subnetwork.

# TEST ACCOUNTS
As the admin is not implemented yet, you can use the following test accounts to connect to the simulator:
- **Pilots**:
   - Username: `Bob35` Password: `hunter42`

   - Username: `Alice42` Password: `hunter43`

- **Observers**:
   - Username: `Charlie21` Password: `hunter44`

- **Game Masters**:
   - Username: `Diana99` Password: `hunter45`

- **Admins (can connect but can't do anything)**:
   - Username: `Eve88` Password: `hunter46`

# How to use the simulator
1. Launch the server

2. Launch the client and login as GM.

3. Add the pilots you want to the simulation. You can add as many pilots as you want, but you must have at least one pilot to start the simulation.

4. Start the simulation by clicking on the "Start Simulation" button.

5. Launch the client and login as a pilot.

6. Click on the "Start Simulation" button to connect to the simulation, this will automatically launch Ardupilot, QGround Control and the JMonkey Renderer (server side or client side depending on the mode selected by the GM).

7. You can now control your drone using the QGround Control interface, DON'T FORGET TO ARM YOUR DRONE BEFORE TAKING OFF, otherwise you won't be able to take off.

While the simulation is running, GMs can add or remove events in real time through their launcher (real time drone addition is not supported yet, you must restart the simulation to add a new drone).
Obervers can watch the simulation in real time, but they can't interact with it through their launchers.
