package fr.univtln.infomath.dronsim.simulation.Server;

import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.HostedConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.simulation.Drones.Drone;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneMovementRequestMessage;

public class ServerListener implements MessageListener<HostedConnection> {
    private static final Logger log = LoggerFactory.getLogger(ServerListener.class);

    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof DroneMovementRequestMessage) {
            // TEST MESSAGE
            DroneMovementRequestMessage MoveReq = (DroneMovementRequestMessage) message;
            log.info("Server received DronePosition : " + MoveReq.getDirections().toString());

        }
    }
}
