package fr.univtln.infomath.dronsim.simulation.Drones;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import lombok.Getter;
import lombok.Setter;

@Getter
public class DroneServer extends Drone {
    private RigidBodyControl body;
    @Setter
    private List<Vector3f> thrusterVecs;
    @Setter
    private List<Vector3f> thrusterGlobalPositions;

    public DroneServer(int id, int clientId, AssetManager assetManager, PhysicsSpace space, DroneModel droneModel,
            Vector3f position, int batteryLevel) {
        super(id, clientId, assetManager, space, droneModel, position, batteryLevel);

        // Création de la collision
        // TODO : Créer une collision shape adaptee au modele du drone
        CollisionShape shape = new BoxCollisionShape(new Vector3f(0.2f, 0.2f, 0.2f));

        body = new RigidBodyControl(shape, weight);
        this.node.addControl(body);
        space.add(body);

        // configure la physique
        this.body.setGravity(Vector3f.ZERO);
        this.body.setLinearDamping(0.2f);
        this.body.setAngularDamping(0.99999999999999999999999999999999f);
        // TODO : Faire des tests et adapter les valeurs avec le controler ardusub
    }

    public static DroneServer createDrone(int id, int clientId, AssetManager assetManager, PhysicsSpace space,
            DroneModel droneModel, Vector3f position, int batteryLevel) {
        DroneServer drone = new DroneServer(id, clientId, assetManager, space, droneModel, position,
                batteryLevel);
        drones.add(drone);
        return drone;
    }
}
