package fr.univtln.infomath.dronsim.shared;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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

    public static List<DroneAssociation> getDroneAssociations() {
        return droneAssociations;
    }

    public static DroneAssociation createDroneAssociation(String droneModelName, String pilotLogin, int connexionMode) {
        DroneAssociation droneAssociation = new DroneAssociation(droneAssociations.size(), droneModelName, pilotLogin,
                connexionMode);
        droneAssociations.add(droneAssociation);
        return droneAssociation;
    }

    public static void initDroneAssociations() {
        droneAssociations = new java.util.ArrayList<>();
    }

}
