package fr.univtln.infomath.dronsim.server.manager;

import java.util.ArrayList;
import java.util.List;

import fr.univtln.infomath.dronsim.server.auth.AuthChecker;
import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.shared.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.HeaderParam;

/**
 * REST resource for managing users in the simulation.
 * <p>
 * Provides an endpoint for game masters (GM) to retrieve a list of all pilots.
 * Access is restricted to authenticated users with GM privileges.
 * <ul>
 * <li>GET: Returns a list of all pilots.</li>
 * </ul>
 * <p>
 * All endpoints require an "Authorization" header for authentication.
 *
 * @author Tom BARTIER
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class UserResource {

    /**
     * Retrieves the list of all pilots.
     * <p>
     * This endpoint allows game masters to access the list of all users with the
     * pilot role. It checks if the user is authenticated and if they are a GM or
     * observer.
     *
     * @param authHeader The authorization header containing the user's token.
     * @return A list of users with the pilot role.
     */
    @GET
    @Path("pilots")
    public List<User> getPilotList(@HeaderParam("Authorization") String authHeader) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster() && !authUser.isAdmin() && !authUser.isObserver()) {
            throw new ForbiddenException("User is not authorized");
        }

        List<User> pilots = new ArrayList<>();
        for (User usertmp : User.getUsers())
            if (usertmp.getRole() == 0)
                pilots.add(usertmp);
        return pilots;
    }
}
