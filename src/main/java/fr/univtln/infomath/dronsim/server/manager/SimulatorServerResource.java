package fr.univtln.infomath.dronsim.server.manager;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.simulation.server.SimulatorServer;
import fr.univtln.infomath.dronsim.server.utils.AuthChecker;
import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Context;

@Path("/SimulatorServer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulatorServerResource {
    @POST
    @Path("/start")
    public String startSimulationServer(@HeaderParam("Authorization") String authHeader) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster()) {
            throw new jakarta.ws.rs.ForbiddenException("User is not a GM");
        }

        // Start the simulation server
        new Thread(() -> {
            // SimulatorServer.initDroneAssociations(DroneAssociation.getDroneAssociations());
            SimulatorServer.main(new String[] {});
        }).start();
        return "Simulation server started";
    }

    @POST
    @Path("/connect/pilot")
    public String connectPilot(@HeaderParam("Authorization") String authHeader,
            @Context ContainerRequestContext requestContext) {

        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a Pilot
        if (!authUser.isPilot()) {
            throw new jakarta.ws.rs.ForbiddenException("User is not a Pilot");
        }

        // Get the drone association
        DroneAssociation droneAssociation = null;
        for (DroneAssociation da : DroneAssociation.getDroneAssociations()) {
            if (da.getPilotLogin() == authUser.getUsername()) {
                droneAssociation = da;
                break;
            }
        }
        if (droneAssociation == null) {
            throw new jakarta.ws.rs.NotFoundException("Drone association not found");
        }

        String IP = requestContext.getHeaderString("X-Forwarded-For");
        // Connect the pilot to the simulation server
        // Using Adrupilot controler
        SimulatorServer.initPilot(droneAssociation, IP, 0);
        return "Server waiting for the Ardupilot controler to start";
    }

}
