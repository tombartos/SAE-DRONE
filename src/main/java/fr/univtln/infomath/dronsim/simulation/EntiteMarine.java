package fr.univtln.infomath.dronsim.simulation;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import lombok.Getter;

@Getter
public class EntiteMarine {
    private Spatial model;
    private Vector3f zoneMin;
    private Vector3f zoneMax;
    private Vector3f direction;
    private float speed;
    private boolean aleatoire;

    public EntiteMarine(AssetManager assetManager, String modelPath, Vector3f position, Vector3f zoneSize,
            float speed, boolean aleatoire) {
        this.model = assetManager.loadModel(modelPath);
        this.model.setLocalTranslation(position);

        this.zoneMin = position.subtract(zoneSize.mult(0.5f));
        this.zoneMax = position.add(zoneSize.mult(0.5f));

        this.speed = speed;
        this.aleatoire = aleatoire;

        if (aleatoire) {
            this.direction = new Vector3f(
                    (float) (Math.random() - 0.5),
                    0,
                    (float) (Math.random() - 0.5)).normalizeLocal();
        } else {
            this.direction = Vector3f.UNIT_Z.clone(); // ou une direction définie
        }
    }

    public void update(float tpf) {
        Vector3f pos = model.getLocalTranslation().add(direction.mult(speed * tpf));
        model.setLocalTranslation(pos);

        if (isOutOfZone(pos)) {
            if (aleatoire) {
                // Nouvelle direction aléatoire
                this.direction = new Vector3f(
                        (float) (Math.random() - 0.5),
                        0,
                        (float) (Math.random() - 0.5)).normalizeLocal();
            } else {
                this.direction = this.direction.negate(); // demi-tour pour les bateaux
                model.rotate(0, FastMath.PI, 0);
            }
        }
    }

    private boolean isOutOfZone(Vector3f pos) {
        return pos.x < zoneMin.x || pos.x > zoneMax.x ||
                pos.z < zoneMin.z || pos.z > zoneMax.z;
    }
}
