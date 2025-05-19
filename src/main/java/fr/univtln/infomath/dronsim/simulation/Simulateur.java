package fr.univtln.infomath.dronsim.simulation;

import java.util.ArrayList;
import java.util.List;

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

import fr.univtln.infomath.dronsim.control.LocalTestingControler;
//import fr.univtln.infomath.dronsim.viewer.primitives.ReferentialNode;

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
                new Vector3f(0.0f, 2.0f, 0.0f),
                200f, 1500f);
        scene.attachChild(droneA.getNode());
        drones.add(droneA);
        // Contrôle clavier du drone A par défaut
        controleDrone = new LocalTestingControler(inputManager, droneA, cam);

        Drone droneB = new Drone(
                assetManager,
                space,
                "vehicle/subseatech/guardian/guardian-vehicle.j3o",
                "VehicleB",
                new Vector3f(3.0f, 2.0f, 0.0f),
                200f, 800f);
        scene.attachChild(droneB.getNode());
        drones.add(droneB);

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
        chaseCam.setMinDistance(3f);
        chaseCam.setSmoothMotion(true);
        chaseCam.setTrailingEnabled(true);
        chaseCam.setDragToRotate(false);
        chaseCam.setEnabled(false); // On commence avec vue interne

        inputManager.addMapping("SWITCH_VIEW", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(this, "SWITCH_VIEW");
        inputManager.addMapping("SWITCH_DRONE", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(this, "SWITCH_DRONE");

        Evenement eventZone = new Evenement(new Vector3f(0, 2, 0), new Vector3f(15,
                15, 15), assetManager, scene);
        eventZone.definirCourant(new Vector3f(0, 0, 1), 1000);
        evenements.add(eventZone);

        // Un poisson
        entitesMarines.add(new EntiteMarine(assetManager, space, "poisson/fish.glb", new Vector3f(0, -8, 20),
                new Vector3f(1, 0, 20), 2f, false));
        entitesMarines.add(new EntiteMarine(assetManager, space, "poisson/gold_fish.glb", new Vector3f(5, -6, 15),
                new Vector3f(1, 0, 20), 2f, false));
        entitesMarines.add(new EntiteMarine(assetManager, space, "poisson/koi_fish.glb", new Vector3f(-5, -10, 25),
                new Vector3f(1, 0, 20), 2f, false));
        entitesMarines.add(new EntiteMarine(assetManager, space, "poisson/model_50a_-_hawksbill_sea_turtle.glb",
                new Vector3f(10, -4, 20),
                new Vector3f(1, 0, 20), 2f, false));
        entitesMarines.add(new EntiteMarine(assetManager, space, "poisson/octopus.glb", new Vector3f(-10, -10, 20),
                new Vector3f(1, 0, 20), 2f, false));
        entitesMarines.add(new EntiteMarine(assetManager, space, "poisson/school_of_fish.glb", new Vector3f(15, -7, 20),
                new Vector3f(1, 0, 20), 2f, false));
        entitesMarines
                .add(new EntiteMarine(assetManager, space, "poisson/the_fish_particle.glb", new Vector3f(-8, -6, 25),
                        new Vector3f(1, 0, 20), 2f, false));
        entitesMarines.add(new EntiteMarine(assetManager, space, "bateau/boat_4c.glb", new Vector3f(15, 5, 40),
                new Vector3f(1, 0, 20), 2f, false));
        entitesMarines.add(new EntiteMarine(assetManager, space, "bateau/speedboat_n2.glb", new Vector3f(8, 3.5f, 30),
                new Vector3f(1, 0, 20), 1f, false));

        for (EntiteMarine e : entitesMarines) {
            scene.attachChild(e.getModelNode());
        }

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
        for (Evenement evt : evenements) {
            evt.apply(drones, tpf);
        }

        for (EntiteMarine e : entitesMarines) {
            e.update(tpf);
        }

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
            controleDrone = new LocalTestingControler(inputManager, selectedDrone, cam);
            chaseCam.setSpatial(selectedDrone.getNode());
            System.out.println("Drone sélectionné : " + selectedDrone.getNode().getName());
        }
    }

}
