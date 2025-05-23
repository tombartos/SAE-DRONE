package fr.univtln.infomath.dronsim.client.launcher;

import java.net.InetAddress;
import java.util.List;

import org.glassfish.jersey.jackson.JacksonFeature;

import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import fr.univtln.infomath.dronsim.shared.PilotInitResp;
import fr.univtln.infomath.dronsim.shared.User;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Setter;

public class RestClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestClient.class);
    // private String baseUrl = "http://localhost:8080/api/v1";
    private static Client client;
    private static WebTarget baseTarget;
    @Setter
    private static String authHeader = "Bearer TEST_TOKEN";
    // TODO: Replace with a real token when we will have the true
    // authentication system

    public RestClient(String baseUrl) {
        try {
            RestClient.client = ClientBuilder.newBuilder()
                    .register(JacksonFeature.class)
                    .build();
            baseTarget = client.target(baseUrl);
            log.info("Client created with base URL: " + baseUrl);
        } catch (Exception e) {
            log.error("Error creating client: " + e.getMessage());
        }
    }

    public static List<User> getPilotList() {
        try {
            Response response = baseTarget.path("users/pilots")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .get();
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return null;
            }
            List<User> pilots = response.readEntity(new GenericType<List<User>>() {
            });
            response.close();
            log.info("Response received: " + pilots);
            return pilots;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return null;
        }
    }

    public static List<String> getDroneModels() {
        try {
            Response response = baseTarget.path("dronemodels")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .get();
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return null;
            }
            List<String> droneModels = response.readEntity(new GenericType<List<String>>() {
            });
            response.close();
            log.info("Response received: " + droneModels);
            return droneModels;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return null;
        }
    }

    public static boolean createDroneAssoReq(String droneModelName, String pilotLogin, int connxionMode) {
        DroneAssociation droneAsso = new DroneAssociation(-1, droneModelName, pilotLogin, connxionMode);
        try {
            Response response = baseTarget.path("droneassociations")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .post(jakarta.ws.rs.client.Entity.entity(droneAsso, MediaType.APPLICATION_JSON));
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return false;
            }
            boolean result = response.readEntity(Boolean.class);
            response.close();
            log.info("Response received: " + result);
            return result;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return false;
        }
    }

    public static List<DroneAssociation> getDroneAsso() {
        try {
            Response response = baseTarget.path("droneassociations")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .get();
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return null;
            }
            List<DroneAssociation> droneAsso = response.readEntity(new GenericType<List<DroneAssociation>>() {
            });
            response.close();
            log.info("Response received: " + droneAsso);
            return droneAsso;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return null;
        }
    }

    public static String startSimulation() {
        try {
            Response response = baseTarget.path("SimulatorServer/start")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .post(null);
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return null;
            }
            String result = response.readEntity(String.class);
            response.close();
            log.info("Response received: " + result);
            return result;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return null;
        }
    }

    private static String getLocalIp() {
        // WARNING: This method may not work if we are not in a local network
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.error("Could not get local IP address: {}", e.getMessage());
            return "127.0.0.1";
        }
    }

    public static PilotInitResp connectPilot() {
        try {
            String localIp = getLocalIp();
            log.info("Local IP Address = " + localIp);
            Response response = baseTarget.path("SimulatorServer/connect/pilot")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .header("X-Forwarded-For", localIp)
                    .post(null);
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return new PilotInitResp(false, -99, null); // -99 is a special value to indicate an error
            }
            PilotInitResp result = response.readEntity(PilotInitResp.class);
            response.close();
            log.info("Response received: " + result);
            return result;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return new PilotInitResp(false, -99, null); // -99 is a special value to indicate an error
        }
    }

}
