package fr.univtln.infomath.dronsim.simulation.Drones;

import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import com.jme3.math.Vector3f;

@Serializable
@AllArgsConstructor
@NoArgsConstructor
public class DroneInitData {
    private int droneId;
    private Vector3f position;
    private Vector3f angular;
    private DroneModel model;
}
