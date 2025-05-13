package fr.univtln.infomath.dronsim.simulation.Drones;

import java.util.ArrayList;
import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Drone {
    private static List<Drone> drones = new ArrayList<>();
    private final int id; // Autoincrement (to be implemented) or gm choice (to be implemented)
    private final int clientId;
    private final DroneModel droneModel;
    @Setter
    private int batteryLevel;
    @Setter
    private Vector3f position;
    @Setter
    private Vector3f angular;
    private final String name;
    private int weight;
    private List<Module> modules = new ArrayList<>();

    private RigidBodyControl control;
    private Node node;

    private Drone(int id, int clientId, AssetManager assetManager, PhysicsSpace space, DroneModel droneModel,
            Vector3f position, Vector3f angular, int batteryLevel) {
        this.id = id;
        this.clientId = clientId;
        this.position = position;
        this.angular = angular;
        this.droneModel = droneModel;
        this.batteryLevel = batteryLevel;
        this.name = droneModel.getName() + "_" + id;
        this.weight = droneModel.getInitialWeight();
        // TODO : Calculer le poids total du drone en fonction de ses modules
        // additionnels (qui ne sont pas prevus de base dans le modele de drone)

        node = new Node(name);
        Spatial model = assetManager.loadModel(droneModel.getModel3DPath());
        // model.rotate(FastMath.HALF_PI, 0f, 0f); // Applique la rotation au modèle,
        // pas au noeud global
        node.attachChild(model);
        node.setLocalTranslation(position); // positionne le node principal

        // Création de la collision à partir du node déjà positionné
        // CollisionShape shape = CollisionShapeFactory.createDynamicMeshShape(node);
        CollisionShape shape = new BoxCollisionShape(new Vector3f(1f, 0.5f, 1f));

        control = new RigidBodyControl(shape, weight);
        node.addControl(control);
        space.add(control);

        // configure la physique
        control.setGravity(Vector3f.ZERO);
        control.setLinearDamping(0.9f);
        control.setAngularDamping(0.9f);
    }

    public static Drone createDrone(int id, int clientId, AssetManager assetManager, PhysicsSpace space,
            DroneModel droneModel, Vector3f position, Vector3f angular, int batteryLevel) {
        Drone drone = new Drone(id, clientId, assetManager, space, droneModel, position, angular,
                batteryLevel);
        drones.add(drone);
        return drone;
    }

    public static List<Drone> getDrones() {
        return drones;
    }

}
