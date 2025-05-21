package fr.univtln.infomath.dronsim.server.manager;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/dronemodels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DroneModelResource {
    @GET
    public List<String> getDroneModels() {
        // TODO : recuperer noms des modeles
        return null;
    }

}
