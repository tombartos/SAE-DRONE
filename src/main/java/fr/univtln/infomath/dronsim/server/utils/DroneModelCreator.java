package fr.univtln.infomath.dronsim.server.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jme3.math.Vector3f;

import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a utility class to serialize a list of DroneModel objects to a
 * JSON file that will be read by the jME server at each startup. You can launch
 * this class directly.
 *
 * @author Tom BARTIER
 */
public class DroneModelCreator {

    /**
     * Saves a list of DroneModel objects to a specified JSON file.
     *
     * @param models   The list of DroneModel objects to save.
     * @param filePath The path to the JSON file where the models will be saved.
     */
    public void saveDroneModels(List<DroneModel> models, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), models);
            System.out.println("Drone models saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to create and save drone models.
     * This method initializes a list of DroneModel objects and saves them to a JSON
     * file.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        DroneModelCreator creator = new DroneModelCreator();
        ArrayList<DroneModel> models = new ArrayList<>();

        // BlueROV2
        // Initial thruster positions and vectors initialization for BlueROV2
        List<Vector3f> initialThrusterVecs = new ArrayList<>();
        List<Vector3f> initialThrusterLocalPosition = new ArrayList<>();

        // Initial thruster position based on the node referencial (local position)
        // WARNING : Can be broken if the node is rotated at creation, need to fix this
        initialThrusterLocalPosition = new ArrayList<>();

        initialThrusterVecs.add(new Vector3f(-0.7431f, 0.0000f, -0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, 0.16f));

        initialThrusterVecs.add(new Vector3f(0.7431f, 0.0000f, -0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, 0.16f));

        initialThrusterVecs.add(new Vector3f(0.7431f, 0.0000f, 0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, -0.16f));

        initialThrusterVecs.add(new Vector3f(-0.7431f, 0.0000f, 0.6691f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, -0.16f));

        initialThrusterVecs.add(new Vector3f(0.0000f, -1f, 0f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(0.1f, 0f, 0f));

        initialThrusterVecs.add(new Vector3f(-0.0000f, -1f, 0f).normalize());
        initialThrusterLocalPosition.add(new Vector3f(-0.1f, 0f, 0f));

        DroneModel BlueROV2 = new DroneModel("BlueROV2", 200, "vehicle/bluerobotics/br2r4/br2-r4-vehicle.j3o", 6, 50,
                initialThrusterVecs, initialThrusterLocalPosition);
        models.add(BlueROV2);

        creator.saveDroneModels(models,
                "JsonData/DronesModels.json");
    }
}
