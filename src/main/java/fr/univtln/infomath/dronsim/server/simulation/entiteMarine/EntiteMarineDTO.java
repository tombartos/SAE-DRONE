package fr.univtln.infomath.dronsim.server.simulation.entiteMarine;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector3f;
import com.jme3.network.serializing.Serializable;
import lombok.*;

/**
 * EntiteMarineDTO is a Data Transfer Object for marine entities in the
 * simulation.
 * It contains the entity's ID, type, position, and direction.
 * It also provides a static method to create and store instances of this DTO.
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
