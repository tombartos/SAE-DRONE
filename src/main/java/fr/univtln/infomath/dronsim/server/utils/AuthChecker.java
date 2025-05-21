package fr.univtln.infomath.dronsim.server.utils;

import fr.univtln.infomath.dronsim.server.auth.AuthenticationService;
import fr.univtln.infomath.dronsim.server.auth.AuthenticationService.AuthenticatedUser;
import fr.univtln.infomath.dronsim.server.auth.NaiveAuthService;
import jakarta.ws.rs.NotAuthorizedException;

public class AuthChecker {
    // TODO: Replace with a real authentication service
    private static AuthenticationService authService = new NaiveAuthService();

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
        return authUser;

    }

}
