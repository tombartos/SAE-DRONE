package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Message pour retirer un événement de la simulation.
 * Il contient l'identifiant de l'événement à retirer.
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RetirerEvenementMessage extends AbstractMessage {
    private int evenementId;
}
