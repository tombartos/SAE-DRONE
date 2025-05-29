package fr.univtln.infomath.dronsim.server.manager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.server.auth.AuthChecker;
import fr.univtln.infomath.dronsim.server.auth.AuthenticationService;
import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.shared.User;
import fr.univtln.infomath.dronsim.shared.auth.AuthUserDTO;
import fr.univtln.infomath.dronsim.shared.auth.TokenResponse;

//TODO : revoir le chargement des utilisateurs (actuellement on les charge une fois dans manager et une fois ici)

@Path("auth")
public class AuthenticationResource {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationResource.class);
    private static Service authService = new Service();
    private static PasswdDatabase passwd = new PasswdDatabase();

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AuthUserDTO whoAmI(@HeaderParam("Authorization") String authHeader) {
        AuthenticatedUser user = AuthChecker.checkAuth(authHeader);
        AuthUserDTO userDto = new AuthUserDTO(user.getUsername(), user.isPilot(),
                user.isGameMaster(), user.isObserver(), user.isAdmin());
        return userDto;
    }

    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_JSON)
    public TokenResponse postLogin(@FormParam("username") String username,
            @FormParam("password") String password) {
        if (username == null || password == null) {
            throw new BadRequestException("null username or password");
        }
        if (passwd.authenticate(username, password)) {
            log.info("Logged in " + username);
            return new TokenResponse(true, authService.newSessionTokenFor(username),
                    "logged in successfully");
        } else {
            log.info("Bad login for " + username);
            return new TokenResponse(false, null, "invalid username or password");
        }
    }

    private static class PasswdDatabase {
        private static String usersJsonPath = "JsonData/users.json";
        private HashMap<String, byte[]> passwd = new HashMap<>();
        private HashMap<String, Service.AuthenticatedUser> users = new HashMap<>();

        public PasswdDatabase() {
            int lineNumber = 1;
            try {
                ObjectMapper mapper = new ObjectMapper();
                // Read the JSON array
                var userList = mapper.readValue(new File(usersJsonPath), new TypeReference<List<User>>() {
                });
                for (User userJson : userList) {
                    final var username = userJson.getLogin();
                    final var role = userJson.getRole();
                    final var password = Base64.getDecoder().decode(userJson.getHashedPasswd());

                    var user = Service.AuthenticatedUser.builder();
                    user.username(username);
                    switch (role) {
                        case 0:
                            user.isPilot(true);
                            break;
                        case 1:
                            user.isObserver(true);
                            break;
                        case 2:
                            user.isGameMaster(true);
                            break;
                        case 3:
                            user.isAdmin(true);
                            break;
                        default:
                            log.error("Invalid role '" + role + "' in '" + usersJsonPath + "' at " + lineNumber);
                    }
                    this.passwd.put(username, password);
                    this.users.put(username, user.build());
                    lineNumber++;
                }
            } catch (Exception e) {
                log.error("Error reading '{}': {}", usersJsonPath, e.getMessage());
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

    private static class Service implements AuthenticationService {
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

    public static AuthenticationService getAuthService() {
        return authService;
    }
}
