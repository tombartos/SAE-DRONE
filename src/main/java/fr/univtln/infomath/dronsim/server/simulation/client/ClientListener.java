
package fr.univtln.infomath.dronsim.server.simulation.client;

import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.EntiteMarineDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.EvenementDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake2;
import lombok.AllArgsConstructor;
import com.jme3.network.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener class for handling messages received from the server in the drone
 * simulation.
 * <p>
 * This class implements {@link MessageListener} for {@link Client} and
 * processes different types of messages
 * such as {@link Handshake2}, {@link DroneDTOMessage},
 * {@link EvenementDTOMessage}, and {@link EntiteMarineDTOMessage}.
 * Depending on the message type, it delegates the handling to the appropriate
 * methods of {@link SimulatorClient}.
 * Unknown message types are logged as warnings.
 * </p>
 *
 * @author Tom BARTIER
 */

@AllArgsConstructor
public class ClientListener implements MessageListener<Client> {
    private static final Logger log = LoggerFactory.getLogger(ClientListener.class);
    private SimulatorClient simulatorClient;

    /**
     * Handles messages received from the server.
     * <p>
     * This method checks the type of the received message and processes it
     * accordingly:
     * <ul>
     * <li>If the message is a {@link Handshake2}, it initializes the environment
     * in {@link SimulatorClient}.</li>
     * <li>If the message is a {@link DroneDTOMessage}, it updates drone
     * information.</li>
     * <li>If the message is an {@link EvenementDTOMessage}, it updates events.</li>
     * <li>If the message is an {@link EntiteMarineDTOMessage}, it updates marine
     * entities.</li>
     * <li>For any other message type, it logs a warning.</li>
     * </ul>
     *
     * @param source  The client that sent the message.
     * @param message The received message.
     */
    public void messageReceived(Client source, Message message) {
        if (message instanceof Handshake2) {
            // HANDSHAKE MESSAGE
            Handshake2 handshake = (Handshake2) message;
            // log.info("Client #" + source.getId() + " received Handshake2 : "
            // + handshake.getDronesInitData().toString());
            simulatorClient.initEnv(handshake);
            return;
        }

        if (message instanceof DroneDTOMessage) {
            DroneDTOMessage DronePosMessage = (DroneDTOMessage) message;
            // log.info("Client #" + source.getId() + " received DronePosition : "
            // + DronePosMessage.getDronesInfos().toString());
            simulatorClient.updateDronesInfo(DronePosMessage.getDronesInfos());
            return;
        }
        if (message instanceof EvenementDTOMessage msg) {
            simulatorClient.updateEvenements(msg.getEvenements());
            return;
        }
        if (message instanceof EntiteMarineDTOMessage msg) {
            simulatorClient.updateEntitesMarine(msg.getEntites());

            return;
        }

        log.warn("Client : received unknown message: " + message.getClass().getName());

    }
}
