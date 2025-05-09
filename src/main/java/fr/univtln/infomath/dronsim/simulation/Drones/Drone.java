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

//TODO : Raccorder au diagramme de classes

public class Drone {
    private static final List<Drone> drones = new ArrayList<>();
    private final int id; // Autoincrement (to be implemented) or gm choice (to be implemented)
    private RigidBodyControl control;
    private Node node;
    private Vector3f position;

    private Drone(int id, AssetManager assetManager, PhysicsSpace space, String modelPath, String name,
            Vector3f position, float mass) {
        this.id = id;
        this.position = position;
        node = new Node(name);
        Spatial model = assetManager.loadModel(modelPath);
        // model.rotate(FastMath.HALF_PI, 0f, 0f); // Applique la rotation au modèle,
        // pas au noeud global
        node.attachChild(model);
        node.setLocalTranslation(position); // positionne le node principal

        // Création de la collision à partir du node déjà positionné
        // CollisionShape shape = CollisionShapeFactory.createDynamicMeshShape(node);
        CollisionShape shape = new BoxCollisionShape(new Vector3f(1f, 0.5f, 1f));

        control = new RigidBodyControl(shape, mass);
        node.addControl(control);
        space.add(control);

        // configure la physique
        control.setGravity(Vector3f.ZERO);
        control.setLinearDamping(0.9f);
        control.setAngularDamping(0.9f);
    }

    public static Drone createDrone(int id, AssetManager assetManager, PhysicsSpace space, String modelPath,
            String name,
            Vector3f position, float mass) {
        Drone drone = new Drone(id, assetManager, space, modelPath, name, position, mass);
        drones.add(drone);
        return drone;
    }

    public Node getNode() {
        return node;
    }

    public RigidBodyControl getControl() {
        return control;
    }

    public Vector3f getPosition() {
        return position;
    }
}
