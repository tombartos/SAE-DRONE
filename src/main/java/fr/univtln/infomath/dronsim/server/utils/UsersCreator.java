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
            System.out.println("Users saved to " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
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
