package fr.univtln.infomath.dronsim.server.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

/**
 * Interface for interaction with authentication service.
 *
 * @author André MARÇAIS
 */

// TODO: Javadoc

public interface AuthenticationService {
    public AuthenticatedUser authenticate(String token) throws AuthenticationException;

    @lombok.ToString
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @lombok.Builder
    @lombok.Getter
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public class AuthenticatedUser {
        @Builder.Default
        protected String username = "";
        @Builder.Default
        @JsonProperty("isPilot")
        protected boolean isPilot = false;
        @Builder.Default
        @JsonProperty("isGameMaster")
        protected boolean isGameMaster = false;
        @Builder.Default
        @JsonProperty("isObserver")
        protected boolean isObserver = false;
        @Builder.Default
        @JsonProperty("isAdmin")
        protected boolean isAdmin = false;
    }

    // public static abstract class AuthorizationException extends Exception {}
    public static abstract class AuthenticationException extends Exception {
    }
    // public static abstract class NoSuchUser extends Exception {}
}
