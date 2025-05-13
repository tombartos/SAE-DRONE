package fr.univtln.infomath.dronsim.simulation.Drones;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

import lombok.Getter;

@Getter
public class DroneServer extends Drone {
    private RigidBodyControl control;

    public DroneServer(int id, int clientId, AssetManager assetManager, PhysicsSpace space, DroneModel droneModel,
            Vector3f position, Vector3f angular, int batteryLevel) {
        super(id, clientId, assetManager, space, droneModel, position, angular, batteryLevel);

        // Création de la collision à partir du node déjà positionné
        // CollisionShape shape = CollisionShapeFactory.createDynamicMeshShape(node);
        CollisionShape shape = new BoxCollisionShape(new Vector3f(1f, 0.5f, 1f));
        control = new RigidBodyControl(shape, weight);
        node.addControl(control);
        space.add(control);

        // configure la physique
        control.setGravity(Vector3f.ZERO);
        control.setLinearDamping(0.9f);
        control.setAngularDamping(0.9f);
    }

    public static DroneServer createDrone(int id, int clientId, AssetManager assetManager, PhysicsSpace space,
            DroneModel droneModel, Vector3f position, Vector3f angular, int batteryLevel) {
        DroneServer drone = new DroneServer(id, clientId, assetManager, space, droneModel, position, angular,
                batteryLevel);
        drones.add(drone);
        return drone;
    }
}
