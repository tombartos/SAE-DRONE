package fr.univtln.infomath.dronsim.server.manager;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DronesAssociations {
    private static List<DronesAssociations> dronesAssociations;
    private int droneModelId;
    private int pilotId;
    private boolean connectionType; // true = cloud, false = local note: local is not supported yet

    public static void init() {
        dronesAssociations = new ArrayList<>();
    }

    public static void addAssociation(int droneModelId, int pilotId, boolean connectionType) {
        dronesAssociations.add(new DronesAssociations(droneModelId, pilotId, connectionType));
    }

}
