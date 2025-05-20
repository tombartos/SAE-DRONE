package fr.univtln.infomath.dronsim.simulation;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.Node;
import lombok.Getter;

@Getter
public class EntiteMarine {
    private Node modelNode;
    private Spatial model;
    private RigidBodyControl control;
    private BoxCollisionShape shape;
    private float mass = 200f;
    private Vector3f zoneMin;
    private Vector3f zoneMax;
    private Vector3f positionInitiale;
    private Vector3f direction;
    private float speed;
    private boolean aleatoire;

    public EntiteMarine(AssetManager assetManager, PhysicsSpace space, String modelPath, Vector3f position,
            float speed) {
        this.modelNode = new Node("EntiteMarine");
        this.model = assetManager.loadModel(modelPath);
        this.modelNode.attachChild(this.model); // important pour que worldBound soit à jour
        this.modelNode.updateGeometricState(); // force le recalcul de bounding box

        // Création de la collision
        BoundingBox bbox = (BoundingBox) model.getWorldBound();
        Vector3f extent = bbox.getExtent(null);
        this.shape = new BoxCollisionShape(extent);

        this.modelNode.setLocalTranslation(position);

        this.positionInitiale = position.clone();
        this.speed = speed;
        this.direction = Vector3f.UNIT_Z.clone(); // ou une direction définie

        Vector3f zoneCenter = Vector3f.ZERO;
        Vector3f halfSize = new Vector3f(200f, 30f, 200f);
        this.zoneMin = zoneCenter.subtract(halfSize);
        this.zoneMax = zoneCenter.add(halfSize);

        this.control = new RigidBodyControl(this.shape, mass);
        this.modelNode.addControl(this.control);
        space.add(this.control);

        // configure la physique
        this.control.setGravity(Vector3f.ZERO);
        this.control.setLinearDamping(0.9f);
        this.control.setAngularDamping(0.9f);

    }

    public void update(float tpf) {
        Vector3f currentPos = this.control.getPhysicsLocation();

        // Vérifier si hors zone
        if (isOutOfZone(currentPos)) {
            this.control.setPhysicsLocation(this.positionInitiale.clone());
            this.control.setLinearVelocity(Vector3f.ZERO); // stop momentané
        } else {
            Vector3f velocity = this.direction.mult(this.speed);
            this.control.setLinearVelocity(velocity);
        }

    }

    private boolean isOutOfZone(Vector3f pos) {
        return pos.x < this.zoneMin.x || pos.x > this.zoneMax.x ||
                pos.z < this.zoneMin.z || pos.z > this.zoneMax.z;
    }
}
