package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.jme3.math.Vector3f;

/**
 * Classe représentant les données d'initialisation d'un événement.
 * Utilisée pour la communication entre le serveur et le client.
 */
@Serializable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EvenementInitData {
    private int id;
    private Vector3f zoneCenter;
    private Vector3f zoneSize;
    private String type;
    private Vector3f direction;
    private float intensite;

}
