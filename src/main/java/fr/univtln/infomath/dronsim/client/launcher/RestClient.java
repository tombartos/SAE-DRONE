package fr.univtln.infomath.dronsim.client.launcher;

import org.glassfish.jersey.jackson.JacksonFeature;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class RestClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestClient.class);
    // private String baseUrl = "http://localhost:8080/api/v1";
    private static Client client;
    private static WebTarget baseTarget;

    public RestClient(String baseUrl) {
        try (Client client = ClientBuilder.newBuilder()
                .register(JacksonFeature.class)
                .build()) {
            baseTarget = client.target(baseUrl);
            log.info("Client created with base URL: " + baseUrl);
        } catch (Exception e) {
            log.error("Error creating client: " + e.getMessage());
        }
    }

    // public String RestClientTest() {
    // try {
    // Response response = baseTarget.path("users/plus/5")
    // .request(MediaType.APPLICATION_JSON)
    // .get();
    // String stringResponse = response.readEntity(String.class);
    // response.close();
    // log.info("Response received: " + stringResponse);
    // return stringResponse;
    // } catch (WebApplicationException e) {
    // log.error(e.getMessage());
    // return e.getMessage();
    // }
    // }
}
