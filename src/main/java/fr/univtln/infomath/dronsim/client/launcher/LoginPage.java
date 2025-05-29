package fr.univtln.infomath.dronsim.client.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.univtln.infomath.dronsim.shared.auth.AuthUserDTO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * GUI class for the login page.
 * Allows users to authenticate and redirects them to the appropriate interface
 * based on their role (student, professor, or administrator).
 */
public class LoginPage {
    private static final Logger log = LoggerFactory.getLogger(LoginPage.class);
    // private static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static void showLoginPage(Stage stage, int width, int height) {
        // Création du GridPane
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER); // Centrer le contenu
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Titre
        Label title = new Label("Connexion");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        grid.add(title, 0, 0, 2, 1); // colonne 0, ligne 0, colspan 2

        // Label + champ utilisateur
        Label userLabel = new Label("Nom d'utilisateur :");
        grid.add(userLabel, 0, 1);

        TextField userField = new TextField();
        grid.add(userField, 1, 1);

        // Label + champ mot de passe
        Label passLabel = new Label("Mot de passe :");
        grid.add(passLabel, 0, 2);

        PasswordField passField = new PasswordField();
        grid.add(passField, 1, 2);

        // Bouton de connexion
        Button loginBtn = new Button("Se connecter");
        grid.add(loginBtn, 1, 3);

        // Message d'erreur / succès
        Label message = new Label();
        grid.add(message, 0, 4, 2, 1); // Colspan 2

        // Action sur bouton
        loginBtn.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();

            String response = RestClient.tryLogin(username, password);
            if (response.equals("Login successful")) {
                AuthUserDTO user = RestClient.getAuthenticatedUser(RestClient.getAuthHeader());
                Group root = new Group();
                Scene scene = new Scene(root, width, height);
                if (user.isGameMaster()) {
                    new Gui(null, root, width, height, stage, scene, 2); // Game Master
                } else if (user.isObserver()) {
                    new Gui(null, root, width, height, stage, scene, 1); // Observer
                } else if (user.isPilot()) {
                    new Gui(null, root, width, height, stage, scene, 3); // Pilot
                } else if (user.isAdmin()) {
                    new Gui(null, root, width, height, stage, scene, 4); // Admin
                } else {
                    log.error("Unknown user role for username: {}", username);
                    throw new IllegalStateException("Unknown user role for username: " + username);
                }
            } else {
                message.setText(response);
                message.setStyle("-fx-text-fill: red;");
            }

            // if (username.equals("obs") && password.equals("1234")) {
            // // MaitreDeJeu mdj = new MaitreDeJeu(username, password);
            // Group root = new Group();
            // Scene scene = new Scene(root, width, height);
            // new Gui(null, root, width, height, stage, scene, 1);

            // } else if (username.equals("mdj") && password.equals("1234")) {
            // Group root = new Group();
            // Scene scene = new Scene(root, width, height);
            // new Gui(null, root, width, height, stage, scene, 2);

            // } else if (username.equals("Bob35") && password.equals("1234")) {
            // Group root = new Group();
            // Scene scene = new Scene(root, width, height);
            // RestClient.setAuthHeader("Bearer " + username);
            // new Gui(null, root, width, height, stage, scene, 3);

            // } else if (username.equals("admin") && password.equals("1234")) {
            // Group root = new Group();
            // Scene scene = new Scene(root, width, height);
            // new Gui(null, root, width, height, stage, scene, 4);
            // } else {
            // message.setText("Identifiants incorrects.");
            // message.setStyle("-fx-text-fill: red;");
            // }

            /*
             * try {
             * Utils.initconnection(username, password);
             * // Here we want to know if we are an etudiant or a professeur
             * try {
             * EtudiantRepository etudiantRepository = new EtudiantRepository(
             * Utils.getEntityManagerFactory().createEntityManager());
             * Etudiant etudiant = etudiantRepository.getByLogin(username);
             * Group root = new Group();
             * Scene scene = new Scene(root, width, height);
             * new Gui(etudiant, root, width, height,
             * Utils.getEntityManagerFactory().createEntityManager(), stage,
             * scene);
             * } catch (Exception ex_et) {
             * log.info("Not an etudiant");
             * try {
             * ProfesseurRepository professeurRepository = new ProfesseurRepository(
             * Utils.getEntityManagerFactory().createEntityManager());
             * Professeur professeur = professeurRepository.getByLogin(username);
             * Group root = new Group();
             * Scene scene = new Scene(root, width, height);
             * new Gui(professeur, root, width, height,
             * Utils.getEntityManagerFactory().createEntityManager(),
             * stage, scene);
             * } catch (Exception ex_pr) {
             * log.info("Not a professeur");
             * try {
             * ResponsableRepository responsableRepository = new ResponsableRepository(
             * Utils.getEntityManagerFactory().createEntityManager());
             * Responsable responsable = responsableRepository.getByLogin(username);
             * Group root = new Group();
             * Scene scene = new Scene(root, width, height);
             * new Gui(responsable, root, width, height,
             * Utils.getEntityManagerFactory().createEntityManager(), stage, scene);
             * } catch (Exception ex_res) {
             * log.info("Not a responsable, problem in the database ?");
             * // log.error(ex_res.getMessage());
             * ex_res.printStackTrace();
             * }
             * }
             * }
             * } catch (Exception ex) {
             * message.setText(" Erreur de connexion à la base de données !");
             * message.setStyle("-fx-text-fill: red;");
             * }
             */
        });

        // Création et affichage de la scène
        Scene scene = new Scene(grid, width, height);
        stage.setTitle("Page de Connexion");
        stage.setScene(scene);
        stage.show();
    }

    // public static void main(String[] args) {
    // launch(args);
    // }

    // TODO: Disconnect button
}
