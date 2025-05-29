package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneDTO;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Network message used to send a list of drone information in the simulation.
 *
 * This message wraps a list of {@link DroneDTO} objects that contain all
 * necessary data about the drones, such as their positions, statuses, and other
 * relevant attributes.
 *
 * It is part of the communication protocol based on jMonkeyEngine's networking
 * system.
 *
 * @author Tom BARTIER
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Serializable
public class DroneDTOMessage extends AbstractMessage {
    private List<DroneDTO> dronesInfos;
}
