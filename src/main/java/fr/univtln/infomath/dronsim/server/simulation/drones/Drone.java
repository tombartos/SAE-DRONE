package fr.univtln.infomath.dronsim.server.simulation.drones;

import java.util.ArrayList;
import java.util.List;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Drone {
    protected static List<Drone> drones = new ArrayList<>();
    protected final int id; // Autoincrement (to be implemented) or gm choice (to be implemented)
    protected final int clientId;
    protected final DroneModel droneModel;
    @Setter
    protected int batteryLevel;
    @Setter
    protected Vector3f position;
    @Setter
    protected Quaternion angular;
    protected final String name;
    protected int weight;
    protected List<Module> modules = new ArrayList<>();
    @Setter
    protected List<Float> motors_speeds;
    protected Node node;
    protected Spatial model;

    protected Drone(int id, int clientId, AssetManager assetManager, PhysicsSpace space, DroneModel droneModel,
            Vector3f position, int batteryLevel) {
        this.id = id;
        this.clientId = clientId;
        this.position = position;
        this.droneModel = droneModel;
        this.batteryLevel = batteryLevel;
        this.name = droneModel.getName() + "_" + id;
        this.weight = droneModel.getInitialWeight();
        this.motors_speeds = new ArrayList<>();
        for (int i = 0; i < droneModel.getNbMotors(); i++) {
            motors_speeds.add(0f);
        }

        // TODO : Calculer le poids total du drone en fonction de ses modules
        // additionnels (qui ne sont pas prevus de base dans le modele de drone)

        node = new Node(name);
        model = assetManager.loadModel(droneModel.getModel3DPath());
        model.setLocalTranslation(0, 0.05f, 0); // positionne le model par rapport à la collision shape
        node.attachChild(model);
        node.setLocalTranslation(position); // positionne le node principal
        this.angular = node.getLocalRotation(); // positionne le node principal

    }

    public static Drone createDrone(int id, int clientId, AssetManager assetManager, PhysicsSpace space,
            DroneModel droneModel, Vector3f position, int batteryLevel) {
        Drone drone = new Drone(id, clientId, assetManager, space, droneModel, position, batteryLevel);
        drones.add(drone);
        return drone;
    }

    public static List<Drone> getDrones() {
        return drones;
    }

}
