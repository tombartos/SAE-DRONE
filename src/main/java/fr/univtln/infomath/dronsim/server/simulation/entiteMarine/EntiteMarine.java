package fr.univtln.infomath.dronsim.server.simulation.entiteMarine;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.math.Vector3f;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a marine entity in the simulation, such as a fish or a boat.
 * Each entity has a 3D model, a position, a direction, and a speed.
 * Entities are stored in a static list for global access and synchronization.
 *
 * @author Emad BA GUBAIR
 * @version 1.0
 */
@Getter
public class EntiteMarine {
    public static final List<EntiteMarine> entites = new ArrayList<>();

    public final int id;
    public final String type; // Exemple : "Poisson", "Bateau"
    @Setter
    public Vector3f direction;
    public float speed;
    public Vector3f positionInitiale;
    @Setter
    public Vector3f positionCourante;
    public Node modelNode;
    public Spatial model;
    public String modelPath;

    public EntiteMarine(int id, String type, String modelPath, Vector3f position, Vector3f direction, float speed,
            AssetManager assetManager) {
        this.id = id;
        this.type = type;
        this.modelPath = modelPath;
        this.direction = direction.normalize();
        this.speed = speed;
        this.positionInitiale = position.clone();
        this.positionCourante = position.clone();

        this.modelNode = new Node(type + "_" + id);
        this.model = assetManager.loadModel(modelPath);
        this.modelNode.attachChild(model);
        this.modelNode.setLocalTranslation(position);

    }

    public static EntiteMarine createEntite(int id, String type, String modelPath, Vector3f position,
            Vector3f direction, float speed, AssetManager assetManager) {
        EntiteMarine entiteMarine = new EntiteMarine(id, type, modelPath, position, direction, speed, assetManager);
        entites.add(entiteMarine);
        return entiteMarine;
    }

    public static List<EntiteMarine> getEntites() {
        return entites;
    }
}
