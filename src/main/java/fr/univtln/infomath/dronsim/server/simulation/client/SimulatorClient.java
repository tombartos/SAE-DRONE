package fr.univtln.infomath.dronsim.server.simulation.client;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.*;
import com.jme3.math.*;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.*;
import com.jme3.scene.control.CameraControl;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import fr.univtln.infomath.dronsim.server.simulation.control.LocalTestingControler;
import fr.univtln.infomath.dronsim.server.simulation.drones.Drone;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneDTO;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneInitData;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarine;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineDTO;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineInitData;
import fr.univtln.infomath.dronsim.server.simulation.evenements.AjoutEntiteMarineEvent;
import fr.univtln.infomath.dronsim.server.simulation.evenements.Courant;
import fr.univtln.infomath.dronsim.server.simulation.evenements.Evenement;
import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.AjoutEvenementMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneMovementRequestMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.EntiteMarineDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.EvenementDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake1;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake2;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.RetirerEvenementMessage;
//import fr.univtln.infomath.dronsim.server.viewer.primitives.ReferentialNode;
import fr.univtln.infomath.dronsim.server.utils.GStreamerSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SimulatorClient is the main client application for the drone simulation.
 * <p>
 * It connects to a simulation server, initializes the 3D environment using
 * jMonkeyEngine, manages the rendering of drones and marine entities, handles
 * network communication, and streams video output using GStreamer.
 * <p>
 * The class is responsible for:
 * <ul>
 * <li>Connecting to the simulation server and handling handshake messages</li>
 * <li>Initializing and updating the 3D scene, including terrain, water,
 * lighting, and sky</li>
 * <li>Managing drones and marine entities, updating their positions and
 * states</li>
 * <li>Displaying visual effects for simulation events</li>
 * <li>Streaming the rendered scene to a specified video destination</li>
 * </ul>
 * <p>
 * Usage: The application expects three command-line arguments:
 *
 * <pre>
 *   &lt;video_destination_ip&gt; &lt;server_ip&gt; &lt;client_id&gt;
 * </pre>
 *
 * @author Tom BARTIER
 * @author Emad BA GUBAIR
 * @author Julien Seinturier
 */
public class SimulatorClient extends SimpleApplication implements PhysicsCollisionListener, ActionListener {
    private static final Logger log = LoggerFactory.getLogger(SimulatorClient.class);

    private Client client;
    private int clientId;
    private BulletAppState bulletState;
    private PhysicsSpace space;
    private FilterPostProcessor fpp;
    private Node scene;
    private LocalTestingControler controlerA;
    private String ipDestVideo;
    private static int portDestVideo = 5600; // Default port for QGroundControl video stream
    private static int width = 1024; // Default window and video resolution
    private static int height = 768;
    private String server_ip;
    private static int server_port = 6143; // Default JME server port

    private enum CamMode {
        FPV, CHASE, FREE
    }

    private CamMode currentCamMode = CamMode.FPV; // Default camera mode is FPV (First Person View)
    private ChaseCamera chaseCam;
    private CameraNode fpvCamNode;
    private int currentObservedIndex = 0;

    public static void main(String[] args) {
        if (args.length != 3) {
            log.info("Launch arguments :  <video_destination_ip> <server_ip> <client_id>");
            System.exit(1);
        }
        AppSettings settings = new AppSettings(true);
        settings.setWidth(width);
        settings.setHeight(height);
        settings.setFrameRate(60);

        SimulatorClient app = new SimulatorClient(args);
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    public SimulatorClient(String[] args) {
        this.ipDestVideo = args[0];
        this.server_ip = args[1];
        this.clientId = Integer.parseInt(args[2]);
    }

    @Override
    public void simpleInitApp() {
        setPauseOnLostFocus(false);

        // Network initialisation
        try {
            client = Network.connectToServer(server_ip, server_port);
            ClientListener clientListener = new ClientListener(this);
            client.addMessageListener(clientListener, DroneDTOMessage.class);
            client.addMessageListener(clientListener, Handshake2.class);
            client.addMessageListener(clientListener, EvenementDTOMessage.class);
            client.addMessageListener(clientListener, EntiteMarineDTOMessage.class);
            client.start();
            log.info("Connected to server at " + server_ip + ":" + server_port);
        } catch (IOException e) {
            log.error("Failed to connect to server at " + server_ip + ":" + server_port, e);
            System.exit(1);
            return;
        }

        // Initialisation GStreamer
        GStreamerSender gstreamerSender = new GStreamerSender(width, height, ipDestVideo, portDestVideo);

        // Initialisation du processeur de capture d'images
        FrameCaptureProcessor frameCaptureProcessor = new FrameCaptureProcessor(width, height, gstreamerSender);

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

        // Handshake
        client.send(new Handshake1(clientId));

        // Lumière et ciel
        initLighting();
        initWater();
        rootNode.attachChild(SkyFactory.createSky(
                assetManager,
                "Textures/Sky/Bright/BrightSky.dds",
                SkyFactory.EnvMapType.CubeMap));
        inputManager.addMapping("SwitchCam", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addMapping("FreeCam", new KeyTrigger(KeyInput.KEY_F));
        inputManager.addMapping("NextDrone", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("PrevDrone", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addListener(this, "SwitchCam", "FreeCam", "NextDrone", "PrevDrone");
        viewPort.addProcessor(frameCaptureProcessor); // Ajout du processeur de capture d'images
        // ajouterEvenementTest();
        // retirerEvenement(999);
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
        Serializer.registerClass(EntiteMarineDTO.class);
        Serializer.registerClass(EntiteMarineDTOMessage.class);
        Serializer.registerClass(EntiteMarineInitData.class);

    }

    /**
     * This method is called when the client receives a message of type Handshake2.
     * It initializes the simulation environment by adding drones and configuring
     * the camera.
     *
     * @param handshake2
     */
    public void initEnv(Handshake2 handshake2) {
        // Ajout des drones
        Drone tmpDrone = null;
        Drone yourDrone = null;
        for (DroneInitData droneinit : handshake2.getDronesInitData()) {
            tmpDrone = Drone.createDrone(
                    droneinit.getId(),
                    droneinit.getClientId(),
                    assetManager,
                    space,
                    droneinit.getDroneModel(),
                    droneinit.getPosition(),
                    droneinit.getBatteryLevel());
            final Drone finalTmpDrone = tmpDrone; // Pour éviter les problèmes de capture dans la lambda
            enqueue(() -> {
                scene.attachChild(finalTmpDrone.getNode());
                return null;
            });
            if (tmpDrone.getId() == handshake2.getYourDroneId()) {
                yourDrone = tmpDrone;
            }
        }

        if (yourDrone == null) {
            // On est donc un observateur
            if (!Drone.getDrones().isEmpty()) {
                setDroneObservationCamera(Drone.getDrones().get(currentObservedIndex));
            }
            // log.error("Your drone is not found in the list of drones");
            // log.error("Available drones: " + Drone.getDrones().toString());
            // log.error("Your client ID: " + handshake2.getYourDroneId());
            // System.exit(1);
        } else {
            Spatial droneSpatial = yourDrone.getNode();
            chaseCam = new ChaseCamera(cam, droneSpatial, inputManager);
            chaseCam.setDefaultDistance(10f); // distance par défaut entre la caméra et le drone
            chaseCam.setMaxDistance(20f); // distance max (molette)
            chaseCam.setMinDistance(3f); // distance min (molette)
            // chaseCam.setSmoothMotion(true); // mouvement fluide
            chaseCam.setTrailingEnabled(true); // caméra suit le mouvement
            // 2. FPV CameraNode attachée au drone
            fpvCamNode = new CameraNode("FPVCam", cam);
            fpvCamNode.setLocalTranslation(new Vector3f(0, 0.2f, 1f));
            ((Node) droneSpatial).attachChild(fpvCamNode);
            // désactiver la FPV au début
            fpvCamNode.setEnabled(false);
            // Contrôle clavier du drone
            controlerA = new LocalTestingControler(inputManager, client, yourDrone.getId());
        }

        // Ajout des entités marines
        for (EntiteMarineInitData marineInit : handshake2.getEntitesMarineInitData()) {
            EntiteMarine entite = EntiteMarine.createEntite(
                    marineInit.getId(),
                    marineInit.getType(),
                    marineInit.getModelPath(),
                    marineInit.getPosition(),
                    marineInit.getDirection(),
                    marineInit.getSpeed(),
                    assetManager);
            enqueue(() -> {
                scene.attachChild(entite.getModelNode());
                return null;
            });
        }

        // Ajout des événements initiaux
        for (EvenementDTO dto : handshake2.getEvenementsInitData()) {
            Evenement event = createEvenementFromDTO(dto, assetManager, space);
            if (event instanceof Courant courant) {
                enqueue(() -> {
                    scene.attachChild(courant.getVisuel());
                    return null;
                });
            } else if (event instanceof AjoutEntiteMarineEvent marineEvent) {
                enqueue(() -> {
                    scene.attachChild(marineEvent.getModelNode());
                    return null;
                });
            }
        }
    }

    /**
     * Attaches the terrain model to the specified parent node.
     *
     * @param parent The parent node to which the terrain will be attached.
     */
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

    /**
     * Initializes the water filter for the scene.
     * <p>
     * This method creates a WaterFilter and adds it to the FilterPostProcessor,
     * which is then added to the viewPort for rendering.
     */
    private void initWater() {
        WaterFilter waterFilter = new WaterFilter();
        waterFilter.setWaterHeight(3.0f);
        waterFilter.setSpeed(0.5f);

        fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(waterFilter);
        viewPort.addProcessor(fpp);
    }

    /**
     * Initializes the lighting for the scene.
     * <p>
     * This method adds an ambient light and a directional light to the root node
     * to illuminate the scene.
     */
    private void initLighting() {
        rootNode.addLight(new AmbientLight(ColorRGBA.White));

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection(new Vector3f(-.5f, -.5f, -.5f).normalizeLocal());
        rootNode.addLight(sun);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (controlerA != null) {
            controlerA.update(tpf);
        }
        for (Drone drone : Drone.getDrones()) {
            // log.info("Drone " + drone.getId() + " position: " + drone.getPosition());
            Node node = drone.getNode();
            if (node != null) {
                node.setLocalTranslation(drone.getPosition());
                node.setLocalRotation(drone.getAngular());
            }
        }

        for (EntiteMarine entite : EntiteMarine.getEntites()) {

            Node node = entite.getModelNode();
            if (node != null) {
                node.setLocalTranslation(entite.getPositionCourante());
            }
        }

        for (Evenement event : Evenement.getEvenements()) {
            if (event != null) {
                if (event instanceof AjoutEntiteMarineEvent marineEvent) {
                    EntiteMarine entiteEvent = marineEvent.getEntite();
                    Node nodemarine = entiteEvent.getModelNode();

                    if (nodemarine != null) {
                        nodemarine.setLocalTranslation(entiteEvent.getPositionCourante());
                    }

                }
            }
        }

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Rien à faire ici, la physique gère la collision naturellement
    }

    /**
     * Updates the positions and states of drones based on the provided list of
     * DroneDTOs.
     *
     * @param dronesDTOsList List of DroneDTOs containing updated drone information.
     */
    public void updateDronesInfo(List<DroneDTO> dronesDTOsList) {
        for (Drone drone : Drone.getDrones()) {
            for (DroneDTO droneDTO : dronesDTOsList) {
                if (drone.getId() == droneDTO.id) {
                    drone.setPosition(droneDTO.getPosition());
                    drone.setAngular(droneDTO.getAngular());
                    drone.setBatteryLevel(droneDTO.getBatteryLevel());
                }
            }
        }

    }

    /**
     * Updates the state (position and direction) of marine entities based on the
     * received list of DTOs.
     * This includes both base entities and those created via events.
     *
     * @param dtos List of {@link EntiteMarineDTO} received from the server.
     */
    public void updateEntitesMarine(List<EntiteMarineDTO> dtos) {
        // 1. Mise à jour des entités de base
        for (EntiteMarine entite : EntiteMarine.getEntites()) {
            for (EntiteMarineDTO dto : dtos) {
                if (entite.getId() == dto.getId()) {
                    entite.setPositionCourante(dto.getPosition());
                    entite.setDirection(dto.getDirection());
                }
            }
        }
        // 2. Mise à jour des entités créées via AjoutEntiteMarineEvent
        for (Evenement event : Evenement.getEvenements()) {
            if (event instanceof AjoutEntiteMarineEvent marineEvent) {
                EntiteMarine entite = marineEvent.getEntite();
                for (EntiteMarineDTO dto : dtos) {
                    if (entite.getId() == dto.getId()) {
                        entite.setPositionCourante(dto.getPosition());
                        entite.setDirection(dto.getDirection());
                    }
                }
            }
        }
    }

    /**
     * Creates an instance of {@link Evenement} from a DTO.
     * Used by the client to reconstruct the event scene.
     *
     * @param dto          The DTO describing the event.
     * @param assetManager AssetManager for loading models and visuals.
     * @param space        The physics space to which the event should be attached.
     * @return The corresponding {@link Evenement}, or null if type is unknown.
     */
    public static Evenement createEvenementFromDTO(EvenementDTO dto, AssetManager assetManager, PhysicsSpace space) {
        if (dto.getType().equals("Courant")) {
            return new Courant(dto.getId(), dto.getZoneCenter(), dto.getZoneSize(), dto.getDirection(),
                    dto.getIntensite(), space, assetManager);
        } else if (dto.getType().equals("EntiteMarine")) {

            EntiteMarineInitData initData = new EntiteMarineInitData(
                    dto.getId(),
                    dto.getEntiteType(),
                    dto.getModelPath(),
                    dto.getZoneCenter(),
                    dto.getDirection(),
                    dto.getIntensite());
            return new AjoutEntiteMarineEvent(initData, assetManager);
        } else {
            log.error("Unknown event type: " + dto.getType());
            return null;
        }

    }

    /**
     * Updates the list of active events on the client.
     * Adds new events and removes those that no longer exist on the server.
     *
     * @param eventDTOs List of event DTOs received from the server.
     */
    public void updateEvenements(List<EvenementDTO> eventDTOs) {
        // 1. Retirer les événements qui ne sont plus présents
        List<Integer> idsRecus = eventDTOs.stream().map(EvenementDTO::getId).toList();
        List<Evenement> toRemove = new ArrayList<>();
        for (Evenement ev : new ArrayList<>(Evenement.getEvenements())) {
            if (!idsRecus.contains(ev.getId())) {
                toRemove.add(ev);
            }
        }
        for (Evenement ev : toRemove) {
            ev.retirer();
            log.info("Evenement retiré du client: " + ev.getId());
        }
        // 2. Ajouter les nouveaux événements
        for (EvenementDTO dto : eventDTOs) {
            boolean dejaAjoute = Evenement.getEvenements().stream()
                    .anyMatch(e -> e != null && e.getId() == dto.getId());
            if (dejaAjoute)
                continue;

            // Création de l'événement selon son type
            Evenement event = createEvenementFromDTO(dto, assetManager, space);
            if (event instanceof Courant courant) {
                // Ajout du visuel du courant à la scène
                enqueue(() -> {
                    if (!scene.hasChild(courant.getVisuel())) {
                        scene.attachChild(courant.getVisuel());
                        log.info("Courant attaché à la scène (updateEvenements): " + courant.getId());
                    }
                });
            } else if (event instanceof AjoutEntiteMarineEvent marineEvent) {
                // Ajout de l'entité marine à la scène
                enqueue(() -> {
                    if (!scene.hasChild(marineEvent.getModelNode())) {
                        scene.attachChild(marineEvent.getModelNode());
                        log.info("Entité marine attachée à la scène (updateEvenements): " + marineEvent.getId());
                    }
                });

            }
        }

    }

    public Node getScene() {
        return scene;
    }

    public void ajouterEvenementTest() {

        // Exemple : ajouter un courant
        EvenementDTO courant = EvenementDTO.createEvenementDTO(
                999, // ID unique arbitraire
                new Vector3f(0, 2, -2), // zoneCenter
                new Vector3f(10, 10, 10),
                "Courant", // type
                new Vector3f(0, 0, 1), // direction
                1000f, // intensité
                null); // pas de type d’entité pour un courant);
        client.send(new AjoutEvenementMessage(courant));

    }

    public void retirerEvenement(int id) {
        Evenement event = Evenement.getEvenements().stream()
                .filter(e -> e != null && e.getId() == id)
                .findFirst()
                .orElse(null);
        if (event != null) {
            event.retirer();
            client.send(new RetirerEvenementMessage(id));
        }
    }

    /**
     * Sets the camera for observing the specified drone.
     * Initializes both the chase camera and the FPV camera for this drone.
     *
     * @param drone The drone to observe.
     */
    private void setDroneObservationCamera(Drone drone) {
        Spatial droneSpatial = drone.getNode();
        if (chaseCam != null)
            chaseCam.setEnabled(false);

        if (fpvCamNode != null)
            fpvCamNode.removeFromParent(); // supprimer l'ancienne si existante

        chaseCam = new ChaseCamera(cam, droneSpatial, inputManager);
        chaseCam.setDefaultDistance(10f); // distance par défaut entre la caméra et le drone
        chaseCam.setMaxDistance(20f); // distance max (molette)
        chaseCam.setMinDistance(3f); // distance min (molette)
        chaseCam.setTrailingEnabled(true);

        fpvCamNode = new CameraNode("FPVCam", cam);
        fpvCamNode.setLocalTranslation(new Vector3f(0, 0.2f, 1f));
        ((Node) droneSpatial).attachChild(fpvCamNode);
        fpvCamNode.setEnabled(false); // on commence en mode CHASE
        currentCamMode = CamMode.CHASE;
    }

    /**
     * Handles camera control and observation logic based on input actions.
     * Allows switching between FPV, Chase, and Free cameras, and navigating between
     * drones.
     *
     * @param name      The action name.
     * @param isPressed Whether the key is pressed.
     * @param tpf       Time per frame.
     */
    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (!isPressed)
            return;

        switch (name) {
            case "SwitchCam" -> {
                // Si on était en FREE, désactiver flyCam d'abord
                if (currentCamMode == CamMode.FREE) {
                    flyCam.setEnabled(false);
                }

                // Bascule entre CHASE <-> FPV
                if (currentCamMode == CamMode.CHASE) {
                    if (chaseCam != null)
                        chaseCam.setEnabled(false);
                    if (fpvCamNode != null)
                        fpvCamNode.setEnabled(true);
                    currentCamMode = CamMode.FPV;
                    log.info("Switched to FPV camera");
                } else {
                    if (fpvCamNode != null)
                        fpvCamNode.setEnabled(false);
                    if (chaseCam != null)
                        chaseCam.setEnabled(true);
                    currentCamMode = CamMode.CHASE;
                    log.info("Switched to Chase camera");
                }
            }

            case "FreeCam" -> {
                // Active FreeCam et désactive les autres
                if (chaseCam != null)
                    chaseCam.setEnabled(false);
                if (fpvCamNode != null)
                    fpvCamNode.setEnabled(false);

                flyCam.setEnabled(true);
                flyCam.setMoveSpeed(10f);
                currentCamMode = CamMode.FREE;
                log.info("Switched to Free camera");
            }

            case "NextDrone" -> {
                if (!Drone.getDrones().isEmpty()) {
                    currentObservedIndex = (currentObservedIndex + 1) % Drone.getDrones().size();
                    setDroneObservationCamera(Drone.getDrones().get(currentObservedIndex));
                    log.info("Switched to next drone for observation.");
                }
            }
            case "PrevDrone" -> {
                if (!Drone.getDrones().isEmpty()) {
                    currentObservedIndex = (currentObservedIndex - 1 + Drone.getDrones().size())
                            % Drone.getDrones().size();
                    setDroneObservationCamera(Drone.getDrones().get(currentObservedIndex));
                    log.info("Switched to previous drone for observation.");
                }
            }

        }
    }

}
