package fr.univtln.infomath.dronsim.shared.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object (DTO) representing an authenticated user.
 * This class is used to transfer user authentication details
 * between the server and client.
 *
 * @author André MARÇAIS
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AuthUserDTO {
    private String username;
    private boolean isPilot;
    private boolean isGameMaster;
    private boolean isObserver;
    private boolean isAdmin;
}
