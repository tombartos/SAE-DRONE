package fr.univtln.infomath.dronsim.server.manager;

import java.util.List;

import fr.univtln.infomath.dronsim.shared.User;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

// TEST CLASS, TO BE REMOVED
public class UserResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    // TODO: getpilotList() should return a list of users that are pilots
    // @GET
    // @Path("/pilots")
    // public List<User> getPilotList() {
    // return;
    // }
}
