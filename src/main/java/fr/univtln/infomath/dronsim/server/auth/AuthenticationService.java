package fr.univtln.infomath.dronsim.server.auth;

/**
 * Interface for interaction with authentication service.
 */
public interface AuthenticationService {
    public AuthenticatedUser authenticate(String token) throws AuthenticationException;

    @lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @lombok.Getter
    public class AuthenticatedUser {
        final String username;
        final boolean isPilot;
        final boolean isGameMaster;
        final boolean isObserver;
    }

    // public static abstract class AuthorizationException extends Exception {}
    public static abstract class AuthenticationException extends Exception {
    }
    // public static abstract class NoSuchUser extends Exception {}
}
