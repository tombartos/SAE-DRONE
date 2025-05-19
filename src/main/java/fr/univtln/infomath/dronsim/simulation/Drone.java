package fr.univtln.infomath.dronsim.simulation;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Drone {

    private RigidBodyControl control;
    private Node node;
    private Node cameraNode;
    private float speed;
    private Vector3f position;
    private AssetManager assetManager;
    Vector3f angular = new Vector3f();
    private List<Vector3f> initialThrusterVecs;
    private List<Vector3f> initialThrusterLocalPosition;
    @Setter
    private List<Vector3f> thrusterVecs;
    @Setter
    private List<Vector3f> thrusterGlobalPositions;

    public Drone(AssetManager assetManager, PhysicsSpace space, String modelPath, String name, Vector3f position,
            float mass, float speed) {

        this.assetManager = assetManager;
        this.node = new Node(name);
        Spatial model = assetManager.loadModel(modelPath);
        model.setLocalTranslation(0, 0.05f, 0);
        this.position = position;
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
        Vector3f dim = new Vector3f(0.2f, 0.14f, 0.2f);
        CollisionShape shape = new BoxCollisionShape(dim);
        // Add this after creating the collision shape and attaching the model
        Geometry debugBox = new Geometry("DebugBox", new Box(dim.x, dim.y, dim.z));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setWireframe(true);
        debugBox.setMaterial(mat);
        node.attachChild(debugBox);

        control = new RigidBodyControl(shape, mass);
        this.node.addControl(control);
        space.add(control);

        // configure la physique
        this.control.setGravity(Vector3f.ZERO);
        this.control.setLinearDamping(0.2f);
        this.control.setAngularDamping(0.99999999999999999999999999999999f);

        // Initial thruster vector
        initialThrusterVecs = new ArrayList<>();
        // Initial thruster position based on the node referencial (local position)
        // WARNING : Can be broken if the node is rotated at creation, need to fix this
        initialThrusterLocalPosition = new ArrayList<>();

        initialThrusterVecs.add(new Vector3f(-0.7431f, 0.0000f, -0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, 0.16f));

        initialThrusterVecs.add(new Vector3f(0.7431f, 0.0000f, -0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, 0.16f));

        initialThrusterVecs.add(new Vector3f(0.7431f, 0.0000f, 0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, -0.16f));

        initialThrusterVecs.add(new Vector3f(-0.7431f, 0.0000f, 0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, -0.16f));

        initialThrusterVecs.add(new Vector3f(0.0000f, 1f, 0f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, 0f));

        initialThrusterVecs.add(new Vector3f(-0.0000f, -1f, 0f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, 0f));

        thrusterVecs = new ArrayList<>();
        for (Vector3f v : initialThrusterVecs) {
            thrusterVecs.add(v.clone());
        }

        thrusterGlobalPositions = new ArrayList<>();
        for (Vector3f v : initialThrusterLocalPosition) {
            thrusterGlobalPositions.add(v.clone());
        }
    }

}
