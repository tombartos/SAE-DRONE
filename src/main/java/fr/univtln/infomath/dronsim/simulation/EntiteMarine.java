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
            Vector3f zoneSize,
            float speed, boolean aleatoire) {
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
        this.zoneMin = position.subtract(zoneSize.mult(5));
        this.zoneMax = position.add(zoneSize.mult(5f));
        this.speed = speed;
        this.aleatoire = aleatoire;

        this.control = new RigidBodyControl(this.shape, mass);
        this.modelNode.addControl(this.control);
        space.add(this.control);

        // configure la physique
        this.control.setGravity(Vector3f.ZERO);
        this.control.setLinearDamping(0.9f);
        this.control.setAngularDamping(0.9f);

        if (aleatoire) {
            this.direction = new Vector3f(
                    (float) (Math.random() - 0.5),
                    0,
                    (float) (Math.random() - 0.5)).normalizeLocal();
        } else {
            this.direction = Vector3f.UNIT_Z.clone(); // ou une direction définie
        }
    }

    public void update(float tpf) {
        Vector3f currentPos = this.control.getPhysicsLocation();

        // Vérifier si hors zone
        if (isOutOfZone(currentPos)) {
            this.control.setPhysicsLocation(positionInitiale.clone());
            this.control.setLinearVelocity(Vector3f.ZERO);

            if (aleatoire) {
                this.direction = new Vector3f(
                        (float) (Math.random() - 0.5),
                        0,
                        (float) (Math.random() - 0.5)).normalizeLocal();
            }
        }

        // Appliquer la direction comme vitesse
        Vector3f velocity = this.direction.mult(this.speed);
        this.control.setLinearVelocity(velocity);
    }

    private boolean isOutOfZone(Vector3f pos) {
        return pos.x < this.zoneMin.x || pos.x > this.zoneMax.x ||
                pos.z < this.zoneMin.z || pos.z > this.zoneMax.z;
    }
}
