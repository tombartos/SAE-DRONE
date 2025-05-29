package fr.univtln.infomath.dronsim.server.manager;

import java.util.ArrayList;
import java.util.List;

import fr.univtln.infomath.dronsim.server.auth.AuthChecker;
import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * REST resource for accessing available drone models.
 * <p>
 * Provides an endpoint for game masters (GM) to retrieve the list of all drone
 * model names.
 * Access is restricted to authenticated users with GM privileges.
 * <ul>
 * <li>GET: Returns a list of drone model names.</li>
 * </ul>
 * <p>
 * All endpoints require an "Authorization" header for authentication.
 *
 * @author Tom BARTIER
 */
@Path("/dronemodels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DroneModelResource {
    /**
     * Retrieves the list of all drone model names.
     * <p>
     * This endpoint allows game masters to access the names of all available
     * drone models. It checks if the user is authenticated and if they are a GM.
     *
     * @param authHeader The authorization header containing the user's token.
     * @return A list of drone model names.
     */
    @GET
    public List<String> getDroneModels(@HeaderParam("Authorization") String authHeader) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster()) {
            throw new jakarta.ws.rs.ForbiddenException("User is not a GM");
        }

        // Return the list of drone models names
        List<String> ModelsNames = new ArrayList<>();
        for (DroneModel drone : Manager.getDroneModels()) {
            ModelsNames.add(drone.getName());
        }
        return ModelsNames;
    }

}
