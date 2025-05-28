package fr.univtln.infomath.dronsim.server.simulation.evenements;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;

import fr.univtln.infomath.dronsim.server.simulation.client.SimulatorClient;
import lombok.*;

@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTO {
    private static final Logger log = LoggerFactory.getLogger(EvenementDTO.class);
    public static List<EvenementDTO> evenementsDTOs = new ArrayList<>();
    private int id;
    private Vector3f zoneCenter;
    private Vector3f zoneSize;
    private String type;
    private Vector3f direction;
    private float intensite;
    private String entiteType;
    private String modelPath;

    public static EvenementDTO createEvenementDTO(int id, Vector3f zoneCenter, Vector3f zoneSize, String type,
            Vector3f direction, float intensite, String entiteType) {

        String modelPath;
        if (type == "Courant") {
            modelPath = null;
        } else if (entiteType == "Bateau") {
            modelPath = "bateau/boat_4c.j3o";
        } else if (entiteType == "Poisson") {
            modelPath = "poisson/koi_fish.j3o";
        } else {
            log.error("Unknown entity type: " + entiteType);
            throw new IllegalArgumentException("Unknown entity type: " + entiteType);
        }
        EvenementDTO evenementDTO = new EvenementDTO(
                id,
                zoneCenter,
                zoneSize,
                type,
                direction,
                intensite,
                entiteType,
                modelPath);
        evenementsDTOs.add(evenementDTO);
        return evenementDTO;
    }

}
