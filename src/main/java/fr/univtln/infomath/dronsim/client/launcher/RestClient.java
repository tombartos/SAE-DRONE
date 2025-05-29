package fr.univtln.infomath.dronsim.client.launcher;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.Inet4Address;
import java.util.List;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.jme3.math.Vector3f;

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

/**
 * RestClient is a utility class for interacting with the drone simulation
 * server's REST API.
 * <p>
 * It provides methods for authentication, retrieving and managing users,
 * drones, events,
 * and simulation control. The class handles HTTP requests and responses,
 * manages authentication
 * headers, and provides utility methods for network information.
 * </p>
 *
 * <ul>
 * <li>Authentication (login, get authenticated user)</li>
 * <li>Retrieve lists of pilots and drone models</li>
 * <li>Create and retrieve drone associations</li>
 * <li>Start and connect to the simulation</li>
 * <li>Manage simulation events (create, list, remove)</li>
 * <li>Utility methods for network and base URL information</li>
 * </ul>
 *
 * <b>Note:</b> This class is designed for use on the client side of the drone
 * simulation application.
 *
 * @author Tom BARTIER
 */
public class RestClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestClient.class);
    // private String baseUrl = "http://localhost:8080/api/v1";
    private static Client client;
    private static WebTarget baseTarget;
    @Setter
    @Getter
    private static String authHeader = "Bearer TEST_TOKEN";
    // authentication system

    /**
     * Constructs a RestClient with the specified base URL.
     * Initializes the JAX-RS client and sets the base target for API requests.
     *
     * @param baseUrl The base URL of the drone simulation server's REST API.
     */
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

    /**
     * Retrieves the authenticated user based on the provided authorization header.
     * Sends a GET request to the server's authentication endpoint and returns
     * the AuthUserDTO if successful.
     *
     * @param authHeader The authorization header containing the Bearer token.
     * @return AuthUserDTO representing the authenticated user, or null if an error
     *         occurs.
     */
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

    /**
     * Attempts to log in a user with the provided username and password.
     * Sends a POST request to the server's authentication endpoint and sets the
     * authHeader if successful.
     *
     * @param username The username of the user trying to log in.
     * @param password The password of the user trying to log in.
     * @return A message indicating the result of the login attempt.
     */
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

    /**
     * Retrieves a list of pilots from the server.
     * Sends a GET request to the server's users/pilots endpoint and returns the
     * list of User objects if successful. Only available for GMs.
     *
     * @return List of User objects representing pilots, or null if an error occurs.
     */
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

    /**
     * Retrieves a list of drone models from the server.
     * Sends a GET request to the server's dronemodels endpoint and returns the list
     * of drone model names if successful. Only available for GMs.
     *
     * @return List of strings representing drone model names, or null if an error
     *         occurs.
     */
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

    /**
     * Creates a drone association request with the specified parameters.
     * Sends a POST request to the server's droneassociations endpoint and returns
     * true if successful, false otherwise. Only available for GMs.
     *
     * @param droneModelName The name of the drone model to associate.
     * @param pilotLogin     The login of the pilot to associate with the drone.
     * @param connxionMode   The connection mode for the drone association.
     * @return true if the association request was created successfully, false
     *         otherwise.
     */
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

    /**
     * Retrieves a list of drone associations from the server.
     * Sends a GET request to the server's droneassociations endpoint and returns
     * the list of DroneAssociation objects if successful. Only available for GMs
     * and Observers.
     *
     * @return List of DroneAssociation objects representing drone associations, or
     *         null if an error occurs.
     */
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

    /**
     * Starts the simulation by sending a POST request to the server's start
     * endpoint.
     * Returns the response from the server if successful, or null if an error
     * occurs. Only available for GMs.
     *
     * @return String response from the server indicating the result of the start
     *         operation, or null if an error occurs.
     */
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

    /**
     * Retrieves the local IP address of the machine running the client.
     * This method iterates through network interfaces to find a suitable IPv4
     * address that is not a loopback address.
     *
     * @return The local IP address as a string.
     * @throws RuntimeException if the local IP address cannot be determined.
     */
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

    /**
     * Connects the pilot to the simulation server.
     * Sends a POST request to the server's connect/pilot endpoint and returns a
     * PilotInitResp object if successful.
     * This method also retrieves the local IP address to include in the request
     * headers. Only available for Pilots.
     *
     * @return PilotInitResp containing the result of the connection attempt, or an
     *         error response if an exception occurs.
     */
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

    /**
     * Retrieves a list of events from the server.
     * Sends a GET request to the server's events endpoint and returns the list of
     * EvenementDTO objects if successful. Only available for GMs.
     *
     * @return List of EvenementDTO objects representing events, or null if an error
     *         occurs.
     */
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

    /**
     * Creates a new event with the specified parameters.
     * Sends a POST request to the server's events endpoint and returns the result
     * as a string. Only available for GMs.
     *
     * @param type      The type of the event to create.
     * @param center    The center position of the event as a Vector3f.
     * @param size      The size of the event as a Vector3f.
     * @param speed     The speed of the event.
     * @param direction The direction of the event as a Vector3f.
     * @return A string indicating the result of the event creation, or an error
     *         message if an exception occurs.
     */
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
                return errorMsg;
            }
            String result = response.readEntity(String.class);
            response.close();
            log.info("Response received: " + result);
            return result;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return ("Error creating event: " + e.getMessage());
        }
    }

    /**
     * Removes an event with the specified ID.
     * Sends a DELETE request to the server's events/{eventId} endpoint and returns
     * the result as a string. Only available for GMs.
     *
     * @param eventId The ID of the event to remove.
     * @return A string indicating the result of the event removal, or an error
     *         message if an exception occurs.
     */
    public static String removeEvent(int eventId) {
        try {
            Response response = baseTarget.path("events/" + eventId)
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", authHeader)
                    .delete();
            if (response.getStatus() >= 400) {
                String errorMsg = response.readEntity(String.class);
                log.error("HTTP {}: {}", response.getStatus(), errorMsg);
                response.close();
                return errorMsg;
            }
            String result = response.readEntity(String.class);
            response.close();
            log.info("Response received: " + result);
            return result;
        } catch (WebApplicationException e) {
            log.error("WebApplicationException: {}", e.getMessage());
            return ("Error removing event: " + e.getMessage());
        }
    }

    /**
     * Retrieves the base IP address from the base target URI.
     * This method extracts the host part of the base target's URI, which can be an
     * IP address or a domain name.
     *
     * @return The base IP address as a string, or null if an error occurs.
     */
    public static String getBaseIp() {
        try {
            String url = baseTarget.getUri().toString();
            java.net.URI uri = new java.net.URI(url);
            return uri.getHost(); // returns the host part (IP or domain)
        } catch (Exception e) {
            log.error("Could not extract IP from baseTarget: {}", e.getMessage());
            return null;
        }
    }

}
