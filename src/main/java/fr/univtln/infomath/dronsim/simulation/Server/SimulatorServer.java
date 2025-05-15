package fr.univtln.infomath.dronsim.simulation.Server;

import java.io.IOException;
import java.util.ArrayList;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
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
import fr.univtln.infomath.dronsim.simulation.Drones.DroneInitData;
import fr.univtln.infomath.dronsim.simulation.Drones.DroneModel;
import fr.univtln.infomath.dronsim.simulation.Drones.DroneServer;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.DroneMovementRequestMessage;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.Handshake1;
import fr.univtln.infomath.dronsim.simulation.jmeMessages.Handshake2;

import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

public class SimulatorServer extends SimpleApplication implements PhysicsCollisionListener {
    private static final int SERVER_PORT = 6143; // Default JME server port
    private static final Logger log = LoggerFactory.getLogger(SimulatorClient.class);
    private static ServerListener serverListener;
    private static Server server;
    private static int idMap;
    private BulletAppState bulletState;
    private PhysicsSpace space;
    private Node scene;
    private static int nbDrones;
    private static final float WATERLEVEL = 2.0f; // TODO: to be set by the map

    public static void main(String[] args) {
        if (args.length != 1) {
            idMap = 0;
            log.warn("No map ID provided, using default: " + idMap);
            log.warn("Launch arguments :  <map_id>");
        }
        SimulatorServer app = new SimulatorServer();
        app.start(JmeContext.Type.Headless); // headless type for servers!
    }

    /**
     * This method is called to register the serializable classes used in the
     * application.
     */
    public void initializeSerializables() {
        Serializer.registerClass(Handshake1.class);
        Serializer.registerClass(Handshake2.class);
        Serializer.registerClass(DroneModel.class);
        Serializer.registerClass(DroneInitData.class);
        Serializer.registerClass(DroneDTO.class);
        Serializer.registerClass(DroneDTOMessage.class);
        Serializer.registerClass(DroneMovementRequestMessage.class);
    }

    @Override
    public void simpleInitApp() {
        try {
            server = Network.createServer(SERVER_PORT);
            initializeSerializables();
            serverListener = new ServerListener(this);
            server.addMessageListener(serverListener, Handshake1.class);
            server.addMessageListener(serverListener, Handshake2.class);
            server.addMessageListener(serverListener, DroneMovementRequestMessage.class);
            server.addMessageListener(serverListener, DroneDTOMessage.class);
            server.start();
            log.info("Server starting on port " + SERVER_PORT);
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

        // Ajout d'un modele de drone
        DroneModel ModelA = new DroneModel("Guardian", 200, "vehicle/subseatech/guardian/guardian-vehicle.j3o", 1,
                1000);
        DroneModel ModelB = new DroneModel("BlueRov", 200, "vehicle/bluerobotics/br2r4/br2-r4-vehicle.j3o", 1, 2000);

        // Ajout du drone
        DroneServer droneA = DroneServer.createDrone(
                0,
                0,
                assetManager,
                space,
                ModelA,
                new Vector3f(0.0f, 2.0f, 0.0f),
                100);
        scene.attachChild(droneA.getNode());

        DroneDTO.createDroneDTO(droneA);

        DroneServer droneB = DroneServer.createDrone(
                1,
                1,
                assetManager,
                space,
                ModelB,
                new Vector3f(3.0f, 0.0f, 0.0f),
                100);
        scene.attachChild(droneB.getNode());
        DroneDTO.createDroneDTO(droneB);
        nbDrones = Drone.getDrones().size();
        log.info("Initialization complete, waiting for clients...");
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
        space.add(terrainPhysics);
    }

    @Override
    public void simpleUpdate(float tpf) {
        // time += tpf;
        // if (time > 0.1f) {
        // time = 0.0f;
        updateDronePositions();
        sendDronePositions();
        // }
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Handle collision events here if needed
    }

    public void sendHandshake2(int clientId, HostedConnection source) {
        int droneId = -1; // The id of the drone that belongs to the client, -1 if no drone
        ArrayList<DroneInitData> dronesInitData = new ArrayList<>();
        for (Drone drone : Drone.getDrones()) {
            dronesInitData
                    .add(new DroneInitData(drone.getId(), drone.getClientId(), drone.getDroneModel(),
                            drone.getBatteryLevel(), drone.getPosition(), drone.getAngular(), drone.getName(),
                            drone.getWeight(), drone.getModules()));
            if (drone.getClientId() == clientId) {
                droneId = drone.getId();
            }
        }
        Handshake2 handshake2 = new Handshake2(dronesInitData, idMap, droneId);
        server.broadcast(Filters.in(source), handshake2);
    }

    public void sendDronePositions() {
        // Direct access to attributes for performance reasons
        for (int i = 0; i < nbDrones; i++) {
            Drone drone = Drone.getDrones().get(i);
            DroneDTO droneDTO = DroneDTO.dronesDTOs.get(i);
            droneDTO.id = drone.getId();
            droneDTO.position = drone.getPosition();
            droneDTO.angular = drone.getAngular();
            droneDTO.batteryLevel = drone.getBatteryLevel();
        }
        server.broadcast(new DroneDTOMessage(DroneDTO.dronesDTOs));
    }

    public void processDroneMovementRequest(DroneMovementRequestMessage message) {
        for (Drone drone : Drone.getDrones()) {
            if (drone.getId() == message.getDroneId()) {
                drone.setDirections(message.getDirections());
                drone.setMotors_speeds(message.getMotorsSpeeds());
                return;
            }
        }
    }

    public void updateDronePositions() {
        // Version test simplifiee, on applique une force a l'origine du drone avec un
        // vecteur qui depend de la direction,
        // a remplacer par definir un vecteur a chaque moteur avec une direction
        // potentiellement fixe pour chaque moteur
        // selon le modele du drone et on change juste les intensites de chauqe force
        for (Drone drone : Drone.getDrones()) {
            Vector3f force = new Vector3f();

            Vector3f forwardDir = drone.getNode().getLocalRotation().mult(Vector3f.UNIT_Z).setY(0).normalizeLocal();
            Vector3f rightDir = drone.getNode().getLocalRotation().mult(Vector3f.UNIT_X).setY(0).normalizeLocal();
            Vector3f upDir = drone.getNode().getLocalRotation().mult(Vector3f.UNIT_Y).normalizeLocal();

            boolean forward = false;
            boolean backward = false;
            boolean left = false;
            boolean right = false;
            boolean ascend = false;
            boolean descend = false;
            for (String direction : drone.getDirections()) {
                switch (direction) {
                    case "FORWARD" -> forward = true;
                    case "BACKWARD" -> backward = true;
                    case "LEFT" -> left = true;
                    case "RIGHT" -> right = true;
                    case "ASCEND" -> ascend = true;
                    case "DESCEND" -> descend = true;
                }
            }
            if (forward)
                force.addLocal(forwardDir);
            if (backward)
                force.subtractLocal(forwardDir);
            if (left)
                force.addLocal(rightDir);
            if (right)
                force.subtractLocal(rightDir);
            if (ascend) {
                float currentY = drone.getNode().getWorldTranslation().y;
                if (currentY < WATERLEVEL) {
                    force.addLocal(upDir); // Monter uniquement si on est sous l’eau
                }
            }
            if (descend)
                force.subtractLocal(upDir);

            // log.info(drone.getDirections().toString());

            if (!force.equals(Vector3f.ZERO)) {
                // TODO: Gerer les differents moteurs
                force.normalizeLocal().multLocal(drone.getMotors_speeds().get(0));
                // log.info("Force: " + force.toString());
                ((DroneServer) (drone)).getControl().applyCentralForce(force);
                drone.setPosition(drone.getNode().getLocalTranslation());
                drone.setAngular(drone.getNode().getLocalRotation());
                // log.info("Drone " + drone.getId() + " position: " +
                // drone.getPosition().toString());
            }
            drone.getDirections().clear();
        }

    }

    // geotools WSG84
    // Choisir zone UTM
}
