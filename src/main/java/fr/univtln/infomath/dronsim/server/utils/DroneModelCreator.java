package fr.univtln.infomath.dronsim.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class is a utility class to serialize a list of DroneModel objects to a
 * JSON file that will be read by the jME server at each startup.
 */
public class DroneModelCreator {

    public void saveDroneModels(List<DroneModel> models, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), models);
            System.out.println("Drone models saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
