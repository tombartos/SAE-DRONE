package fr.univtln.infomath.dronsim.server.manager;

import java.util.List;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.simulation.drones.DroneModel;
import fr.univtln.infomath.dronsim.server.utils.AuthChecker;
import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import fr.univtln.infomath.dronsim.shared.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/droneassociations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DroneAssoResource {

    @POST
    public boolean createDroneAsso(@HeaderParam("Authorization") String authHeader,
            DroneAssociation droneAssociation) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster()) {
            throw new jakarta.ws.rs.ForbiddenException("User is not a GM");
        }

        // Check if the drone association already exists
        for (DroneAssociation existingDroneAssociation : DroneAssociation.getDroneAssociations()) {
            if (existingDroneAssociation.getPilotLogin().equals(droneAssociation.getPilotLogin())) {
                throw new jakarta.ws.rs.WebApplicationException("Drone association already exists", 409);
            }
        }

        // Check if the drone model exists
        boolean droneModelExists = false;
        for (DroneModel droneModel : Manager.getDroneModels()) {
            if (droneModel.getName().equals(droneAssociation.getDroneModelName())) {
                droneModelExists = true;
                break;
            }
        }
        if (!droneModelExists) {
            throw new jakarta.ws.rs.NotFoundException("Drone model not found");
        }
        // Check if the pilot is valid

        boolean pilotExists = false;
        for (User user : Manager.getUsers()) {
            if (user.getLogin().equals(droneAssociation.getPilotLogin())) {
                pilotExists = true;
                if (user.getRole() != 0) {
                    throw new jakarta.ws.rs.ForbiddenException("User is not a pilot");
                }
                break;
            }
        }

        if (!pilotExists) {
            throw new jakarta.ws.rs.NotFoundException("Pilot not found");
        }

        droneAssociation.setId(DroneAssociation.getDroneAssociations().size());
        DroneAssociation.getDroneAssociations().add(droneAssociation);
        return true;
    }

    @GET
    public List<DroneAssociation> getDroneAsso(@HeaderParam("Authorization") String authHeader) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster()) {
            throw new jakarta.ws.rs.ForbiddenException("User is not a GM");
        }

        return DroneAssociation.getDroneAssociations();
    }

}
