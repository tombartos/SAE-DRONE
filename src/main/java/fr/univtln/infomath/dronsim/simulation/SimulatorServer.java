package fr.univtln.infomath.dronsim.simulation;

import java.io.IOException;

import com.jme3.app.SimpleApplication;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.system.JmeContext;

public class SimulatorServer extends SimpleApplication {
    private static final int SERVER_PORT = 6143; // Default JME server port

    public static void main(String[] args) {
        SimulatorServer app = new SimulatorServer();
        app.start(JmeContext.Type.Headless); // headless type for servers!
    }

    @Override
    public void simpleInitApp() {
        try {
            Server myServer = Network.createServer(SERVER_PORT);
            myServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
