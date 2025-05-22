package fr.univtln.infomath.dronsim.server.simulation.evenements;

import java.util.List;
import java.util.ArrayList;

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

    public static EvenementDTO createEvenementDTO(Evenement evenement) {
        if (evenement instanceof Courant courant) {
            return new EvenementDTO(
                    courant.getId(),
                    courant.getZoneCenter(),
                    courant.getZoneSize(),
                    courant.getType(),
                    courant.getDirection(),
                    courant.getIntensite());
        } else {
            return new EvenementDTO(
                    evenement.getId(),
                    evenement.getZoneCenter(),
                    evenement.getZoneSize(),
                    evenement.getType(),
                    null,
                    0f);
        }
    }

}
