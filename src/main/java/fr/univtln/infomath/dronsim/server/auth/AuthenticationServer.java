package fr.univtln.infomath.dronsim.server.auth;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.HashMap;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.shared.auth.AuthMessages;

@Path("auth")
public class AuthenticationServer {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationServer.class);
    private Service authService = new Service();
    private PasswdDatabase passwd = new PasswdDatabase("passwd");

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public AuthMessages.TokenResponse postLogin(@FormParam("username") String username, @FormParam("password") String password) {
        if (username == null || password == null) {
            throw new BadRequestException("null username or password");
        }
        if (passwd.authenticate(username, password)) {
            log.info("Logged in " + username);
            return new AuthMessages.TokenResponse(true, authService.newSessionTokenFor(username), "logged in successfully");
        } else {
            log.info("Bad login for " + username);
            return new AuthMessages.TokenResponse(false, null, "invalid username or password");
        }
    }

    private static class PasswdDatabase {
        private HashMap<String, byte[]> passwd = new HashMap<>();
        private HashMap<String, Service.AuthenticatedUser> users = new HashMap<>();

        public PasswdDatabase(String passwdFilePath) {
            int lineNumber = 1;
            try (BufferedReader br = new BufferedReader(new FileReader(passwdFilePath))) {
                String line;
                for (; (line = br.readLine()) != null; ++lineNumber) {
                    String[] parts = line.split(":", 3);
                    if (parts.length == 3) {
                        final var username = parts[0];
                        final var groups = parts[1];
                        final var password = java.util.Base64.getDecoder().decode(parts[2].stripTrailing());

                        var user = Service.AuthenticatedUser.builder();
                        user.username(username);
                        for (var group : groups.split(",")) {
                            switch (group) {
                                case "pilot":
                                    user.isPilot(true);
                                    break;
                                case "mdj":
                                    user.isGameMaster(true);
                                    // fallthrough
                                case "observer":
                                    user.isObserver(true);
                                    break;
                                default:
                                    log.error("Invalid group '" + group + "' in '" + passwdFilePath + "' at " + lineNumber);
                            }
                        }

                        this.passwd.put(username, password);
                        this.users.put(username, user.build());
                    } else {
                        log.error("Invalid line in '" + passwdFilePath + "' at " + lineNumber);
                    }
                }
            } catch (java.io.IOException e) {
                log.error("IO error reading '" + passwdFilePath + "'.");
            }
        }

        public boolean authenticate(String username, String password) {
            try {
                final var md = java.security.MessageDigest.getInstance("SHA-512");
                md.update(StandardCharsets.UTF_8.encode(password));
                final var actualDigest = md.digest();
                final var expectedDigest = passwd.get(username);
                return java.util.Arrays.equals(actualDigest, expectedDigest);
            } catch (java.security.NoSuchAlgorithmException e) {
                throw new RuntimeException("Could not get hash function");
            }
        }

        public Service.AuthenticatedUser lookup(String username) {
            return users.get(username);
        }
    }

    private class Service implements AuthenticationService {
        private HashMap<String, AuthenticatedUser> sessions = new HashMap<>();
        private SecureRandom rand = new SecureRandom();
        private byte[] buf = new byte[32];

        @Override
        public AuthenticatedUser authenticate(String token) throws AuthenticationException {
            return sessions.get(token);
        }

        public String newSessionTokenFor(String username) {
            rand.nextBytes(buf);
            var token = java.util.Base64.getEncoder().encodeToString(buf);
            var user = passwd.lookup(username);
            sessions.put(token, user);
            return token;
        }
    }

    public AuthenticationService getAuthService() {
        return authService;
    }
}
