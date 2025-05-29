package fr.univtln.infomath.dronsim.shared;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Class representing the association between a drone and a pilot.
 * This class is used to manage the relationship between drones and pilots,
 * including their connection modes ( 0 for cloud or 1 for local).
 *
 * @author Tom BARTIER
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DroneAssociation {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DroneAssociation.class);
    private static List<DroneAssociation> droneAssociations;

    private int id;
    private String droneModelName;
    private String pilotLogin;
    private int connexionMode; // 0: cloud, 1: local

    /**
     * Returns the list of all drone associations.
     *
     * @return List of DroneAssociation objects
     */
    public static List<DroneAssociation> getDroneAssociations() {
        return droneAssociations;
    }

    /**
     * Creates a new drone association and adds it to the list.
     *
     * @param droneModelName The name of the drone model
     * @param pilotLogin     The login of the pilot
     * @param connexionMode  The connection mode (0 for cloud, 1 for local)
     * @return The created DroneAssociation object
     */
    public static DroneAssociation createDroneAssociation(String droneModelName, String pilotLogin, int connexionMode) {
        DroneAssociation droneAssociation = new DroneAssociation(droneAssociations.size(), droneModelName, pilotLogin,
                connexionMode);
        droneAssociations.add(droneAssociation);
        return droneAssociation;
    }

    /**
     * Initializes the list of drone associations.
     * This method should be called at the start of the application to ensure
     * that the list is ready for use.
     */
    public static void initDroneAssociations() {
        droneAssociations = new java.util.ArrayList<>();
    }

}
