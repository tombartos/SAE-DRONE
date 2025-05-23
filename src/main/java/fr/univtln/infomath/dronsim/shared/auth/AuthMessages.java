package fr.univtln.infomath.dronsim.shared.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

public final class AuthMessages {
    private AuthMessages() {}

    @lombok.AllArgsConstructor
    @lombok.Getter
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class TokenResponse {
        final boolean success;
        final String token;
        final String message;
    }
}
