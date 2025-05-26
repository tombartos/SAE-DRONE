package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.*;
import java.util.List;

import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;

@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTOMessage extends AbstractMessage {
    private List<EvenementDTO> evenements;
}
