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
 * Network message containing a list of marine entity data transfer objects.
 *
 * This message is typically used to synchronize the marine entities
 * between the server and the client in the simulation environment.
 * It allows the client to display or update marine entities based on
 * the state maintained on the server side.
 *
 * @author Ba gubair
 * @version 1.0
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntiteMarineDTOMessage extends AbstractMessage {
    private List<EntiteMarineDTO> entites;
}
