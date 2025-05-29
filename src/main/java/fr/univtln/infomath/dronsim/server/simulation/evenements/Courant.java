package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneServer;
import lombok.Getter;

/**
 * Represents a current ("Courant") event in the simulation.
 *
 * A current applies a constant directional force to any drone located
 * within its influence zone. It also includes a visual representation
 * using a particle system.
 *
 * Inherits from the {@link Evenement} base class and overrides the
 * {@code apply}
 * method to apply forces on drones.
 *
 * @author Ba gubair
 * @version 1.0
 */
@Getter
public class Courant extends Evenement {
    ParticleEmitter visuel;
    Vector3f force;

    public Courant(int id, Vector3f zoneCenter, Vector3f zoneSize, Vector3f direction, float intensite,
            PhysicsSpace physicsSpace, AssetManager assetManager) {
        super(id, zoneCenter, zoneSize, direction, intensite, physicsSpace);

        this.type = "Courant";
        force = this.direction.mult(intensite);

        visuel = new ParticleEmitter("CourantVisuel", ParticleMesh.Type.Triangle, 100);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        Texture tex = assetManager.loadTexture("Effects/courant.png");
        mat.setTexture("Texture", tex);
        visuel.setMaterial(mat);
        visuel.setImagesX(1);
        visuel.setImagesY(1);
        visuel.setStartColor(new ColorRGBA(0.2f, 0.6f, 0.5f, 0.6f));
        visuel.setEndColor(new ColorRGBA(0.2f, 0.6f, 0.5f, 0.4f));
        visuel.setStartSize(2f);
        visuel.setEndSize(4f);
        visuel.setLowLife(2f);
        visuel.setHighLife(3f);
        visuel.setFacingVelocity(true);
        visuel.getParticleInfluencer().setInitialVelocity(direction.normalize().mult(4f));
        visuel.getParticleInfluencer().setVelocityVariation(0.2f);
        visuel.setLocalTranslation(zoneCenter);

        evenements.add(this);
    }

    @Override
    public void apply(float tpf) {
        for (DroneServer d : DroneServer.getDroneServerList()) {
            if (isInZone(d.getNode().getWorldTranslation())) {
                d.getBody().applyCentralForce(force);
            }
        }
    }

    @Override
    public void retirer() {
        if (visuel != null && visuel.getParent() != null) {
            visuel.removeFromParent();
        }
        super.retirer();
    }
}
