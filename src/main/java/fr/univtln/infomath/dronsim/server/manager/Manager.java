package fr.univtln.infomath.dronsim.server.manager;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.univtln.infomath.dronsim.server.simulation.client.SimulatorClient;
import fr.univtln.infomath.dronsim.server.simulation.drones.Drone;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
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
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/api/v1/";
    public static List<User> users;
    public static List<DroneModel> droneModels;

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
     * Main method.
     *
     * @param args
     * @throws IOException
     */
    private static final Logger log = LoggerFactory.getLogger(Manager.class);

    public static void main(String[] args) throws IOException {

        // Initialize the drone associations
        DronesAssociations.init();
        // Initialize users list
        // Initialize users list
        ObjectMapper mapper = new ObjectMapper();
        users = mapper.readValue(
                Paths.get("JsonData/users.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<List<User>>() {
                });
        User.setUsers(users);
        log.info("Users loaded from file: " + users.size() + " users loaded");

        droneModels = mapper.readValue(
                Paths.get("JsonData/droneModels.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<List<DroneModel>>() {
                });

        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with endpoints available at "
                + "%s%nHit Ctrl-C to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}
