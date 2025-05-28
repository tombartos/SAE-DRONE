package fr.univtln.infomath.dronsim.client.launcher;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.Inet4Address;
import java.util.List;
import java.util.Vector;

import org.glassfish.jersey.jackson.JacksonFeature;

import com.jme3.math.Vector3f;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.simulation.evenements.EvenementDTO;
import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import fr.univtln.infomath.dronsim.shared.EventCreateRequest;
import fr.univtln.infomath.dronsim.shared.PilotInitResp;
import fr.univtln.infomath.dronsim.shared.User;
import fr.univtln.infomath.dronsim.shared.auth.AuthUserDTO;
import fr.univtln.infomath.dronsim.shared.auth.TokenResponse;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Getter;
import lombok.Setter;

public class RestClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestClient.class);
    // private String baseUrl = "http://localhost:8080/api/v1";
    private static Client client;
    private static WebTarget baseTarget;
    @Setter
    @Getter
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

    public static AuthUserDTO getAuthenticatedUser(String authHeader) {
        try {
            Response response = baseTarget.path("auth")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .get();
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return null;
            }
            AuthUserDTO authUser = response.readEntity(AuthUserDTO.class);
            response.close();
            log.info("Authenticated user: " + authUser);
            return authUser;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return null;
        }
    }

    public static String tryLogin(String username, String password) {
        try {
            Response response = baseTarget.path("auth")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.form(new Form()
                            .param("username", username)
                            .param("password", password)));
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return errorMsg;
            } else {
                TokenResponse tokenResponse = response.readEntity(TokenResponse.class);
                if (!tokenResponse.isSuccess()) {
                    log.error("Login failed: " + tokenResponse.getMessage());
                    response.close();
                    return tokenResponse.getMessage();
                }
                authHeader = "Bearer " + tokenResponse.getToken();
                log.info("Login successful, auth header set: " + authHeader);
                response.close();
                return "Login successful";
            }
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return "Login failed: " + e.getMessage();
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
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp())
                    continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            log.error("Could not get local IP address: {}", e.getMessage());
            throw new RuntimeException("Could not get local IP address", e);
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

    public static List<EvenementDTO> getEvents() {
        try {
            Response response = baseTarget.path("events")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .get();
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return null;
            }
            List<EvenementDTO> events = response.readEntity(new GenericType<List<EvenementDTO>>() {
            });
            response.close();
            log.info("Response received: " + events);
            return events;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return null;
        }
    }

    public static String createEvent(String type, Vector3f center, Vector3f size, float speed, Vector3f direction) {
        EventCreateRequest eventReq = new EventCreateRequest(type, center, size, speed, direction);
        try {
            Response response = baseTarget.path("events")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .post(Entity.entity(eventReq, MediaType.APPLICATION_JSON));
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

}
