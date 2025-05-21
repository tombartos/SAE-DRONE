package fr.univtln.infomath.dronsim.server.simulation.drones;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

import fr.univtln.infomath.dronsim.server.simulation.control.Controler;
import lombok.Getter;
import lombok.Setter;

@Getter
public class DroneServer extends Drone {
    private final Controler controler;
    private RigidBodyControl body;
    @Setter
    private List<Vector3f> thrusterVecs;
    @Setter
    private List<Vector3f> thrusterGlobalPositions;

    public DroneServer(int id, int clientId, AssetManager assetManager, PhysicsSpace space, DroneModel droneModel,
            Vector3f position, int batteryLevel, Controler controler) {
        super(id, clientId, assetManager, space, droneModel, position, batteryLevel);

        // Création de la collision
        BoundingBox bbox = (BoundingBox) model.getWorldBound();
        Vector3f extent = bbox.getExtent(null);
        BoxCollisionShape shape = new BoxCollisionShape(extent);
        body = new RigidBodyControl(shape, weight);
        this.node.addControl(body);
        space.add(body);

        // configure la physique
        this.body.setGravity(new Vector3f(0f, 0f, 0f));
        this.body.setLinearDamping(0.4f);
        this.body.setAngularDamping(0.99f);
        // TODO : Faire des tests et adapter les valeurs avec le controler ardusub

        this.controler = controler;
    }

    public static DroneServer createDrone(int id, int clientId, AssetManager assetManager, PhysicsSpace space,
            DroneModel droneModel, Vector3f position, int batteryLevel, Controler controler) {
        DroneServer drone = new DroneServer(id, clientId, assetManager, space, droneModel, position,
                batteryLevel, controler);
        drones.add(drone);
        return drone;
    }
}
