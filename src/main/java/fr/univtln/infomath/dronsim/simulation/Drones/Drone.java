package fr.univtln.infomath.dronsim.simulation.Drones;

import java.util.ArrayList;
import java.util.List;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
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
    protected Vector3f angular;
    protected final String name;
    protected int weight;
    protected List<Module> modules = new ArrayList<>();

    protected Node node;

    protected Drone(int id, int clientId, AssetManager assetManager, PhysicsSpace space, DroneModel droneModel,
            Vector3f position, Vector3f angular, int batteryLevel) {
        this.id = id;
        this.clientId = clientId;
        this.position = position;
        this.angular = angular;
        this.droneModel = droneModel;
        this.batteryLevel = batteryLevel;
        this.name = droneModel.getName() + "_" + id;
        this.weight = droneModel.getInitialWeight();
        // TODO : Calculer le poids total du drone en fonction de ses modules
        // additionnels (qui ne sont pas prevus de base dans le modele de drone)

        node = new Node(name);
        Spatial model = assetManager.loadModel(droneModel.getModel3DPath());
        // model.rotate(FastMath.HALF_PI, 0f, 0f); // Applique la rotation au modèle,
        // pas au noeud global
        node.attachChild(model);
        node.setLocalTranslation(position); // positionne le node principal

    }

    public static Drone createDrone(int id, int clientId, AssetManager assetManager, PhysicsSpace space,
            DroneModel droneModel, Vector3f position, Vector3f angular, int batteryLevel) {
        Drone drone = new Drone(id, clientId, assetManager, space, droneModel, position, angular,
                batteryLevel);
        drones.add(drone);
        return drone;
    }

    public static List<Drone> getDrones() {
        return drones;
    }

}
