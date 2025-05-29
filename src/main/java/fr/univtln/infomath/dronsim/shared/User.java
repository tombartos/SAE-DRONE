package fr.univtln.infomath.dronsim.shared;

import java.util.ArrayList;
import java.util.List;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
/**
 * Represents a user in the drone simulation system.
 * <p>
 * Each user has a login, a role, and a hashed password.
 * <ul>
 * <li>role = 0 : Pilot</li>
 * <li>role = 1 : Observer</li>
 * <li>role = 2 : Game Master</li>
 * <li>role = 3 : Administrator</li>
 * </ul>
 * Users are managed in a static list.
 *
 * @author Tom BARTIER
 */
public class User {
    private static List<User> users = new ArrayList<>();
    private String login;
    private int role; // 0 = Pilote, 1 = Observateur, 2 = Maitre de jeu, 3 = Administrateur
    private String hashedPasswd;

    /**
     * Creates a new user with the specified login, role, and hashed password.
     *
     * @param login        The login of the user
     * @param role         The role of the user (0 for Pilot, 1 for Observer, 2 for
     *                     Game Master, 3 for Administrator)
     * @param hashedPasswd The hashed password of the user
     */
    public static User createUser(String login, int role, String hashedPasswd) {
        User user = new User(login, role, hashedPasswd);
        users.add(user);
        return user;
    }

    /**
     * Retrieves the list of all users.
     *
     * @return List of User objects
     */
    public static List<User> getUsers() {
        return users;
    }

    /**
     * Sets the list of users.
     * This method is used to initialize or update the list of users.
     *
     * @param users The new list of User objects
     */
    public static void setUsers(List<User> users) {
        User.users = users;
    }
}
