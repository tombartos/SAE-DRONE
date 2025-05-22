package fr.univtln.infomath.dronsim.server.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.infomath.dronsim.shared.User;

public class UsersCreator {
    public static void saveUsers(List<User> users, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), users);
            System.out.println("Drone models saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        User.createUser(0, "Bob35", 0);
        User.createUser(1, "Alice42", 0);
        User.createUser(2, "Charlie21", 1);
        User.createUser(3, "Diana99", 2);
        User.createUser(4, "Eve88", 3);

        saveUsers(User.getUsers(), "JsonData/users.json");

    }

}
