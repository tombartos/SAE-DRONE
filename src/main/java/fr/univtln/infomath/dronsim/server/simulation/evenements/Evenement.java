package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe centralise tous les événements physiques appliqués aux entités
 * ou drones.
 */
@Getter
public abstract class Evenement {
    protected final int id;
    protected Vector3f zoneCenter;
    protected Vector3f zoneSize;
    protected String type;
    protected PhysicsSpace physicsSpace;

    public static final List<Evenement> evenements = new ArrayList<>();

    public Evenement(int id, Vector3f zoneCenter, Vector3f zoneSize, PhysicsSpace physicsSpace) {
        this.id = id;
        this.zoneCenter = zoneCenter;
        this.zoneSize = zoneSize;
        this.physicsSpace = physicsSpace;
        evenements.add(this);
    }

    public abstract void apply(float tpf);

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
}
