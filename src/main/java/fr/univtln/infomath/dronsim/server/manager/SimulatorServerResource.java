package fr.univtln.infomath.dronsim.server.manager;

import java.util.List;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.simulation.server.SimulatorServer;
import fr.univtln.infomath.dronsim.server.utils.AuthChecker;
import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import fr.univtln.infomath.dronsim.shared.PilotInitResp;
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

    /**
     * Mehtod called by the pilot to connect to the simulation server
     *
     * @param authHeader
     * @param requestContext
     * @return a PilotInitResp object containing the connection status and the
     *         client ID, the clientID is -1 if
     *         the connection is in cloud mode,
     */

    @POST
    @Path("/connect/pilot")
    public PilotInitResp connectPilot(@HeaderParam("Authorization") String authHeader,
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
            throw new jakarta.ws.rs.ForbiddenException("Drone association not found");
        }

        String IP = requestContext.getHeaderString("X-Forwarded-For");
        if (IP == null) {
            throw new jakarta.ws.rs.ForbiddenException("IP of the client not found");
        }
        // Connect the pilot to the simulation server
        // Using Adrupilot controler
        final DroneAssociation finalDroneAssociation = droneAssociation;
        new Thread(() -> SimulatorServer.initPilot(finalDroneAssociation, IP, 0)).start();
        // Returns true to indicate that the server is ready to accept the pilot
        // connection
        PilotInitResp result;
        if (droneAssociation.getConnexionMode() == 0) // Cloud
            result = new PilotInitResp(true, -1, null);
        else {
            // Local
            result = new PilotInitResp(true, droneAssociation.getId(), Manager.getBaseHost());
        }
        return result;
    }

}
