package fr.univtln.infomath.dronsim.simulation.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.simulation.Client.SimulatorClient;
import fr.univtln.infomath.dronsim.simulation.Drones.Drone;
import fr.univtln.infomath.dronsim.simulation.Drones.DroneDTO;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneMovementRequestMessage;
import com.jme3.network.Message;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

public class SimulatorServer extends SimpleApplication implements PhysicsCollisionListener {
    private static final int SERVER_PORT = 6143; // Default JME server port
    private static final Logger log = LoggerFactory.getLogger(SimulatorClient.class);
    private static ServerListener serverListener;
    private static Server server;
    private static int test = 0;
    private BulletAppState bulletState;
    private PhysicsSpace space;
    private Node scene;

    public static void main(String[] args) {
        SimulatorServer app = new SimulatorServer();
        app.start(JmeContext.Type.Headless); // headless type for servers!
    }

    /**
     * This method is called to register the serializable classes used in the
     * application.
     */
    public void initializeSerializables() {
        Serializer.registerClass(DroneDTO.class);
        Serializer.registerClass(DroneDTOMessage.class);
        Serializer.registerClass(DroneMovementRequestMessage.class);
    }

    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(SERVER_PORT);
            initializeSerializables();
            serverListener = new ServerListener();
            server.addMessageListener(serverListener, DroneMovementRequestMessage.class);
            server.addMessageListener(serverListener, DroneDTOMessage.class);
            server.start();
            log.info("Server started on port " + SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Initialisation de la physique
        bulletState = new BulletAppState();
        stateManager.attach(bulletState);
        space = bulletState.getPhysicsSpace();
        space.addCollisionListener(this);

        // Crée la scène principale
        scene = new Node("MainScene");
        scene.setShadowMode(ShadowMode.CastAndReceive);
        rootNode.attachChild(scene);

        // Enregistrement du dossier d'assets
        assetManager.registerLocator("data/asset", FileLocator.class);

        // Ajout d’un repère
        // ReferentialNode refNode = new ReferentialNode(assetManager);
        // scene.attachChild(refNode);

        // Ajout du terrain
        attachTerrain(scene);

        // Ajout du drone
        Drone droneA = Drone.createDrone(
                0,
                assetManager,
                space,
                "vehicle/bluerobotics/br2r4/br2-r4-vehicle.j3o",
                "VehicleA",
                new Vector3f(0.0f, 2.0f, 0.0f),
                200f);
        scene.attachChild(droneA.getNode());

        Drone droneB = Drone.createDrone(
                1,
                assetManager,
                space,
                "vehicle/subseatech/guardian/guardian-vehicle.j3o",
                "VehicleB",
                new Vector3f(3.0f, 2.0f, 0.0f),
                200f);
        scene.attachChild(droneB.getNode());
        log.info("Initialization complete");
    }

    private void attachTerrain(Node parent) {
        Node terrainNode = new Node("Terrain");
        terrainNode.setLocalTranslation(new Vector3f(3.0f, -15.0f, 0.0f));
        Spatial terrain = assetManager.loadModel("Models/manta_point_version_6_superseded.glb");
        terrainNode.attachChild(terrain);
        parent.attachChild(terrainNode);

        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(terrainNode);
        RigidBodyControl terrainPhysics = new RigidBodyControl(terrainShape, 0); // Masse 0 = statique
        terrainNode.addControl(terrainPhysics);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // TEST
        //
        // test++;
        // List<DroneDTO> dronesInfos = new ArrayList<>();
        // for (int i = 0; i < 4; i++) {
        // DroneDTO drone = new DroneDTO(i, new Vector3f(test, test, test));
        // dronesInfos.add(drone);
        // }
        // Message dronePosMessage = new DroneDTOMessage(dronesInfos);
        // server.broadcast(dronePosMessage);
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Handle collision events here if needed
    }

    // geotools WSG84
    // Choisir zone UTM
}
