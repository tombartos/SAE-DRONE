package fr.univtln.infomath.dronsim.simulation.Drones;

import java.util.List;

import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.jme3.math.Vector3f;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Serializable
/**
 * * This class represents a drone model. It contains all the the informations
 * about the drone model. It is used to create a drone in the simulation.
 */
public class DroneModel {
    private String name;
    private int initialWeight;
    private String model3DPath;
    private int nbMotors;
    private int motorMaxSpeed;
    private List<Vector3f> initialThrusterVecs;
    private List<Vector3f> initialThrusterLocalPosition;

}
