package fr.univtln.infomath.dronsim.server.simulation.evenements;

import java.util.List;
import java.util.ArrayList;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import lombok.*;

/**
 * Data Transfer Object (DTO) representing an event in the simulation.
 *
 * This class is used for communication between the server and client,
 * carrying all relevant data to reconstruct the event on either side
 * of the network.
 *
 * @author Ba gubair
 * @version 1.0
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTO {

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
            Vector3f direction, float intensite, String entiteType,
            String modelPath) {
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
