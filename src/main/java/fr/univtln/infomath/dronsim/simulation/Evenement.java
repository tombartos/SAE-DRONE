package fr.univtln.infomath.dronsim.simulation;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.scene.Node;
import com.jme3.math.Vector3f;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import java.util.List;
import java.util.function.BiConsumer;

public class Evenement {

    private Vector3f zoneCenter;
    private Vector3f zoneSize;
    private BiConsumer<Drone, Float> effet; // effet appliqué si drone dans la zone

    private Node scene; // pour attacher l'effet visuel
    private AssetManager assetManager;
    private PhysicsSpace physicsSpace;

    public Evenement(Vector3f zoneCenter, Vector3f zoneSize, AssetManager assetManager, Node scene,
            PhysicsSpace physicsSpace) {
        this.zoneCenter = zoneCenter;
        this.zoneSize = zoneSize;
        this.assetManager = assetManager;
        this.scene = scene;
        this.physicsSpace = physicsSpace;
    }

    public void definirCourant(Vector3f direction, float intensite) {
        Vector3f force = direction.normalize().mult(intensite);
        this.effet = (drone, tpf) -> {
            drone.getControl().applyCentralForce(force);
        };

        // Effet visuel de courant
        ParticleEmitter courant = new ParticleEmitter("CourantVisuel", ParticleMesh.Type.Triangle, 300);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        Texture tex = assetManager.loadTexture("Effects/courant.png");
        mat.setTexture("Texture", tex);
        courant.setMaterial(mat);
        courant.setImagesX(1);
        courant.setImagesY(1);
        courant.setStartColor(new ColorRGBA(0.4f, 0.7f, 1f, 0.4f));
        courant.setEndColor(new ColorRGBA(0.4f, 0.7f, 1f, 0.0f));
        courant.setStartSize(2f);
        courant.setEndSize(3f);
        courant.setLowLife(2f);
        courant.setHighLife(4f);
        courant.setFacingVelocity(true);
        courant.getParticleInfluencer().setInitialVelocity(direction.normalize().mult(4f));
        courant.getParticleInfluencer().setVelocityVariation(0.2f);

        courant.setLocalTranslation(zoneCenter);

        scene.attachChild(courant);
    }

    public EntiteMarine ajouterEntiteMarine(String modelPath, float speed) {
        Vector3f position = zoneCenter.clone(); // position = centre de la zone
        EntiteMarine entite = new EntiteMarine(this.assetManager, this.physicsSpace, modelPath, position,
                speed);

        scene.attachChild(entite.getModelNode());
        return entite;
    }

    public boolean isInZone(Vector3f position) {
        Vector3f min = zoneCenter.subtract(zoneSize.mult(0.5f));
        Vector3f max = zoneCenter.add(zoneSize.mult(0.5f));
        return position.x >= min.x && position.x <= max.x &&
                position.y >= min.y && position.y <= max.y &&
                position.z >= min.z && position.z <= max.z;
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
}
