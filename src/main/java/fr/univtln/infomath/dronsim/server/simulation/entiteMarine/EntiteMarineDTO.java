package fr.univtln.infomath.dronsim.server.simulation.entiteMarine;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import lombok.*;

/**
 * Data Transfer Object (DTO) representing a marine entity.
 * This class is used to transfer simplified entity data (ID, type, position,
 * and direction)
 * between client and server in a networked simulation context.
 *
 * @author Emad BA GUBAIR
 * @version 1.0
 */
@Serializable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntiteMarineDTO {
    public static List<EntiteMarineDTO> entitesMarineDTOs = new ArrayList<>();
    private int id;
    private String type;
    private Vector3f position;
    private Vector3f direction;

    public static EntiteMarineDTO createEntiteMarineDTO(EntiteMarine entiteMarine) {
        EntiteMarineDTO entiteMarineDTO = new EntiteMarineDTO(entiteMarine.getId(), entiteMarine.getType(),
                entiteMarine.getPositionCourante(), entiteMarine.getDirection());
        entitesMarineDTOs.add(entiteMarineDTO);
        return entiteMarineDTO;
    }
}
