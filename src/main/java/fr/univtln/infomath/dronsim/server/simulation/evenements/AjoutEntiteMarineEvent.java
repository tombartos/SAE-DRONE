package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.math.Vector3f;
import com.jme3.bullet.PhysicsSpace;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarine;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineInitData;
import fr.univtln.infomath.dronsim.server.simulation.entiteMarine.EntiteMarineServer;
import lombok.Getter;

/**
 * Classe représentant un événement d'ajout d'une entité marine dans la
 * simulation.
 * Elle hérite de la classe Evenement.
 */
@Getter
public class AjoutEntiteMarineEvent extends Evenement {
    private final EntiteMarine entite; // Remplace EntiteMarineServer par EntiteMarine

    // Constructeur pour le serveur (avec physique)
    public AjoutEntiteMarineEvent(EntiteMarineInitData initData, AssetManager assetManager, PhysicsSpace space) {
        super(initData.getId(), initData.getPosition(), new Vector3f(10, 10, 150), initData.getDirection(),
                initData.getSpeed(), space);
        this.type = "EntiteMarine";
        this.entite = new EntiteMarineServer(initData.getId(), initData.getType(), assetManager, space,
                initData.getModelPath(), initData.getPosition(), initData.getDirection(), initData.getSpeed());
        evenements.add(this);
    }

    // Constructeur pour le client (SANS physique)
    public AjoutEntiteMarineEvent(EntiteMarineInitData initData, AssetManager assetManager) {
        super(initData.getId(), initData.getPosition(), new Vector3f(10, 10, 150), initData.getDirection(),
                initData.getSpeed(), null);
        this.type = "EntiteMarine";
        this.entite = new EntiteMarine(initData.getId(), initData.getType(), initData.getModelPath(),
                initData.getPosition(), initData.getDirection(), initData.getSpeed(), assetManager);
        evenements.add(this);
    }

    @Override
    public void apply(float tpf) {
        if (entite != null) {
            // Côté client : rien à faire (pas de physique)
            // Côté serveur : entite.update(tpf) si c'est un EntiteMarineServer
            if (entite instanceof EntiteMarineServer serverEntite) {
                serverEntite.update(tpf);
            }
        }
    }

    @Override
    public void retirer() {
        if (entite.getModelNode().getParent() != null) {
            entite.getModelNode().removeFromParent();
        }
        if (entite instanceof EntiteMarineServer serverEntite && physicsSpace != null) {
            physicsSpace.remove(serverEntite.getControl());
        }
        EntiteMarine.getEntites().remove(entite);
        super.retirer();
    }

    public Node getModelNode() {
        return entite.getModelNode();
    }
}
