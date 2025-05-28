package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Network message used to request the addition of an event into the simulation.
 *
 * This message wraps an {@link EvenementDTO} that contains all necessary data
 * to recreate the event on the receiving side (typically from client to
 * server).
 *
 * It is a part of the communication protocol based on jMonkeyEngine's
 * networking system.
 *
 * @author Ba gubair
 * @version 1.0
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AjoutEvenementMessage extends AbstractMessage {
    private EvenementDTO evenement;
}
