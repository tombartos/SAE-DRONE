package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Message pour ajouter un événement à la simulation.
 * Il contient un objet EvenementDTO qui représente l'événement à ajouter.
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AjoutEvenementMessage extends AbstractMessage {
    private EvenementDTO evenement;
}
