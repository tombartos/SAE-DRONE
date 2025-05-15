package fr.univtln.infomath.dronsim.simulation.Drones;

import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/*
 * This class is used to send drone informations to the clients.
 * The server will send a list of all DroneDTO to all the clients every tick so
 * it needs to be lightweight.
 * The client will then update its local simulation with the new positions.
 */
@AllArgsConstructor
@NoArgsConstructor
@Serializable
@Getter
@Setter
public class DroneDTO {
    // Attributes are public for performance reasons
    public static List<DroneDTO> dronesDTOs = new ArrayList<>();
    public int id;
    public Vector3f position;
    public Quaternion angular;
    public int batteryLevel;

    public static DroneDTO createDroneDTO(Drone drone) {
        DroneDTO droneDTO = new DroneDTO(drone.getId(), drone.getPosition(), drone.getNode().getLocalRotation(),
                drone.getBatteryLevel());
        dronesDTOs.add(droneDTO);
        return droneDTO;
    }
}
