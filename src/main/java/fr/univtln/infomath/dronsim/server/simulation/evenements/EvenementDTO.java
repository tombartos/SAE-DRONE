package fr.univtln.infomath.dronsim.server.simulation.evenements;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import lombok.*;

@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTO {
    private int id;
    private Vector3f zoneCenter;
    private Vector3f zoneSize;
    private String type;
    private Vector3f direction;
    private float intensite;
}
