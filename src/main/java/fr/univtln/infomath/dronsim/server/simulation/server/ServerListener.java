package fr.univtln.infomath.dronsim.server.simulation.server;

import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.HostedConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneMovementRequestMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake1;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ServerListener implements MessageListener<HostedConnection> {
    private static Logger log = LoggerFactory.getLogger(ServerListener.class);
    private SimulatorServer simulatorServer;

    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof Handshake1) {
            // HANDSHAKE MESSAGE
            Handshake1 handshake = (Handshake1) message;
            log.info("Server received Handshake1 : ClientId = " + handshake.getClientId());
            // send back a Handshake2 message to the client
            simulatorServer.sendHandshake2(handshake.getClientId(), source);
            return;
        }
        // if (message instanceof DroneMovementRequestMessage) {
        // DroneMovementRequestMessage MoveReq = (DroneMovementRequestMessage) message;
        // log.info("Server : received DroneMovementRequestMessage : " +
        // MoveReq.getDroneId() + " "
        // + MoveReq.getDirections().toString() + " " +
        // MoveReq.getMotorsSpeeds().toString());
        // simulatorServer.processDroneMovementRequest(MoveReq);
        // return;
        // }
        log.warn("Server : received unknown message: " + message.getClass().getName());
    }
}
