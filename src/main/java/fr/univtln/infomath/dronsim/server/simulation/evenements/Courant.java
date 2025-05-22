package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import fr.univtln.infomath.dronsim.server.simulation.drones.Drone;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneServer;
import lombok.Getter;
import java.util.function.BiConsumer;

/**
 * Cette classe représente un événement de type courant dans la simulation.
 * Elle hérite de la classe Evenement et applique une force à un drone dans une
 * direction donnée.
 */
@Getter
public class Courant extends Evenement {
    private final Vector3f direction;
    private final float intensite;
    private final BiConsumer<Drone, Float> effet;

    public Courant(int id, Vector3f zoneCenter, Vector3f zoneSize, Vector3f direction, float intensite,
            PhysicsSpace physicsSpace) {
        super(id, zoneCenter, zoneSize, physicsSpace);
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

    @Override
    public void apply(float tpf) {
        for (Drone d : Drone.getDrones()) {
            if (isInZone(d.getNode().getWorldTranslation())) {
                effet.accept(d, tpf);
            }
        }
    }
}
