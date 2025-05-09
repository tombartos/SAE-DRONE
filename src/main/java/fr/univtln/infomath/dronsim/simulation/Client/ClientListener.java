package fr.univtln.infomath.dronsim.simulation.Client;

import com.jme3.network.Message;
import com.jme3.network.MessageListener;

import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneDTOMessage;
import com.jme3.network.Client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientListener implements MessageListener<Client> {
    private static final Logger log = LoggerFactory.getLogger(ClientListener.class);

    public void messageReceived(Client source, Message message) {
        if (message instanceof DroneDTOMessage) {
            DroneDTOMessage DronePosMessage = (DroneDTOMessage) message;
            log.info("Client #" + source.getId() + " received DronePosition : "
                    + DronePosMessage.getDronesInfos().toString());

        }
    }
}
