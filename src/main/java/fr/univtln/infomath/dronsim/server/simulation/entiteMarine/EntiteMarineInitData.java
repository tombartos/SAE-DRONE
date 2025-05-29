package fr.univtln.infomath.dronsim.server.simulation.entiteMarine;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Initialization data for a marine entity.
 * This class is used to transfer all the necessary information
 * to create a marine entity on the server or client during the initial
 * handshake.
 *
 * @author Emad BA GUBAIR
 * @version 1.0
 */
@Serializable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntiteMarineInitData {
    private int id;
    private String type;
    private String modelPath;
    private Vector3f position;
    private Vector3f direction;
    private float speed;
}
