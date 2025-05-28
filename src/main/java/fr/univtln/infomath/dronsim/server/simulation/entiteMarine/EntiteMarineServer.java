package fr.univtln.infomath.dronsim.server.simulation.entiteMarine;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import lombok.Getter;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;


@Getter
public class EntiteMarineServer extends EntiteMarine {
    private static final Logger log = LoggerFactory.getLogger(EntiteMarineServer.class);

    private RigidBodyControl control;
    private BoxCollisionShape shape;
    private Vector3f zoneMin, zoneMax;
    private final float mass = 200f;

    public EntiteMarineServer(int id, String type, AssetManager assetManager, PhysicsSpace space,
            String modelPath, Vector3f position, Vector3f direction, float speed) {

        super(id, type, modelPath, position, direction, speed, assetManager);
        this.positionInitiale = position.clone();
        this.positionCourante = position.clone();

        Spatial model = assetManager.loadModel(modelPath);
        this.modelNode.attachChild(model);
        this.modelNode.setLocalTranslation(position);

        model.updateGeometricState();
        BoundingBox bbox = (BoundingBox) model.getWorldBound();
        shape = new BoxCollisionShape(bbox.getExtent(null));

        this.control = new RigidBodyControl(shape, mass);
        this.modelNode.addControl(control);
        space.add(control);
        control.setGravity(Vector3f.ZERO);
        control.setLinearDamping(0.9f);
        control.setAngularDamping(0.9f);

        Vector3f half = new Vector3f(200f, 20f, 200f);
        zoneMin = Vector3f.ZERO.subtract(half);
        zoneMax = Vector3f.ZERO.add(half);
    }

    public static EntiteMarineServer createEntite(int id, String type, AssetManager assetManager,
            PhysicsSpace space, String modelPath, Vector3f position, Vector3f direction, float speed) {

        EntiteMarineServer entiteMarine = new EntiteMarineServer(id, type, assetManager, space, modelPath, position,
                direction, speed);
        entites.add(entiteMarine);
        return entiteMarine;
    }

    public void update(float tpf) {
        Vector3f currentPos = control.getPhysicsLocation();
        this.positionCourante = currentPos.clone();
        if (isOutOfZone(currentPos)) {
            control.setPhysicsLocation(positionInitiale.clone());
            // control.setLinearVelocity(Vector3f.ZERO); // stop momentané
        }
        Vector3f velocity = direction.mult(speed);
        control.setLinearVelocity(velocity);

    }

    private boolean isOutOfZone(Vector3f pos) {
        return pos.x < zoneMin.x || pos.x > zoneMax.x ||
                pos.z < zoneMin.z || pos.z > zoneMax.z;
    }

    public Vector3f getPosition() {
        return control.getPhysicsLocation();
    }
}
