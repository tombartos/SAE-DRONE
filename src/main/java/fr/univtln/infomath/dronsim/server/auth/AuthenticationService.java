package fr.univtln.infomath.dronsim.server.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;

// FIXME I'm doubting "authentication" is the right term here. The more appropriate term may be "identification" or "authorization".
/**
 * Interface for interaction with authentication service.
 * <p>
 * When a client authenticates itself as a given user (e.g., by POSTing a
 * username and password to a login resource), the server grants them a session
 * token that can be used for subsequent requests. Server side code for
 * authentication may provide an implementation of this interface to the rest of
 * the server side code may authenticate a session independently of the
 * authentication method/protocol.
 * <p>
 * The way the client passes the token to the server is protocol defined. (See
 * {@link AuthChecker} for a utility class to help with HTTP/REST).
 *
 * @author André Marçais
 */
public interface AuthenticationService {
    /**
     * Authenticate a client against a session token.
     * <p>
     * If this method returns, the provided token was valid. Otherwise an exception
     * is thrown.
     *
     * @param token Comprises session token and any information the client may
     *              provide to identify itself.
     * @return An {@link AuthenticatedUser} carrying authenticated data about a
     *         user.
     * @throws AuthenticationException Indicates the given token is invalid.
     */
    public AuthenticatedUser authenticate(String token) throws AuthenticationException;

    /**
     * Authenticated user and metadata.
     * <p>
     * An instance of this class should only exist for an authenticated user. It
     * should not be used merely for aggregating data on a user (that may not have
     * been authenticated).
     */
    @lombok.ToString
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
    @lombok.Builder
    @lombok.Getter
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    public class AuthenticatedUser {
        /** The username. */
        @Builder.Default
        protected String username = "";

        /** Wether or not the user has role pilot. */
        @Builder.Default
        @JsonProperty("isPilot")
        protected boolean isPilot = false;

        /** Wether or not the user has role game master. */
        @Builder.Default
        @JsonProperty("isGameMaster")
        protected boolean isGameMaster = false;

        /** Wether or not the user has role observer. */
        @Builder.Default
        @JsonProperty("isObserver")
        protected boolean isObserver = false;

        /** Wether or not the user is an administrator */
        @Builder.Default
        @JsonProperty("isAdmin")
        protected boolean isAdmin = false;
    }

    /**
     * An exception indicating some authentication has occurred.
     * <p>
     * This exception should be handled by sending the client an error response.
     * (E.g., a 403 status code for HTTP.)
     */
    public static abstract class AuthenticationException extends Exception {
    }
}
