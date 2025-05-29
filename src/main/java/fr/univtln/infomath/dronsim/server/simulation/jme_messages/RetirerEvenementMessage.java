package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Network message used to request the removal of a simulation event.
 * <p>
 * This message is sent from the client to the server (or vice versa)
 * to indicate that an event, such as a current or marine entity,
 * should be removed from the simulation space.
 * </p>
 *
 * @author Emad BA GUBAIR
 * @version 1.0
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetirerEvenementMessage extends AbstractMessage {
    private int evenementId;
}
