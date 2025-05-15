package fr.univtln.infomath.dronsim.simulation.Drones;

import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import com.jme3.math.Vector3f;

@Serializable
@AllArgsConstructor
@NoArgsConstructor
@Getter
/**
 * This class represents the initial data of a drone. It contains all the
 * information about the drone. It is used to create a drone in the simulation.
 * We use this class to avoid sending the whole drone object to the client.
 */
public class DroneInitData {
    private int id; // Autoincrement (to be implemented) or gm choice (to be implemented)
    private int clientId;
    private DroneModel droneModel;
    private int batteryLevel;
    private Vector3f position;
    private Vector3f angular;
    private String name;
    private int weight;
    private List<Module> modules;

}
