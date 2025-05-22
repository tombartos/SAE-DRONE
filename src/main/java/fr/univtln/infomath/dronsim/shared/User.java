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
 * role = 1 : Observator
 * role = 2 : Game Master
 * role = 3 : Administrator
 */
public class User {
    private static List<User> users = new ArrayList<>();
    private int id;
    private String login;
    private int role; // 0 = Pilote, 1 = Observateur, 2 = Maitre de jeu, 3 = Administrateur

    public static User createUser(int id, String login, int role) {
        User user = new User(id, login, role);
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
