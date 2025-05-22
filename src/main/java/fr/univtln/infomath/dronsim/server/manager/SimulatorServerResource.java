package fr.univtln.infomath.dronsim.server.manager;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.simulation.server.SimulatorServer;
import fr.univtln.infomath.dronsim.server.utils.AuthChecker;
import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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
            SimulatorServer.main(new String[] {});
        }).start();
        return "Simulation server started";
    }

    @POST
    @Path("/connect/pilot")
    public String connectPilot(@HeaderParam("Authorization") String authHeader,
            @Context ContainerRequestContext requestContext) {
        // TODO: TERMINER CA

        // // Check if the user is authenticated
        // AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // // Check if the user is a GM
        // if (!authUser.isPilot()) {
        // throw new jakarta.ws.rs.ForbiddenException("User is not a Pilot");
        // }

        // // Get the Client ID with the id of the drone association
        // int clientId = -1;
        // String modelName = null;
        // DroneAssociation.getDroneAssociations().forEach(da -> {
        // if (da.getPilotLogin().equals(authUser.getUsername())) {
        // clientId = da.getId();
        // // Get the model name of the drone
        // modelName = da.getDroneModelName();
        // }
        // });
        // if (clientId == -1) {
        // throw new jakarta.ws.rs.NotFoundException("Drone association not found");
        // }

        // // Connect the pilot to the simulation server
        // // SimulatorServer.initPilot(clientId, modelName, );
        return "Pilot connected to simulation server";
    }

}
