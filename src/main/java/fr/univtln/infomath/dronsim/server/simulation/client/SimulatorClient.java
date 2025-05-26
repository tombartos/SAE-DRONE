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
import com.jme3.light.*;
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
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneDTOMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.DroneMovementRequestMessage;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake1;
import fr.univtln.infomath.dronsim.server.simulation.jme_messages.Handshake2;
//import fr.univtln.infomath.dronsim.server.viewer.primitives.ReferentialNode;
import fr.univtln.infomath.dronsim.server.utils.GStreamerSender;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private void initWater() {
        WaterFilter waterFilter = new WaterFilter();
        waterFilter.setWaterHeight(3.0f);
        waterFilter.setSpeed(0.5f);

        fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(waterFilter);
        viewPort.addProcessor(fpp);
    }

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
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Rien à faire ici, la physique gère la collision naturellement
    }

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
}
