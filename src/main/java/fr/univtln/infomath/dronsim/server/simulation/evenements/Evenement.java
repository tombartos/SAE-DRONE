package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a physical event that affects drones
 * in the simulation. Each event is defined by its zone of influence, direction,
 * intensity, and physical context.
 *
 * Subclasses must implement the {@code apply(float tpf)} method
 * which describes how the event influences entities per simulation frame.
 *
 * @author Emad BA GUABAIR
 * @version 1.0
 */
@Getter
public abstract class Evenement {
    protected final int id;
    protected Vector3f zoneCenter;
    protected Vector3f zoneSize;
    protected String type;
    protected Vector3f direction;
    private float intensite;
    protected PhysicsSpace physicsSpace;

    public static final List<Evenement> evenements = new ArrayList<>();

    public Evenement(int id, Vector3f zoneCenter, Vector3f zoneSize, Vector3f direction, float intensite,
            PhysicsSpace physicsSpace) {
        this.id = id;
        this.zoneCenter = zoneCenter;
        this.zoneSize = zoneSize;
        this.direction = direction;
        this.intensite = intensite;
        this.physicsSpace = physicsSpace;
    }

    /**
     * Abstract method that applies the event's effect.
     * This must be implemented by subclasses to define
     * the behavior of the event during each simulation frame.
     *
     * @param tpf Time per frame (used for frame-dependent updates)
     */
    public abstract void apply(float tpf);

    /**
     * Checks whether a given position is inside the zone of influence of this
     * event.
     *
     * @param position The position to check
     * @return {@code true} if the position is within the zone, {@code false}
     *         otherwise
     */
    public boolean isInZone(Vector3f position) {
        Vector3f min = zoneCenter.subtract(zoneSize.mult(0.5f));
        Vector3f max = zoneCenter.add(zoneSize.mult(0.5f));
        return position.x >= min.x && position.x <= max.x &&
                position.y >= min.y && position.y <= max.y &&
                position.z >= min.z && position.z <= max.z;
    }

    public static List<Evenement> getEvenements() {
        return evenements;
    }

    public static void ajouterEvenement(Evenement event) {
        evenements.add(event);
    }

    public void retirer() {
        evenements.remove(this);
    }
}
