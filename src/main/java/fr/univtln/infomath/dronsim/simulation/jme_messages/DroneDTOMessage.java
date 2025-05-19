package fr.univtln.infomath.dronsim.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import fr.univtln.infomath.dronsim.simulation.drones.DroneDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Serializable
public class DroneDTOMessage extends AbstractMessage {
    private List<DroneDTO> dronesInfos;
}
