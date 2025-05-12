package fr.univtln.infomath.dronsim.simulation;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.Getter;

@Getter
public class Drone {

    private RigidBodyControl control;
    private Node node;
    private Node cameraNode;
    private float speed;
    Vector3f angular = new Vector3f();

    public Drone(AssetManager assetManager, PhysicsSpace space, String modelPath, String name, Vector3f position,
            float mass, float speed) {

        this.node = new Node(name);
        Spatial model = assetManager.loadModel(modelPath);
        // model.rotate(FastMath.HALF_PI, 0f, 0f); // Applique la rotation au modèle,
        // pas au noeud global
        this.node.attachChild(model);
        this.node.setLocalTranslation(position); // positionne le node principal
        this.speed = speed;

        // Ajoute un point pour la caméra à l'avant du drone
        this.cameraNode = new Node("CameraNode");
        this.cameraNode.setLocalTranslation(0, 0.2f, 1f);
        this.node.attachChild(this.cameraNode);

        // Création de la collision
        CollisionShape shape = new BoxCollisionShape(new Vector3f(0.2f, 0.2f, 0.2f));

        control = new RigidBodyControl(shape, mass);
        this.node.addControl(control);
        space.add(control);

        // configure la physique
        this.control.setGravity(Vector3f.ZERO);
        this.control.setLinearDamping(0.9f);
        this.control.setAngularDamping(0.9f);

    }

}
