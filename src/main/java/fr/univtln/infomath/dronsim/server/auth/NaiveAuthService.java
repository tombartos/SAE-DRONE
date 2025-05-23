package fr.univtln.infomath.dronsim.server.auth;

/**
 * Authentication service that grants all permissions to any credential
 */
public class NaiveAuthService implements AuthenticationService {
    private AuthenticatedUser root = new AuthenticatedUser("root", true, true, true, true);
    private AuthenticatedUser pilot1 = new AuthenticatedUser("Bob35", true, false, false, false);

    @Override
    public AuthenticatedUser authenticate(String token) throws AuthenticationException {
        if (token.equals("Bob35"))
            return pilot1;
        if (token != "invalid") {
            return root;
        } else {
            throw new AuthenticationException() {
            };
        }
    }
}
