package fr.univtln.infomath.dronsim.shared.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) representing a token response.
 * This class is used to transfer token information
 * between the server and client after authentication.
 * It includes a success flag, the token itself, and an optional message.
 *
 * @author André MARÇAIS
 */
@lombok.Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    boolean success;
    String token;
    String message;
}
