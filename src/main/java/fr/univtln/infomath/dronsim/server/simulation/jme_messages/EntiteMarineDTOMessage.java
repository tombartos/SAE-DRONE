package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineDTO;

/**
 * Message contenant une liste d'entités marines.
 * Utilisé pour la communication entre le serveur et le client.
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntiteMarineDTOMessage extends AbstractMessage {
    private List<EntiteMarineDTO> entites;
}
