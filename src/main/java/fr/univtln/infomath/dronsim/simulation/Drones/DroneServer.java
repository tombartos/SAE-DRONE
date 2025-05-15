package fr.univtln.infomath.dronsim.simulation.Drones;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

import lombok.Getter;

@Getter
public class DroneServer extends Drone {
    private RigidBodyControl control;

    public DroneServer(int id, int clientId, AssetManager assetManager, PhysicsSpace space, DroneModel droneModel,
            Vector3f position, int batteryLevel) {
        super(id, clientId, assetManager, space, droneModel, position, batteryLevel);

        // Création de la collision
        // TODO : Créer une collision shape adaptee au modele du drone
        CollisionShape shape = new BoxCollisionShape(new Vector3f(0.2f, 0.2f, 0.2f));

        control = new RigidBodyControl(shape, weight);
        this.node.addControl(control);
        space.add(control);

        // configure la physique
        this.control.setGravity(Vector3f.ZERO);
        this.control.setLinearDamping(0.9f);
        this.control.setAngularDamping(0.9f);
    }

    public static DroneServer createDrone(int id, int clientId, AssetManager assetManager, PhysicsSpace space,
            DroneModel droneModel, Vector3f position, int batteryLevel) {
        DroneServer drone = new DroneServer(id, clientId, assetManager, space, droneModel, position,
                batteryLevel);
        drones.add(drone);
        return drone;
    }
}
