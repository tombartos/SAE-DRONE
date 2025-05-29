package fr.univtln.infomath.dronsim.server.simulation.drones;

import java.util.List;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import fr.univtln.infomath.dronsim.server.simulation.control.Controler;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

/**
 * Represents a drone in the server-side simulation.
 * <p>
 * This class extends the Drone class and is responsible for managing the
 * drone's physics, thrusters, and control logic on the server side.
 * </p>
 *
 * @author Tom BARTIER
 * @author Yann ROBLIN
 */
@Getter
public class DroneServer extends Drone {
    private static List<DroneServer> droneServerList = new ArrayList<>();
    @Setter
    private Controler controler;
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
        this.body.setLinearDamping(0.2f);
        this.body.setAngularDamping(0.9f);

        // Création de Nodes pour représenter les moteurs.
        // TODO : Modifier pour faire fonctionner avec n'importe quelle type de drone

        for (int i = 0; i < droneModel.getInitialThrusterLocalPosition().size(); i++) {
            this.node.attachChild(new Node("ThrusterNode" + i));
            this.node.getChild("ThrusterNode" + i).lookAt(droneModel.getInitialThrusterVecs().get(i).add(position),
                    Vector3f.UNIT_Y);
            this.node.getChild("ThrusterNode" + i)
                    .setLocalTranslation(droneModel.getInitialThrusterLocalPosition().get(i));
        }

        this.controler = controler;
    }

    public static DroneServer createDrone(int id, int clientId, AssetManager assetManager, PhysicsSpace space,
            DroneModel droneModel, Vector3f position, int batteryLevel, Controler controler) {
        DroneServer drone = new DroneServer(id, clientId, assetManager, space, droneModel, position,
                batteryLevel, controler);
        droneServerList.add(drone);
        return drone;
    }

    public static List<DroneServer> getDroneServerList() {
        return droneServerList;
    }
}
