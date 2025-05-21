package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.math.Vector3f;
import fr.univtln.infomath.dronsim.server.simulation.drones.Drone;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneServer;

import com.jme3.bullet.PhysicsSpace;
import java.util.List;
import java.util.function.BiConsumer;
import lombok.Getter;

@Getter
/**
 * Evenement
 *
 * Cette classe représente un événement dans la simulation.
 *
 * @author infomath
 */
public class Evenement {
    private final int id;
    private Vector3f zoneCenter;
    private Vector3f zoneSize;
    private String type; // ex:"courant"
    private Vector3f direction;
    private float intensite;
    private BiConsumer<Drone, Float> effet;
    private PhysicsSpace physicsSpace;

    public Evenement(int id, Vector3f zoneCenter, Vector3f zoneSize, PhysicsSpace physicsSpace) {
        this.id = id;
        this.zoneCenter = zoneCenter;
        this.zoneSize = zoneSize;
        this.physicsSpace = physicsSpace;
    }

    public void definirCourant(Vector3f direction, float intensite) {
        this.direction = direction.normalize();
        this.intensite = intensite;
        this.type = "courant";

        Vector3f force = this.direction.mult(this.intensite);
        this.effet = (drone, tpf) -> {
            if (drone instanceof DroneServer ds) {
                ds.getBody().applyCentralForce(force);
            }
        };
    }

    public void apply(List<Drone> drones, float tpf) {

        if (effet == null)
            return;
        for (Drone d : drones) {
            if (isInZone(d.getNode().getWorldTranslation())) {
                effet.accept(d, tpf);
            }
        }
    }

    public boolean isInZone(Vector3f position) {
        Vector3f min = zoneCenter.subtract(zoneSize.mult(0.5f));
        Vector3f max = zoneCenter.add(zoneSize.mult(0.5f));
        return position.x >= min.x && position.x <= max.x &&
                position.y >= min.y && position.y <= max.y &&
                position.z >= min.z && position.z <= max.z;
    }

}
