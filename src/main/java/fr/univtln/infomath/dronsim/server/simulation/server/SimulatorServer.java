package fr.univtln.infomath.dronsim.server.simulation.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
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

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.NoSuchAuthorityCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.server.simulation.client.SimulatorClient;
import fr.univtln.infomath.dronsim.server.simulation.control.ArduSubControler;
import fr.univtln.infomath.dronsim.server.simulation.control.Controler;
import fr.univtln.infomath.dronsim.server.simulation.drones.Drone;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneDTO;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneInitData;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneServer;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarine;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineDTO;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineInitData;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineServer;
import fr.univtln.infomath.dronsim.server.simulation.evenements.Evenement;
import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;
import fr.univtln.infomath.dronsim.server.simulation.evenements.AjoutEntiteMarineEvent;
import fr.univtln.infomath.dronsim.server.simulation.evenements.Courant;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.AjoutEvenementMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneMovementRequestMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.EntiteMarineDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.EvenementDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake1;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake2;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.RetirerEvenementMessage;

import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import lombok.Getter;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.lang.ProcessBuilder;

//TODO: Fix Jmonkey Serialization problem with server and client in same jvm (idea : put a launch param to tell the client to not do the serialization)
public class SimulatorServer extends SimpleApplication implements PhysicsCollisionListener {
    private static final int SERVER_PORT = 6143; // Default JME server port
    private static final Logger log = LoggerFactory.getLogger(SimulatorServer.class);
    private static ServerListener serverListener;
    private static Server server;
    private BulletAppState bulletState;
    private static PhysicsSpace space;
    private Node scene;
    private static int nbDrones;
    private static final float WATERLEVEL = 2.5f; // TODO: to be set by the map
    @Getter
    private static List<DroneModel> models;
    private static boolean ready = false;
    @Getter
    private static SimulatorServer instance; // Singleton instance, useful to access assetmanager from other classes and
                                             // check if the server is already running

    @Getter
    private static List<DroneAssociation> droneAssociations = DroneAssociation.getDroneAssociations();
    // WARNING: we assume that the drone associations are already initialized from
    // the manager

    public static void main(String[] args) {
        if (instance != null) {
            log.error("SimulatorServer already running, aborting");
            throw new IllegalStateException("SimulatorServer already running, aborting");
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
        Serializer.registerClass(EvenementDTO.class);
        Serializer.registerClass(EvenementDTOMessage.class);
        Serializer.registerClass(AjoutEvenementMessage.class);
        Serializer.registerClass(RetirerEvenementMessage.class);
        Serializer.registerClass(EntiteMarineDTO.class);
        Serializer.registerClass(EntiteMarineDTOMessage.class);
        Serializer.registerClass(EntiteMarineInitData.class);

    }

    @Override
    public void simpleInitApp() {
        instance = this; // Singleton instance
        try {
            server = Network.createServer(SERVER_PORT);
            initializeSerializables();
            serverListener = new ServerListener(this);
            server.addMessageListener(serverListener, Handshake1.class);
            server.addMessageListener(serverListener, Handshake2.class);
            server.addMessageListener(serverListener, DroneMovementRequestMessage.class);
            server.addMessageListener(serverListener, DroneDTOMessage.class);
            server.addMessageListener(serverListener, AjoutEvenementMessage.class);
            server.addMessageListener(serverListener, RetirerEvenementMessage.class);

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

        models = loadDroneModelsFromJson(
                "JsonData/DronesModels.json");

        // // Init Ardusub controler
        // Controler controler;
        // try {
        // controler = new ArduSubControler(Controler_IP_LIST.get(0));
        // // Ajout du drone
        // DroneServer droneA = DroneServer.createDrone(
        // 0,
        // 0,
        // assetManager,
        // space,
        // ModelB,
        // new Vector3f(0.0f, 2.0f, 0.0f),
        // 100,
        // controler);
        // scene.attachChild(droneA.getNode());

        // DroneDTO.createDroneDTO(droneA);
        // } catch (IOException e) {
        // e.printStackTrace();
        // log.error("Error while connecting to the controler, skipping drone
        // creation");

        // }

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

        if (droneAssociations.size() == 0) {
            log.error("No drone associations found, aborting server initialization");
            throw new IllegalStateException("No drone associations found, aborting server initialization");
        }
        // log.info("DEBUG : Drone associations: " + droneAssociations.toString());
        initDrones(assetManager, space, scene);
        nbDrones = DroneServer.getDroneServerList().size();
        log.info("Drones initialized: " + nbDrones + " drones created");
        ready = true;
        log.info("Initialization complete, waiting for pilots to connect");
    }

    /**
     * This method is called by the manager when a new pilot added by the GM
     * try to connects to the server.
     *
     * @param clientId      The id of the client calculated by the manager
     * @param ModelName     The name of the drone model
     * @param pilotIP       The IP of the pilot
     * @param controlerType The type of the controler, currently the only one
     *                      supported is 0 (Ardusub), if you are making your own
     *                      controler you need to add it in the code of this method
     */
    public static void initPilot(DroneAssociation droneAsso, String pilotIP, int controlerType) {
        if (!ready) {
            log.error("Server not ready, please wait and retry, aborting drone creation");
            throw new IllegalStateException("Server not ready, please wait and retry, aborting drone creation");
        }
        Controler controler = null;
        int tryCount = 0;
        try {
            Thread.sleep(3000); // Wait 3s to let Ardupilot start properly
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Try to connect to the controler
        while (controler == null && tryCount < 120) {
            try {
                controler = new ArduSubControler(pilotIP);
                log.info("Controler connected to " + pilotIP);
            } catch (IOException e) {
                log.error("Error while connecting to the controler " + pilotIP + ", retrying in 1s");
            }
            tryCount++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (controler == null) {
            log.error("Error while connecting to the controler " + pilotIP + ", aborting drone creation");
            throw new IllegalStateException(
                    "Error while connecting to the controler " + pilotIP + ", aborting drone creation");
        }

        for (DroneServer droneServer : DroneServer.getDroneServerList()) {
            if (droneAsso.getId() == droneServer.getClientId()) {
                // We assume that the drone is already created and we just need to set the
                // controler
                droneServer.setControler(controler);
                log.info("Controler set for drone " + droneServer.getId() + " with clientId " + droneAsso.getId());
            }
        }

        // We start the jME client if the connection mode is cloud (0) in a new JVM
        int connMode = droneAsso.getConnexionMode();
        if (connMode == 0) {
            // new Thread(() -> {
            // SimulatorClient
            // .main(new String[] { pilotIP, "127.0.0.1", String.valueOf(droneAsso.getId()),
            // String.valueOf(connMode) });
            // }).start();
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-cp",
                    System.getProperty("java.class.path"),
                    "fr.univtln.infomath.dronsim.server.simulation.client.SimulatorClient",
                    pilotIP, "127.0.0.1", String.valueOf(droneAsso.getId()) // Optional arguments
            );
            try {
                Process process = pb.inheritIO().start();
                log.info("SimulatorClient started for pilot " + droneAsso.getId() + " at " + pilotIP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void attachTerrain(Node parent) {
        Node terrainNode = new Node("Terrain");
        terrainNode.setLocalTranslation(new Vector3f(3.0f, -15.0f, 0.0f));
        Spatial terrain = assetManager.loadModel("Models/manta_point_version_6_superseded.j3o");
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
        // Appliquer les forces de courant AVANT d’envoyer les positions

        for (Evenement event : Evenement.getEvenements()) {
            event.apply(tpf);
        }
        for (EntiteMarine entite : EntiteMarine.getEntites()) {
            if (entite instanceof EntiteMarineServer entiteServer) {
                entiteServer.update(tpf);
            }
        }
        sendDronePositions();
        sendEntiteMarinePositions();
        broadcastEvenements();

        // }
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Handle collision events here if needed
    }

    public void sendHandshake2(int clientId, HostedConnection source) {
        int droneId = -1; // The id of the drone that belongs to the client, -1 if no drone
        ArrayList<DroneInitData> dronesInitData = new ArrayList<>();
        for (Drone drone : DroneServer.getDroneServerList()) {
            dronesInitData
                    .add(new DroneInitData(drone.getId(), drone.getClientId(), drone.getDroneModel(),
                            drone.getBatteryLevel(), drone.getPosition(), drone.getAngular(), drone.getName(),
                            drone.getWeight(), drone.getModules()));
            if (drone.getClientId() == clientId) {
                droneId = drone.getId();
            }
        }
        List<EntiteMarineInitData> marineInitData = new ArrayList<>();
        for (EntiteMarine entite : EntiteMarine.getEntites()) {
            marineInitData.add(new EntiteMarineInitData(
                    entite.getId(),
                    entite.getType(),
                    entite.getModelPath(),
                    entite.getPositionInitiale(),
                    entite.getDirection(),
                    entite.getSpeed()));
        }
        List<EvenementDTO> evenementsInitData = new ArrayList<>();
        for (Evenement evenement : Evenement.getEvenements()) {

            float intensite = 1f;
            String entiteType = null;
            String modelPath = null;

            if (evenement instanceof Courant courant) {
                intensite = courant.getIntensite();
            } else if (evenement instanceof AjoutEntiteMarineEvent e) {
                intensite = e.getEntite().getSpeed();
                entiteType = e.getEntite().getType();
                modelPath = e.getEntite().getModelPath();
            }
            evenementsInitData.add(new EvenementDTO(
                    evenement.getId(),
                    evenement.getZoneCenter(),
                    evenement.getZoneSize(),
                    evenement.getType(),
                    evenement.getDirection(),
                    intensite,
                    entiteType,
                    modelPath));
        }

        Handshake2 handshake2 = new Handshake2(dronesInitData, droneId, marineInitData, evenementsInitData);
        if (droneId == -1) {
            log.error("No drone found for clientId " + clientId + ", aborting handshake");
            throw new IllegalStateException("No drone found for clientId " + clientId + ", aborting handshake");
        }

        server.broadcast(Filters.in(source), handshake2);
    }

    public void sendDronePositions() {
        // Direct access to attributes for performance reasons
        for (int i = 0; i < nbDrones; i++) {
            Drone drone = DroneServer.getDroneServerList().get(i);
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
        for (DroneServer droneServer : DroneServer.getDroneServerList()) {
            // Update the thruster vectors
            // Rotate each initial thruster vector by the drone's local rotation
            DroneModel droneModel = droneServer.getDroneModel();
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

            List<Float> motorsSpeeds = droneServer.getMotors_speeds();
            motorsSpeeds.clear(); // Clear the list to update it with new values
            // Update the motors speeds list if the controler is not null
            if (droneServer.getControler() != null) {
                for (int i = 0; i < droneModel.getNbMotors(); i++) {
                    float motorspeed = droneServer.getControler().getMotorThrottle(i);
                    motorsSpeeds.add(motorspeed * droneModel.getMotorsMaxSpeed());
                    // log.info("Motor " + i + " speed: " + motorspeed + " x " +
                    // droneModel.getMotorsMaxSpeed());
                }
                // log.info("DEBUG: Motors speeds for drone " + droneServer.getId() + ": "
                // + motorsSpeeds.toString() + " Drone motorspeeds: " +
                // droneServer.getMotors_speeds().toString());
            }
            // If the controler is null, we set the motors speeds to 0
            else {
                for (int i = 0; i < droneModel.getNbMotors(); i++) {
                    motorsSpeeds.add(0f);
                }
            }

            // Apply forces to the drone based on the thruster vectors and speeds
            for (int i = 0; i < droneModel.getNbMotors(); i++) {
                Vector3f force = droneServer.getThrusterVecs().get(i).mult(droneServer.getMotors_speeds().get(i));
                // log.info("DEBUG: Trhuster " + i + " force: " + force.toString() + "Thruster
                // vec: "
                // + droneServer.getThrusterVecs().get(i).toString() + " Speed: "
                // + droneServer.getMotors_speeds().get(i));
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
            Vector3f phyLocation = body.getPhysicsLocation();
            if (phyLocation.y > WATERLEVEL) {
                Vector3f newPos = phyLocation.clone();
                newPos.y = WATERLEVEL;
                droneServer.getNode().setLocalTranslation(newPos);
                droneServer.getBody().setPhysicsLocation(newPos);

            }

            // Update the drone's position and rotation attributes
            droneServer.setPosition(droneServer.getNode().getLocalTranslation());
            droneServer.setAngular(droneServer.getNode().getLocalRotation());
            // log.info("DEBUG: Drone position = " + droneServer.getPosition() +
            // " Drone node position = " + droneServer.getNode().getLocalTranslation() +
            // "Drone body position = " + body.getPhysicsLocation());

        }

    }

    public void ajoutEvenement(EvenementDTO dto) {
        if ("Courant".equals(dto.getType())) {
            Courant courant = new Courant(
                    dto.getId(),
                    dto.getZoneCenter(),
                    dto.getZoneSize(),
                    dto.getDirection(),
                    dto.getIntensite(),
                    space, assetManager);
        } else if ("EntiteMarine".equals(dto.getType())) {
            EntiteMarineInitData initData = new EntiteMarineInitData(
                    dto.getId(),
                    dto.getEntiteType(),
                    dto.getModelPath(),
                    dto.getZoneCenter(), // Utilisé comme position
                    dto.getDirection(), // Direction initiale
                    dto.getIntensite() // Vitesse
            );

            AjoutEntiteMarineEvent event = new AjoutEntiteMarineEvent(
                    initData,
                    assetManager,
                    space);
            scene.attachChild(event.getModelNode());
        }

        broadcastEvenements();
    }

    public void retirerEvenement(int id) {
        Evenement toRemove = null;
        for (Evenement ev : Evenement.getEvenements()) {
            if (ev.getId() == id) {
                toRemove = ev;
                break;
            }
        }
        if (toRemove != null) {
            toRemove.retirer();
            broadcastEvenements();
        }
    }

    private void broadcastEvenements() {
        List<EvenementDTO> eventDTOs = new ArrayList<>();
        for (Evenement event : Evenement.getEvenements()) {
            eventDTOs.add(EvenementDTO.createEvenementDTO(event.getId(),
                    event.getZoneCenter(),
                    event.getZoneSize(),
                    event.getType(),
                    event.getDirection(),
                    event instanceof Courant ? ((Courant) event).getIntensite() : 1f,
                    event instanceof AjoutEntiteMarineEvent ? ((AjoutEntiteMarineEvent) event).getEntite().getType()
                            : null,
                    event instanceof AjoutEntiteMarineEvent
                            ? ((AjoutEntiteMarineEvent) event).getEntite().getModelPath()
                            : null));
        }
        server.broadcast(new EvenementDTOMessage(eventDTOs));
    }

    public void sendEntiteMarinePositions() {
        List<EntiteMarineDTO> dtos = new ArrayList<>();

        // 1. Entités marines "de base"
        for (EntiteMarine entite : EntiteMarine.getEntites()) {
            dtos.add(new EntiteMarineDTO(
                    entite.getId(),
                    entite.getType(),
                    entite.getPositionCourante(),
                    entite.getDirection() // Direction actuelle
            ));
        }

        // 2. Entités marines créées via Evenement
        for (Evenement event : Evenement.getEvenements()) {
            if (event instanceof AjoutEntiteMarineEvent marineEvent) {
                EntiteMarine entite = marineEvent.getEntite();
                dtos.add(new EntiteMarineDTO(
                        entite.getId(),
                        entite.getType(),
                        entite.getPositionCourante(),
                        entite.getDirection()));
            }
        }

        // Envoi réseau
        server.broadcast(new EntiteMarineDTOMessage(dtos));

    }

    /**
     * Initializes the drones depending of the assotiations.
     * This method is called in the simpleInitApp method
     */
    private static void initDrones(AssetManager assetmanager, PhysicsSpace space, Node scene) {
        for (DroneAssociation da : droneAssociations) {
            DroneModel model = null;
            for (DroneModel modeltmp : SimulatorServer.getModels()) {
                if (modeltmp.getName().equals(da.getDroneModelName())) {
                    model = modeltmp;
                    break;
                }
            }
            if (model == null) {
                log.error("Model not found " + da.getDroneModelName() + ",aborting drone creation");
                throw new IllegalStateException(
                        "Model not found " + da.getDroneModelName() + ",aborting drone creation");
            }
            int id = Drone.getDrones().size();
            DroneServer drone = DroneServer.createDrone(
                    id,
                    da.getId(),
                    assetmanager,
                    space,
                    model,
                    new Vector3f((float) id, 0.0f, 0.0f),
                    100,
                    null);
            scene.attachChild(drone.getNode());
            DroneDTO.createDroneDTO(drone);
        }
    }

    /**
     * Load the drone models from a JSON file.
     *
     * @param filePath The path to the JSON file.
     * @return A list of DroneModel objects.
     */
    public static List<DroneModel> loadDroneModelsFromJson(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        // We set this to avoid errors about the unitVector property when we serialize a
        // Vector3f
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(new File(filePath), new TypeReference<List<DroneModel>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // geotools WSG84
    // Choisir zone UTM
}
