package fr.univtln.infomath.dronsim.shared.auth;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@lombok.Getter
@JsonInclude(JsonInclude.Include.ALWAYS)
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
    boolean success;
    String token;
    String message;
}
