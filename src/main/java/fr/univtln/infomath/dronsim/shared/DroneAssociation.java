package fr.univtln.infomath.dronsim.shared;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DroneAssociation {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DroneAssociation.class);
    private static List<DroneAssociation> droneAssociations;

    private String droneModelName;
    private String pilotLogin;
    private int connexionMode; // 0: cloud, 1: local

}
