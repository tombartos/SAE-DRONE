package fr.univtln.infomath.dronsim.simulation;

import com.jme3.math.Vector3f;

/*
 * This class is used to send drone informations to the clients.
 * The server will send a list of all DroneDTO to all the clients every tick so
 * it needs to be lightweight.
 * The client will then update its local simulation with the new positions.
 */
public class DroneDTO {
    int id;
    Vector3f position;

}
