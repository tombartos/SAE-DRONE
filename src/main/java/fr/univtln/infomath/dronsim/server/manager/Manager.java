package fr.univtln.infomath.dronsim.server.manager;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import fr.univtln.infomath.dronsim.shared.User;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.List;

/**
 * Manager is the main entry point for the server-side application.
 * <p>
 * It is responsible for:
 * <ul>
 * <li>Starting the Grizzly HTTP server and exposing JAX-RS REST endpoints</li>
 * <li>Loading and providing access to user and drone model data from JSON
 * files</li>
 * <li>Initializing drone associations and user lists</li>
 * <li>Providing utility methods for accessing server configuration</li>
 * </ul>
 * <p>
 * The server listens on {@link #BASE_URI} and loads configuration from the
 * <code>JsonData/users.json</code> and <code>JsonData/DronesModels.json</code>
 * files.
 * <p>
 * Usage: Run the main method to start the server.
 *
 * @author Tom BARTIER
 */
public class Manager {
    private static final Logger log = LoggerFactory.getLogger(Manager.class);
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://0.0.0.0:8080/api/v1/";
    private static List<User> users;
    private static List<DroneModel> droneModels;

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
     * application.
     *
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().packages("fr.univtln.infomath.dronsim.server.manager");
        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Returns the list of users loaded from the JSON file.
     *
     * @return List of users
     */
    public static List<User> getUsers() {
        return users;
    }

    /**
     * Returns the list of drone models loaded from the JSON file.
     *
     * @return List of drone models
     */
    public static List<DroneModel> getDroneModels() {
        return droneModels;
    }

    /**
     * Returns the base host of the server.
     *
     * @return Base host as a String
     */
    public static String getBaseHost() {
        try {
            URI uri = new URI(BASE_URI);
            return uri.getHost();
        } catch (Exception e) {
            log.error("Error parsing BASE_URI: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        // Initialize the drone associations
        DroneAssociation.initDroneAssociations();
        // Initialize users list
        ObjectMapper mapper = new ObjectMapper();
        // We set this to avoid errors about the unitVector property when we serialize a
        // Vector3f
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        users = mapper.readValue(
                Paths.get("JsonData/users.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {
                });
        User.setUsers(users);
        log.info("Users loaded from file: " + users.size() + " users loaded");

        droneModels = mapper.readValue(
                Paths.get("JsonData/DronesModels.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<List<DroneModel>>() {
                });

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with endpoints available at "
                + "%s%nHit Ctrl-C to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}
