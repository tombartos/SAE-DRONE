package fr.univtln.infomath.dronsim.server.simulation.jme_messages;

import java.util.List;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneInitData;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineInitData;
import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;

/**
 * This class is used to send a handshake message from the server to the client.
 * It contains initialization data for drones, marine entities, and events.
 *
 * @author Tom BARTIER
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Serializable
public class Handshake2 extends AbstractMessage {
    private List<DroneInitData> dronesInitData;
    private int yourDroneId;

    private List<EntiteMarineInitData> entitesMarineInitData;
    private List<EvenementDTO> evenementsInitData;

}
