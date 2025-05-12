package fr.univtln.infomath.dronsim.simulation.jmeMessages;

import java.util.List;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.univtln.infomath.dronsim.simulation.Drones.DroneInitData;
import com.jme3.math.Vector3f;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Serializable
public class Handshake2 extends AbstractMessage {
    private List<DroneInitData> dronesInitData;
    private int idMap;
    private int yourDroneId;
}
