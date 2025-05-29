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
 * This class represents a user. It contains all the informations about the
 * user.
 * role = 0 : Pilot
 * role = 1 : Observer
 * role = 2 : Game Master
 * role = 3 : Administrator
 */
public class User {
    private static List<User> users = new ArrayList<>();
    private String login;
    private int role; // 0 = Pilote, 1 = Observateur, 2 = Maitre de jeu, 3 = Administrateur
    private String hashedPasswd;

    public static User createUser(String login, int role, String hashedPasswd) {
        User user = new User(login, role, hashedPasswd);
        users.add(user);
        return user;
    }

    public static List<User> getUsers() {
        return users;
    }

    public static void setUsers(List<User> users) {
        User.users = users;
    }
}
