package fr.univtln.infomath.dronsim.simulation.jmeMessages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * This class is used to send drone movement requests from the client to the server.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Serializable
public class DroneMovementRequestMessage extends AbstractMessage {
    // TODO : implement this class
    private String test;
}
