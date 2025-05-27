package fr.univtln.infomath.dronsim.server.auth;

/**
 * Interface for interaction with authentication service.
 */
public interface AuthenticationService {
    public AuthenticatedUser authenticate(String token) throws AuthenticationException;

    @lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @lombok.Builder
    @lombok.Getter
    public class AuthenticatedUser {
        final String username;
        @lombok.Builder.Default
        final boolean isPilot = false;
        @lombok.Builder.Default
        final boolean isGameMaster = false;
        @lombok.Builder.Default
        final boolean isObserver = false;
        @lombok.Builder.Default
        final boolean isAdmin = false;
    }

    // public static abstract class AuthorizationException extends Exception {}
    public static abstract class AuthenticationException extends Exception {
    }
    // public static abstract class NoSuchUser extends Exception {}
}
