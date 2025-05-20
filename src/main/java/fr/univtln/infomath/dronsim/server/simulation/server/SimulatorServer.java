package fr.univtln.infomath.dronsim.server.simulation.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle.Control;

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
import fr.univtln.infomath.dronsim.server.simulation.client.SimulatorClient;
import fr.univtln.infomath.dronsim.server.simulation.drones.Drone;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneDTO;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneInitData;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneServer;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneMovementRequestMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake1;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake2;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import fr.univtln.infomath.dronsim.server.control.Controler;
import fr.univtln.infomath.dronsim.server.control.ArduSubControler;

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
    private static List<String> Controler_IP_LIST = new ArrayList<>();

    public static void main(String[] args) {
        // TODO: gerer ip dynamiquement quand on fera le launcher
        if (args.length != 2) {
            log.error("Launch arguments :  <map_id> <controler0_ip>");
            System.exit(1);
        }
        idMap = Integer.parseInt(args[0]);
        Controler_IP_LIST.add(args[1]);

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

        // Ajout des modeles de drone

        // DroneModel ModelA = new DroneModel("Guardian", 200,
        // "vehicle/subseatech/guardian/guardian-vehicle.j3o", 1,
        // 1000);

        // Initial thruster positions and vectors initialization for BlueROV2
        List<Vector3f> initialThrusterVecs = new ArrayList<>();
        List<Vector3f> initialThrusterLocalPosition = new ArrayList<>();

        // Initial thruster position based on the node referencial (local position)
        // WARNING : Can be broken if the node is rotated at creation, need to fix this
        initialThrusterLocalPosition = new ArrayList<>();

        initialThrusterVecs.add(new Vector3f(-0.7431f, 0.0000f, -0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, 0.16f));

        initialThrusterVecs.add(new Vector3f(0.7431f, 0.0000f, -0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, 0.16f));

        initialThrusterVecs.add(new Vector3f(0.7431f, 0.0000f, 0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, -0.16f));

        initialThrusterVecs.add(new Vector3f(-0.7431f, 0.0000f, 0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, -0.16f));

        initialThrusterVecs.add(new Vector3f(0.0000f, -1f, 0f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, 0f));

        initialThrusterVecs.add(new Vector3f(-0.0000f, -1f, 0f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, 0f));

        DroneModel ModelB = new DroneModel("BlueROV2", 200, "vehicle/bluerobotics/br2r4/br2-r4-vehicle.j3o", 6, 50,
                initialThrusterVecs, initialThrusterLocalPosition);

        // Init Ardusub controler
        Controler controler;
        try {
            controler = new ArduSubControler(Controler_IP_LIST.get(0));
            // Ajout du drone
            DroneServer droneA = DroneServer.createDrone(
                    0,
                    0,
                    assetManager,
                    space,
                    ModelB,
                    new Vector3f(0.0f, 2.0f, 0.0f),
                    100,
                    controler);
            scene.attachChild(droneA.getNode());

            DroneDTO.createDroneDTO(droneA);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error while connecting to the controler, skipping drone creation");

        }

        // DroneServer droneB = DroneServer.createDrone(
        // 1,
        // 1,
        // assetManager,
        // space,
        // ModelB,
        // new Vector3f(3.0f, 0.0f, 0.0f),
        // 100);
        // scene.attachChild(droneB.getNode());
        // DroneDTO.createDroneDTO(droneB);
        nbDrones = Drone.getDrones().size();
        log.info("Initialization complete, waiting for jME clients...");
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

    // Test method, outdated
    // public void processDroneMovementRequest(DroneMovementRequestMessage message)
    // {
    // for (Drone drone : Drone.getDrones()) {
    // if (drone.getId() == message.getDroneId()) {
    // drone.setDirections(message.getDirections());
    // drone.setMotors_speeds(message.getMotorsSpeeds());
    // return;
    // }
    // }
    // }

    public void updateDronePositions() {
        for (Drone drone : DroneServer.getDrones()) {
            // Update the thruster vectors
            // Rotate each initial thruster vector by the drone's local rotation
            DroneModel droneModel = drone.getDroneModel();
            DroneServer droneServer = (DroneServer) drone;
            RigidBodyControl body = droneServer.getBody();
            List<Vector3f> rotatedThrusterVecs = new ArrayList<>();
            Quaternion rotation = body.getPhysicsRotation();
            for (Vector3f vec : droneModel.getInitialThrusterVecs()) {
                rotatedThrusterVecs.add(rotation.mult(vec));
            }
            droneServer.setThrusterVecs(rotatedThrusterVecs);

            // Update the thruster global positions based on the drone's position and
            // rotation
            List<Vector3f> updatedThrusterPositions = new ArrayList<>();
            Quaternion droneRotation = droneServer.getNode().getLocalRotation();
            Vector3f droneTranslation = droneServer.getNode().getLocalTranslation();
            for (Vector3f initialPos : droneModel.getInitialThrusterLocalPosition()) {
                Vector3f rotatedPos = droneRotation.mult(initialPos);
                updatedThrusterPositions.add(rotatedPos.add(droneTranslation));
            }
            droneServer.setThrusterGlobalPositions(updatedThrusterPositions);

            // Update the motors speeds list
            List<Float> motorsSpeeds = new ArrayList<>();
            for (int i = 0; i < droneModel.getNbMotors(); i++) {
                float motorspeed = droneServer.getControler().getMotorThrottle(i);
                // log.info("Motor " + i + " speed: " + motorspeed);
                motorsSpeeds.add(motorspeed * droneModel.getMotorsMaxSpeed());
            }
            // We update the motors speeds list in the drone object for evnetual use in the
            // future
            drone.setMotors_speeds(motorsSpeeds);
            // log.info("Motors speeds: " + motorsSpeeds.toString());

            // Apply forces to the drone based on the thruster vectors and speeds
            for (int i = 0; i < droneModel.getNbMotors(); i++) {
                Vector3f force = droneServer.getThrusterVecs().get(i).mult(drone.getMotors_speeds().get(i));
                Vector3f thrusterPos = droneServer.getThrusterGlobalPositions().get(i);
                body.applyForce(force, thrusterPos);
            }

            // Force stabilization
            Quaternion droneRot = body.getPhysicsRotation();
            body.setPhysicsRotation(new Quaternion()
                    .fromAngles(
                            0,
                            droneRot.toAngles(null)[1],
                            0));

            // Drones doesnt flies
            if (body.getPhysicsLocation().y > WATERLEVEL) {
                Vector3f newPos = drone.getNode().getLocalTranslation();
                newPos.y = WATERLEVEL;
                drone.getNode().setLocalTranslation(newPos);
            }

            // Update the drone's position and rotation attributes
            drone.setPosition(drone.getNode().getLocalTranslation());
            drone.setAngular(drone.getNode().getLocalRotation());

        }

    }

    // geotools WSG84
    // Choisir zone UTM
}
