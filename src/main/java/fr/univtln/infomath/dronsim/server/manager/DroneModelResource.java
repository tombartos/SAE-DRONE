package fr.univtln.infomath.dronsim.server.manager;

import java.util.ArrayList;
import java.util.List;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
import fr.univtln.infomath.dronsim.server.utils.AuthChecker;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/dronemodels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DroneModelResource {
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
        for (DroneModel drone : Manager.droneModels) {
            ModelsNames.add(drone.getName());
        }
        return ModelsNames;
    }

}
