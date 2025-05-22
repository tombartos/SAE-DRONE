package fr.univtln.infomath.dronsim.server.simulation.entiteMarine;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Classe représentant les données d'initialisation d'une entité marine.
 * Utilisée pour la communication entre le serveur et le client.
 */
@Serializable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EntiteMarineInitData {
    private int id;
    private String type;
    private String modelPath;
    private Vector3f position;
    private Vector3f direction;
    private float speed;
}
