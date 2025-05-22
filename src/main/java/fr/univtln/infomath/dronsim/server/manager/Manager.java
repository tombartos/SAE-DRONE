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
 * Main class.
 *
 */
public class Manager {
    private static final Logger log = LoggerFactory.getLogger(Manager.class);
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/api/v1/";
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

    public static List<User> getUsers() {
        return users;
    }

    public static List<DroneModel> getDroneModels() {
        return droneModels;
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
