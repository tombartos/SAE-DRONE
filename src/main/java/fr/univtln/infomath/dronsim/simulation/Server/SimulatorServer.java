package fr.univtln.infomath.dronsim.simulation.Server;

import java.io.IOException;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.system.JmeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.simulation.Client.SimulatorClient;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneMovementRequestMessage;

public class SimulatorServer extends SimpleApplication {
    private static final int SERVER_PORT = 6143; // Default JME server port
    private static final Logger log = LoggerFactory.getLogger(SimulatorClient.class);

    public static void main(String[] args) {
        SimulatorServer app = new SimulatorServer();
        app.start(JmeContext.Type.Headless); // headless type for servers!
    }

    /**
     * This method is called to register the serializable classes used in the
     * application.
     */
    public void initializeSerializables() {
        Serializer.registerClass(DroneDTOMessage.class);
        Serializer.registerClass(DroneMovementRequestMessage.class);
    }

    @Override
    public void simpleInitApp() {
        try {
            Server myServer = Network.createServer(SERVER_PORT);
            initializeSerializables();
            // TODO: verif si je dois faire un nouveau listener pour chaque message
            myServer.addMessageListener(new ServerListener(), DroneDTOMessage.class);
            myServer.start();
            log.info("Server started on port " + SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
