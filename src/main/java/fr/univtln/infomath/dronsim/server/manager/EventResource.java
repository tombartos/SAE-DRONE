package fr.univtln.infomath.dronsim.server.manager;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.univtln.infomath.dronsim.server.auth.AuthChecker;
import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;
import fr.univtln.infomath.dronsim.server.simulation.server.SimulatorServer;
import fr.univtln.infomath.dronsim.shared.EventCreateRequest;

/**
 * REST resource for managing events in the simulation.
 * <p>
 * Provides endpoints for game masters (GM) to create, retrieve, and delete
 * events.
 * Access is restricted to authenticated users with GM privileges.
 * <ul>
 * <li>GET: Retrieve a list of all events.</li>
 * <li>POST: Create a new event.</li>
 * <li>DELETE: Remove an event by its ID.</li>
 * </ul>
 * <p>
 * All endpoints require an "Authorization" header for authentication.
 *
 * @author Tom BARTIER
 */
@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {
    private static final Logger log = LoggerFactory.getLogger(EventResource.class);

    /**
     * Retrieves the list of all active events.
     * <p>
     * This endpoint allows game masters to access the list of all events in the
     * simulation. It checks if the user is authenticated and if they are a GM.
     *
     * @param authHeader The authorization header containing the user's token.
     * @return A list of event DTOs.
     */
    @GET
    public List<EvenementDTO> getEvents(@HeaderParam("Authorization") String authHeader) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster()) {
            throw new jakarta.ws.rs.ForbiddenException("User is not a GM");
        }

        return SimulatorServer.getEvenementDTOs();
    }

    /**
     * Creates a new event in the simulation.
     * <p>
     * This endpoint allows game masters to create an event by providing the
     * necessary details in the request body. It checks if the user is authenticated
     * and if they are a GM.
     *
     * @param authHeader         The authorization header containing the user's
     *                           token.
     * @param eventCreateRequest The request body containing event details.
     * @return A success message indicating the event was created.
     */
    @POST
    public String createEvent(@HeaderParam("Authorization") String authHeader,
            EventCreateRequest eventCreateRequest) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster()) {
            throw new jakarta.ws.rs.ForbiddenException("User is not a GM");
        }

        String type;
        String entiteType;
        if (eventCreateRequest.getType().equals("Courant")) {
            type = "Courant";
            entiteType = null;

        } else if (eventCreateRequest.getType().equals("Bateau")) {
            type = "EntiteMarine";
            entiteType = "Bateau";
        } else if (eventCreateRequest.getType().equals("Poisson")) {
            type = "EntiteMarine";
            entiteType = "Poisson";
        } else {
            log.error("Unknown event type: " + eventCreateRequest.getType());
            throw new jakarta.ws.rs.BadRequestException("Unknown event type: " + eventCreateRequest.getType());
        }

        // Add the event to the server
        EvenementDTO evenementDTO = EvenementDTO.createEvenementDTO(
                EvenementDTO.evenementsDTOs.size(),
                eventCreateRequest.getCenter(),
                eventCreateRequest.getSize(),
                type,
                eventCreateRequest.getDirection(),
                eventCreateRequest.getSpeed(),
                entiteType);

        SimulatorServer instance = SimulatorServer.getInstance();
        if (instance == null) {
            log.error("SimulatorServer instance is null, server is not initialized");
            throw new jakarta.ws.rs.BadRequestException("SimulatorServer instance is not initialized");
        }
        instance.ajoutEvenement(evenementDTO);
        return "Event created successfully";
    }

    /**
     * Deletes an event by its ID.
     * <p>
     * This endpoint allows game masters to remove an event from the simulation by
     * providing the event ID in the URL path. It checks if the user is
     * authenticated and if they are a GM.
     *
     * @param authHeader The authorization header containing the user's token.
     * @param id         The ID of the event to be deleted.
     * @return A success message indicating the event was deleted.
     */
    @Path("/{id}")
    @DELETE
    public String deleteEvent(@HeaderParam("Authorization") String authHeader, @PathParam("id") int id) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster()) {
            throw new jakarta.ws.rs.ForbiddenException("User is not a GM");
        }

        // Remove the event from the server
        boolean removed = SimulatorServer.getInstance().retirerEvenement(id);
        if (!removed) {
            throw new jakarta.ws.rs.BadRequestException("Event with id " + id + " not found");
        }
        return "Event deleted successfully";
    }
}
