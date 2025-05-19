package fr.univtln.infomath.dronsim.simulation.jme_messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

/*
 * This class is used to send drone movement requests from the client to the server.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Serializable
public class DroneMovementRequestMessage extends AbstractMessage {
    // TEST MESSAGE
    private int droneId;
    private List<String> Directions;
    private List<Integer> MotorsSpeeds;
}
