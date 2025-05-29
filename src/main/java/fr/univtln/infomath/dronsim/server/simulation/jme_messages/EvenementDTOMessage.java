package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.*;
import java.util.List;

import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;

/**
 * Network message containing a list of event data transfer objects.
 *
 * This message is used to synchronize active or newly added simulation events
 * (such as currents, marine entities, etc.) between the server and the client.
 * It allows the client to visualize or react to the same events that occur
 * in the server's simulation space.
 *
 * @author Emad BA GUBAIR
 * @version 1.0
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTOMessage extends AbstractMessage {
    private List<EvenementDTO> evenements;
}
