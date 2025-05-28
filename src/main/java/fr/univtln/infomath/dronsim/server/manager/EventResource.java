package fr.univtln.infomath.dronsim.server.manager;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
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

@Path("/events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EventResource {
    private static final Logger log = LoggerFactory.getLogger(EventResource.class);

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
        if (eventCreateRequest.getType() == "Courant") {
            type = "Courant";
            entiteType = null;

        } else if (eventCreateRequest.getType() == "Bateau") {
            type = "EntiteMarine";
            entiteType = "Bateau";
        } else if (eventCreateRequest.getType() == "Poisson") {
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
        SimulatorServer.getInstance().ajoutEvenement(evenementDTO);
        return "Event created successfully";
    }
}
