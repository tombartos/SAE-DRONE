package fr.univtln.infomath.dronsim.server.auth;

/**
 * Authentication service that grants all permissions to any credential
 */
public class NaiveAuthService implements AuthenticationService {
    private AuthenticatedUser root = new AuthenticatedUser("root", true, true, true);

    @Override
    public AuthenticatedUser authenticate(String token) throws AuthenticationException {
        if (token != "invalid") {
            return root;
        } else {
            throw new AuthenticationException() {
            };
        }
    }
}
