package fr.univtln.infomath.dronsim.simulation.jmeMessages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is used to send a handshake message from the client to the server.
 * It contains the client ID.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Serializable
public class Handshake1 extends AbstractMessage {
    private int clientId;

}
