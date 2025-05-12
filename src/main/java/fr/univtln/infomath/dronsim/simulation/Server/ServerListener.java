package fr.univtln.infomath.dronsim.simulation.Server;

import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.HostedConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.simulation.Drones.Drone;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneMovementRequestMessage;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.Handshake1;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class ServerListener implements MessageListener<HostedConnection> {
    private static Logger log = LoggerFactory.getLogger(ServerListener.class);
    private SimulatorServer simulatorServer;

    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof Handshake1) {
            // HANDSHAKE MESSAGE
            Handshake1 handshake = (Handshake1) message;
            log.info("Server received Handshake1 : " + handshake.getClientId());
            // send back a Handshake2 message to the client
            simulatorServer.sendHandshake2(handshake.getClientId(), source);

        }
        if (message instanceof DroneMovementRequestMessage) {
            // TEST MESSAGE
            DroneMovementRequestMessage MoveReq = (DroneMovementRequestMessage) message;
            log.info("Server received DronePosition : " + MoveReq.getDirections().toString());
        }
    }
}
