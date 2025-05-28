package fr.univtln.infomath.dronsim.server.simulation.client;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.light.*;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import com.jme3.math.*;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.*;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;

import com.jme3.input.ChaseCamera;

import fr.univtln.infomath.dronsim.server.simulation.control.LocalTestingControler;
import fr.univtln.infomath.dronsim.server.simulation.drones.Drone;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneDTO;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneInitData;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarine;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineDTO;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineInitData;
import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneMovementRequestMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.EntiteMarineDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.EvenementDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake1;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake2;
import fr.univtln.infomath.dronsim.server.utils.GStreamerSender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class SimulatorClient extends SimpleApplication implements PhysicsCollisionListener {
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
    private Map<Integer, Spatial> evenementsVisuels = new HashMap<>();
    private List<EvenementDTO> pendingEventDTOs = new ArrayList<>(); // buffer pour les événements reçus trop tôt
    private boolean sceneReady = false;

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

        viewPort.addProcessor(frameCaptureProcessor); // Ajout du processeur de capture d'images
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
            scene.attachChild(tmpDrone.getNode());
            if (tmpDrone.getId() == handshake2.getYourDroneId()) {
                yourDrone = tmpDrone;
            }
        }

        if (yourDrone == null) {
            log.error("Your drone is not found in the list of drones");
            log.error("Available drones: " + Drone.getDrones().toString());
            log.error("Your client ID: " + handshake2.getYourDroneId());
            System.exit(1);
        } else {
            // ChaseCamera
            // TODO : A remplacer par la first person camera
            Spatial droneSpatial = yourDrone.getNode();
            ChaseCamera chaseCam = new ChaseCamera(cam, droneSpatial, inputManager);
            chaseCam.setDefaultDistance(10f); // distance par défaut entre la caméra et le drone
            chaseCam.setMaxDistance(20f); // distance max (molette)
            chaseCam.setMinDistance(3f); // distance min (molette)
            // chaseCam.setSmoothMotion(true); // mouvement fluide
            chaseCam.setTrailingEnabled(true); // caméra suit le mouvement
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
            scene.attachChild(entite.getModelNode());
        }

        sceneReady = true;
        if (!pendingEventDTOs.isEmpty()) {
            updateEvenements(pendingEventDTOs);
            pendingEventDTOs.clear();
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

    public void updateEntitesMarine(List<EntiteMarineDTO> dtos) {
        for (EntiteMarine entite : EntiteMarine.getEntites()) {
            for (EntiteMarineDTO dto : dtos) {
                if (entite.getId() == dto.getId()) {
                    entite.setPositionCourante(dto.getPosition());
                    entite.setDirection(dto.getDirection());
                }
            }
        }
    }

    public void updateEvenements(List<EvenementDTO> eventDTOs) {
        for (EvenementDTO eventDTO : eventDTOs) {
            if (!evenementsVisuels.containsKey(eventDTO.getId())) {
                ParticleEmitter courant = createCourantVisuel(eventDTO.getZoneCenter(), eventDTO.getDirection());
                evenementsVisuels.put(eventDTO.getId(), courant);
                enqueue(() -> {
                    // Cela garantit que scene.attachChild(...) est exécuté dans le thread principal
                    // de JME
                    scene.attachChild(courant);
                    return null;
                });
            }
        }
    }

    private ParticleEmitter createCourantVisuel(Vector3f pos, Vector3f direction) {
        ParticleEmitter courantVisuel = new ParticleEmitter("CourantVisuel", ParticleMesh.Type.Triangle, 300);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        Texture tex = assetManager.loadTexture("Effects/courant.png");
        mat.setTexture("Texture", tex);
        courantVisuel.setMaterial(mat);
        courantVisuel.setImagesX(1);
        courantVisuel.setImagesY(1);
        courantVisuel.setStartColor(new ColorRGBA(0.2f, 0.6f, 0.5f, 0.6f));
        courantVisuel.setEndColor(new ColorRGBA(0.2f, 0.6f, 0.5f, 0.4f));
        courantVisuel.setStartSize(2f);
        courantVisuel.setEndSize(4f);
        courantVisuel.setLowLife(2f);
        courantVisuel.setHighLife(3f);
        // courantVisuel.setParticlesPerSec(100f); // Plus de particules par seconde
        // courantVisuel.setNumParticles(600);
        courantVisuel.setFacingVelocity(true);
        courantVisuel.getParticleInfluencer().setInitialVelocity(direction.normalize().mult(4f));
        courantVisuel.getParticleInfluencer().setVelocityVariation(0.2f);
        courantVisuel.setLocalTranslation(pos);
        return courantVisuel;
    }

    public Node getScene() {
        return scene;
    }

    public boolean isSceneReady() {
        return sceneReady;
    }

    public void bufferEvenements(List<EvenementDTO> dtos) {
        pendingEventDTOs.addAll(dtos);
    }

}
