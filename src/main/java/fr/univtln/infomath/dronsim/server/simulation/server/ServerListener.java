package fr.univtln.infomath.dronsim.server.simulation.server;

import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.HostedConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.AjoutEvenementMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake1;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.RetirerEvenementMessage;
import lombok.AllArgsConstructor;

/**
 * ServerListener is responsible for handling incoming network messages from
 * clients in the drone simulation server. It processes different types of
 * messages such as handshake requests, event addition, and event removal, and
 * delegates the appropriate actions to the SimulatorServer instance.
 * <p>
 * Supported message types:
 * <ul>
 * <li>{@link fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake1}
 * - Handles client handshake and responds with a handshake acknowledgment.</li>
 * <li>{@link fr.univtln.infomath.dronsim.server.simulation.jme_messages.AjoutEvenementMessage}
 * - Adds a new event to the simulation.</li>
 * <li>{@link fr.univtln.infomath.dronsim.server.simulation.jme_messages.RetirerEvenementMessage}
 * - Removes an event from the simulation.</li>
 * </ul>
 * For unknown message types, a warning is logged.
 *
 * @author Tom BARTIER
 * @see fr.univtln.infomath.dronsim.server.simulation.server.SimulatorServer
 */

@AllArgsConstructor
public class ServerListener implements MessageListener<HostedConnection> {
    private static Logger log = LoggerFactory.getLogger(ServerListener.class);
    private SimulatorServer simulatorServer;

    /**
     * Handles incoming messages from clients.
     *
     * @param source  The connection from which the message was received.
     * @param message The message received from the client.
     */
    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof Handshake1) {
            // HANDSHAKE MESSAGE
            Handshake1 handshake = (Handshake1) message;
            log.info("Server received Handshake1 : ClientId = " + handshake.getClientId());
            // send back a Handshake2 message to the client
            simulatorServer.sendHandshake2(handshake.getClientId(), source);
            return;
        }
        if (message instanceof AjoutEvenementMessage ajoutMsg) {
            simulatorServer.ajoutEvenement(ajoutMsg.getEvenement());
            return;
        } else if (message instanceof RetirerEvenementMessage retirerMsg) {
            simulatorServer.retirerEvenement(retirerMsg.getEvenementId());
            return;
        }

        log.warn("Server : received unknown message: " + message.getClass().getName());
    }
}
