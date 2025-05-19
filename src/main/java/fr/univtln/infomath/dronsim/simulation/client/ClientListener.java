package fr.univtln.infomath.dronsim.simulation.client;

import com.jme3.network.Message;
import com.jme3.network.MessageListener;

import fr.univtln.infomath.dronsim.simulation.jme_messages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.simulation.jme_messages.Handshake2;
import lombok.AllArgsConstructor;

import com.jme3.network.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
public class ClientListener implements MessageListener<Client> {
    private static final Logger log = LoggerFactory.getLogger(ClientListener.class);
    private SimulatorClient simulatorClient;

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

        log.warn("Client : received unknown message: " + message.getClass().getName());

    }
}
