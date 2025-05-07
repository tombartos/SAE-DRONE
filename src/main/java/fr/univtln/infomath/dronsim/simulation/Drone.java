package fr.univtln.infomath.dronsim.simulation;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class Drone {

    private RigidBodyControl control;
    private Node node;

    public Drone(AssetManager assetManager, PhysicsSpace space, String modelPath, String name, Vector3f position,
            float mass) {
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

    public Node getNode() {
        return node;
    }

    public RigidBodyControl getControl() {
        return control;
    }
}
