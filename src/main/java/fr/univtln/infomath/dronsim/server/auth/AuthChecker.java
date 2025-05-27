package fr.univtln.infomath.dronsim.server.auth;

import org.checkerframework.checker.units.qual.A;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.manager.AuthenticationResource;
import jakarta.ws.rs.NotAuthorizedException;

public class AuthChecker {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthChecker.class);
    private static AuthenticationService authService = AuthenticationResource.getAuthService();

    public static AuthenticatedUser checkAuth(String authHeader) {
        // Extract token from header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }
        String token = authHeader.substring("Bearer ".length());

        // Authenticate the user
        AuthenticatedUser authUser;
        try {
            authUser = authService.authenticate(token);
        } catch (AuthenticationService.AuthenticationException e) {
            throw new NotAuthorizedException("Invalid token");
        }
        log.info("DEBUG : AUTHENTICATED USER: " + authUser);
        return authUser;

    }

}
