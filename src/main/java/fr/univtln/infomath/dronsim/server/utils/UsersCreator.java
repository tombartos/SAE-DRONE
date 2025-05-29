package fr.univtln.infomath.dronsim.server.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.univtln.infomath.dronsim.shared.User;

/**
 * This class is a utility class to serialize a list of User objects to a JSON
 * file that will be read by the server at each startup. You can launch this
 * class directly. This is likely to disappear in the future when admins will be
 * implemented
 *
 * @author Tom BARTIER
 */
public class UsersCreator {
    public static void saveUsers(List<User> users, String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), users);
            System.out.println("Users saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method to create and save users.
     * This method initializes a list of User objects and saves them to a JSON file.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        // Passwords are hashed with the script hashpasswd at the root of the project
        User.createUser("Bob35", 0,
                "pOHiWcOoDG3idQBr0ZAWKZUs0qvYmjQUa+e4Pdwaa7pihB8OzFw6k4hw95X+cfKolau8rWO+osoRQxfDn0LU9Q==");
        User.createUser("Alice42", 0,
                "VtDUmXRbncEpi49WBdd0ZmKBMu/PDbgO1pHWwOMxK/k4qyJB5zFUsyl3ssjNV5UPy//yDz/lCmt5CKOBELTKGA==");
        User.createUser("Charlie21", 1,
                "pRYbv3EoW05UCLn/ERueAeA4LbQ2PEirIE4PHEK2V/6INmOekX80At0oL/gGMw7sJ3BYkFnLGCEyZEYXhYsnSQ==");
        User.createUser("Diana99", 2,
                "wVXHhjRv2hVA09ZZjnSxAdyvUHvl6zXDbAs64RX7ySQNfITGvVS8m/8R5yJu1Lx82mun+89+Vj7aAJryDsEJcw==");
        User.createUser("Eve88", 3,
                "vRaEtAqDFTKNCf2uRPTI1AtgOp0BOt6mk2aQEWNqpG0DKg5GeHTsmlzDlhIOLuC8zTiKSRJmMmph7wUDRNWL+Q==");

        saveUsers(User.getUsers(), "JsonData/users.json");
    }
}
