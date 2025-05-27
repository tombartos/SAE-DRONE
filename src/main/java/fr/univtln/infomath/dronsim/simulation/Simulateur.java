package fr.univtln.infomath.dronsim.simulation;

import java.util.ArrayList;
import java.util.List;
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
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.*;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.*;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.scene.shape.Line;

import fr.univtln.infomath.dronsim.ReferentialNode;
import fr.univtln.infomath.dronsim.control.LocalTestingControler;

public class Simulateur extends SimpleApplication implements PhysicsCollisionListener, ActionListener {

    private List<Drone> drones = new ArrayList<>();
    private List<Evenement> evenements = new ArrayList<>();
    private List<EntiteMarine> entitesMarines = new ArrayList<>();

    private int droneIndex = 0;

    private boolean firstPersonView = true;
    private ChaseCamera chaseCam;

    private BulletAppState bulletState;
    private PhysicsSpace space;
    private FilterPostProcessor fpp;
    private Node scene;
    private LocalTestingControler controleDrone;

    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1024);
        settings.setHeight(768);

        Simulateur app = new Simulateur();
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
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

        // Ajout du terrain
        attachTerrain(scene);

        // Ajout du drone
        Drone droneA = new Drone(
                assetManager,
                space,
                "vehicle/bluerobotics/br2r4/br2-r4-vehicle.j3o",
                "VehicleA",
                new Vector3f(0.0f, -0.0f, 0.0f),
                400f, 1500f);
        scene.attachChild(droneA.getNode());
        drones.add(droneA);

        // Create a referential node
        // ReferentialNode refNodeA = new ReferentialNode(assetManager);
        // droneA.getNode().attachChild(refNodeA);

        // Contrôle clavier du drone A par défaut
        controleDrone = new LocalTestingControler(inputManager, droneA, cam, rootNode);

        Drone droneB = new Drone(
                assetManager,
                space,
                "vehicle/subseatech/guardian/guardian-vehicle.j3o",
                "VehicleB",
                new Vector3f(5.0f, 2.0f, 0.0f),
                200f, 800f);
        scene.attachChild(droneB.getNode());
        drones.add(droneB);
        ReferentialNode refNodeB = new ReferentialNode(assetManager);
        droneB.getNode().attachChild(refNodeB);

        // Lumière et ciel
        initLighting();
        initWater();
        rootNode.attachChild(SkyFactory.createSky(
                assetManager,
                "Textures/Sky/Bright/BrightSky.dds",
                SkyFactory.EnvMapType.CubeMap));

        // Caméra
        cam.setLocation(new Vector3f(4.0f, 4.4f, 2.0f));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        flyCam.setEnabled(false); // désactive la caméra libre

        // ChaseCamera
        chaseCam = new ChaseCamera(cam, droneA.getNode(), inputManager);
        chaseCam.setDefaultDistance(10f);
        chaseCam.setMaxDistance(20f);
        chaseCam.setMinDistance(0.3f);
        chaseCam.setTrailingEnabled(true);
        chaseCam.setDragToRotate(true);
        chaseCam.setEnabled(false); // On commence avec vue interne

        inputManager.addMapping("SWITCH_VIEW", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(this, "SWITCH_VIEW");
        inputManager.addMapping("SWITCH_DRONE", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(this, "SWITCH_DRONE");

        // Evenement eventZone = new Evenement(new Vector3f(0, 2, 0), new Vector3f(15,
        // 15, 15), assetManager, scene);
        // eventZone.definirCourant(new Vector3f(0, 0, 1), 1000);
        // evenements.add(eventZone);

        // Un poisson
        // entitesMarines.add(new EntiteMarine(assetManager, "poisson/fish1.glb", new
        // Vector3f(5, 1, 5),
        // new Vector3f(10, 5, 10), 2f, true));

        // Un bateau
        // entitesMarines.add(new EntiteMarine(assetManager, "bateau/titanic.glb", new
        // Vector3f(-5, 0, 0),
        // new Vector3f(30, 5, 30), 1f, false));

        // Vector3f pointA = new Vector3f(0, 0, 0);
        // Vector3f pointB = new Vector3f(3, 2, 1);

        // VECTEURS POUR BLUEROV2 (trouvés à la main et à l'aide de Blender)
        // VOIR SCHEMA https://www.ardusub.com/quick-start/vehicle-frame.html

        // drawLineBetweenPoints(
        // droneA.getThruster1Pos(),
        // droneA.getThruster1Pos().add(droneA.getThruster1vec()),
        // droneA.getNode(), assetManager, ColorRGBA.Orange);

        // drawLineBetweenPoints(
        // droneA.getThruster2Pos(),
        // droneA.getThruster2Pos().add(droneA.getThruster2vec()),
        // droneA.getNode(), assetManager, ColorRGBA.Magenta);

        // drawLineBetweenPoints(
        // droneA.getThruster3Pos(),
        // droneA.getThruster3Pos().add(droneA.getThruster3vec()),
        // droneA.getNode(), assetManager, ColorRGBA.Cyan);

        // drawLineBetweenPoints(
        // droneA.getThruster4Pos(),
        // droneA.getThruster4Pos().add(droneA.getThruster4vec()),
        // droneA.getNode(), assetManager, ColorRGBA.Gray);

        // drawLineBetweenPoints(
        // droneA.getThruster5Pos(),
        // droneA.getThruster5Pos().add(droneA.getThruster5vec()),
        // droneA.getNode(), assetManager, ColorRGBA.Green);

        // drawLineBetweenPoints(
        // droneA.getThruster6Pos(),
        // droneA.getThruster6Pos().add(droneA.getThruster6vec()),
        // droneA.getNode(), assetManager, ColorRGBA.Red);

    }

    private void attachTerrain(Node parent) {
        Node terrainNode = new Node("Terrain");
        terrainNode.setLocalTranslation(new Vector3f(0.0f, -15.0f, 0.0f));
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
        if (controleDrone != null) {
            controleDrone.update(tpf);
        }

        // Mettre à jour la position de la caméra seulement en vue FPS
        if (firstPersonView) {
            Node camNode = controleDrone.getDrone().getCameraNode();
            cam.setLocation(camNode.getWorldTranslation());
            cam.setRotation(camNode.getWorldRotation());
        }
        // for (Evenement evt : evenements) {
        // evt.apply(drones, tpf);
        // }

        // for (EntiteMarine e : entitesMarines) {
        // e.update(tpf);
        // }

    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        // Rien à faire ici, la physique gère la collision naturellement

    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if ("SWITCH_VIEW".equals(name) && isPressed) {
            firstPersonView = !firstPersonView;
            chaseCam.setEnabled(!firstPersonView);
        }
        if ("SWITCH_DRONE".equals(name) && isPressed) {
            droneIndex = (droneIndex + 1) % drones.size();
            Drone selectedDrone = drones.get(droneIndex);
            controleDrone = new LocalTestingControler(inputManager, selectedDrone, cam, rootNode);
            chaseCam.setSpatial(selectedDrone.getNode());
            System.out.println("Drone sélectionné : " + selectedDrone.getNode().getName());
        }
    }

}
