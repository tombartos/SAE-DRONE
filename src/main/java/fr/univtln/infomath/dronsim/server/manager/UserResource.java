package fr.univtln.infomath.dronsim.server.manager;

import java.util.ArrayList;
import java.util.List;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService;
import fr.univtln.infomath.dronsim.server.auth.NaiveAuthService;
import fr.univtln.infomath.dronsim.server.utils.AuthChecker;
import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.shared.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.HeaderParam;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

// TEST CLASS, TO BE REMOVED
public class UserResource {

    @GET
    @Path("/test")
    public String getIt() {
        return "Got it!";
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("pilots")
    public List<User> getPilotList(@HeaderParam("Authorization") String authHeader) {
        // Check if the user is authenticated
        AuthenticatedUser authUser = AuthChecker.checkAuth(authHeader);

        // Check if the user is a GM
        if (!authUser.isGameMaster()) {
            throw new ForbiddenException("User is not a GM");
        }

        List<User> pilots = new ArrayList<>();
        for (User usertmp : User.getUsers())
            if (usertmp.getRole() == 0)
                pilots.add(usertmp);
        return pilots;
    }
}
