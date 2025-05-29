package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.jme3.math.Vector3f;

/**
 * Represents initialization data for a physical event in the simulation.
 *
 * This data structure is used to transmit event information between the server
 * and client, especially during the initialization or synchronization phase.
 *
 * @author Ba gubair
 * @version 1.0
 */
@Serializable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EvenementInitData {
    private int id;
    private Vector3f zoneCenter;
    private Vector3f zoneSize;
    private String type;
    private Vector3f direction;
    private float intensite;

}
