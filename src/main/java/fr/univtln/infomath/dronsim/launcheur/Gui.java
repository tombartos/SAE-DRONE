package fr.univtln.infomath.dronsim.launcheur;

import fr.univtln.infomath.dronsim.model.Administrateur;
import fr.univtln.infomath.dronsim.model.MaitreDeJeu;
import fr.univtln.infomath.dronsim.model.Observateur;
import fr.univtln.infomath.dronsim.model.Pilot;
import fr.univtln.infomath.dronsim.model.Utilisateur;
import fr.univtln.infomath.dronsim.simulation.Simulateur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

import java.util.List;

public class Gui {
    private final Group root;
    private final int width;
    private final int height;
    private final Stage stage;
    private Utilisateur utilisateur;

    private int test_username;

    private VBox centerBox;
    private VBox listeDronesBox;
    private VBox formulaireBox;
    private VBox listeUtilisateursBox;
    private JFXButton dronesConnectBtn;
    private JFXButton ajouterDroneBtn;
    private JFXButton playBtn;
    private JFXButton quitBtn;
    private JFXButton btnSuivre;
    private JFXButton validerBtn;
    private JFXButton annulerBtn;
    private JFXButton gererUtilisateursBtn;
    private JFXComboBox<String> piloteCombo;
    private JFXComboBox<String> modeCombo;
    private JFXComboBox<String> droneCombo;
    private List<String> droneConnectes;

    public Gui(Utilisateur utilisateur, Group root, int width, int height, Stage stage, Scene scene,
            int test_username) {
        this.utilisateur = utilisateur;
        this.root = root;
        this.width = width;
        this.height = height;
        this.stage = stage;
        this.test_username = test_username;

        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        // Image de fond
        Image bgImage = new Image(getClass().getResource("/page_home/MenuPrincipal.png").toExternalForm());
        BackgroundImage bg = new BackgroundImage(bgImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false));

        BorderPane rootPane = new BorderPane();

        rootPane.setBackground(new Background(bg));

        this.centerBox = new VBox(20);
        this.centerBox.setAlignment(Pos.CENTER);
        this.centerBox.setPadding(new Insets(40));

        this.dronesConnectBtn = new JFXButton("Drones connectés");
        this.dronesConnectBtn.setButtonType(JFXButton.ButtonType.RAISED);
        this.dronesConnectBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");

        this.listeDronesBox = new VBox(10);
        this.listeDronesBox.setAlignment(Pos.CENTER);
        this.listeDronesBox.setPadding(new Insets(10));
        this.listeDronesBox.setStyle("jfx-combo-box");
        ScrollPane scrollPaneConnected = new ScrollPane(this.listeDronesBox);
        scrollPaneConnected.setFitToWidth(true);
        scrollPaneConnected.setPrefHeight(150);
        scrollPaneConnected.setBackground(Background.EMPTY);
        scrollPaneConnected.setBorder(Border.EMPTY);
        scrollPaneConnected.setStyle("""
                    -fx-background-color: transparent;
                    -fx-background: transparent;
                    -fx-control-inner-background: transparent;
                    -fx-padding: 0;
                    -fx-border-color: transparent;
                """);

        this.droneConnectes = List.of("Drone Alpha", "Drone Beta", "Drone Gamma", "Drone Delta", "Drone Epsilon");

        this.listeDronesBox.getChildren().clear();
        for (String drone : this.droneConnectes) {
            HBox ligne = new HBox(10);
            ligne.setAlignment(Pos.CENTER);

            Label nomDrone = new Label(drone);
            nomDrone.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

            Button btnSuivre = new Button("Suivre");
            btnSuivre.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

            btnSuivre.setOnAction(ev -> {
                System.out.println("Suivi activé pour " + drone);
                // Logique de suivi ici
            });

            ligne.getChildren().addAll(nomDrone, btnSuivre);
            listeDronesBox.getChildren().add(ligne);
        }

        // Ajout du bouton pour ajouter un drone
        this.ajouterDroneBtn = new JFXButton("Ajouter un drone");
        this.ajouterDroneBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");

        this.formulaireBox = new VBox(15);
        this.formulaireBox.setPadding(new Insets(20));
        this.formulaireBox.setAlignment(Pos.CENTER);

        this.piloteCombo = new JFXComboBox<>();
        this.piloteCombo.getStyleClass().add("jfx-combo-box");
        this.piloteCombo.getItems().addAll("Alice", "Bob", "Charlie");
        this.piloteCombo.setPromptText("Choisir un pilote");

        this.modeCombo = new JFXComboBox<>();
        this.modeCombo.getStyleClass().add("jfx-combo-box");
        this.modeCombo.getItems().addAll("local", "réseau");
        this.modeCombo.setPromptText("Choisir le mode de connexion");

        this.droneCombo = new JFXComboBox<>();
        this.droneCombo.getStyleClass().add("jfx-combo-box");
        this.droneCombo.getItems().addAll("Drone Alpha", "Drone Beta");
        this.droneCombo.setPromptText("Choisir un drone");

        this.validerBtn = new JFXButton("Valider");
        this.validerBtn.setButtonType(JFXButton.ButtonType.RAISED);
        this.validerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px;");
        this.annulerBtn = new JFXButton("Annuler");
        this.annulerBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 18px;");
        HBox boutonsValidation = new HBox(10);
        boutonsValidation.setAlignment(Pos.CENTER);
        boutonsValidation.getChildren().addAll(this.validerBtn, this.annulerBtn);

        this.formulaireBox.getChildren().addAll(this.piloteCombo, this.modeCombo, this.droneCombo,
                boutonsValidation);

        this.playBtn = new JFXButton("Démarrer le simulateur");
        this.playBtn.setButtonType(JFXButton.ButtonType.RAISED);
        this.playBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");
        this.quitBtn = new JFXButton("QUITTER");
        this.quitBtn.setButtonType(JFXButton.ButtonType.RAISED);
        this.quitBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");

        this.quitBtn.setOnAction(e -> stage.close());
        this.playBtn.setOnAction(e -> lancerSimulateur());

        this.gererUtilisateursBtn = new JFXButton("Gérer les utilisateurs");
        this.gererUtilisateursBtn.setButtonType(JFXButton.ButtonType.RAISED);
        this.gererUtilisateursBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");
        this.listeUtilisateursBox = new VBox(10);
        this.listeUtilisateursBox.setAlignment(Pos.CENTER);
        this.listeUtilisateursBox.setPadding(new Insets(10));
        this.listeUtilisateursBox.setStyle("jfx-combo-box");
        ScrollPane scrollPane = new ScrollPane(this.listeUtilisateursBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150); // hauteur visible max
        scrollPane.setBackground(Background.EMPTY);
        scrollPane.setBorder(Border.EMPTY);
        scrollPane.setStyle("""
                    -fx-background-color: transparent;
                    -fx-background: transparent;
                    -fx-control-inner-background: transparent;
                    -fx-padding: 0;
                    -fx-border-color: transparent;
                """);

        if (test_username == 1) {
            // si l'utilisateur observateur
            this.centerBox.getChildren().addAll(this.playBtn, this.dronesConnectBtn, this.quitBtn);

        } else if (test_username == 2) {
            // si l'utilisateur est un maitre de jeu
            this.centerBox.getChildren().addAll(this.playBtn, this.dronesConnectBtn, this.ajouterDroneBtn,
                    this.quitBtn);
        } else if (test_username == 3) {
            // si l'utilisateur est un pilote
            this.centerBox.getChildren().addAll(this.playBtn, this.quitBtn);
        } else {
            // si l'utilisateur est un administrateur
            this.centerBox.getChildren().addAll(this.playBtn, this.dronesConnectBtn, this.gererUtilisateursBtn,
                    this.quitBtn);
        }

        rootPane.setCenter(this.centerBox);
        scene.setRoot(rootPane);
        stage.setScene(scene);
        stage.show();

        this.dronesConnectBtn.setOnAction(e -> {
            if (this.centerBox.getChildren().contains(scrollPaneConnected)) {
                this.centerBox.getChildren().remove(scrollPaneConnected);
            } else {
                int index = this.centerBox.getChildren().indexOf(this.dronesConnectBtn);
                this.centerBox.getChildren().add(index + 1, scrollPaneConnected);
            }

        });
        // Comportement du bouton principal
        this.ajouterDroneBtn.setOnAction(e -> {
            if (this.centerBox.getChildren().contains(this.formulaireBox)) {
                this.centerBox.getChildren().remove(this.formulaireBox);
            } else {
                int index = this.centerBox.getChildren().indexOf(this.ajouterDroneBtn);
                this.centerBox.getChildren().add(index + 1, this.formulaireBox);
            }
        });

        this.validerBtn.setOnAction(e -> {
            String pilote = this.piloteCombo.getValue();
            String mode = this.modeCombo.getValue();
            String drone = this.droneCombo.getValue();

            if (pilote != null && mode != null && drone != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Pilote '" + pilote + "' connecté en " + mode + " avec le drone '" + drone + "'.");
                alert.showAndWait();
                this.centerBox.getChildren().remove(this.formulaireBox);

            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
                alert.showAndWait();
            }
        });
        this.annulerBtn.setOnAction(e -> {
            this.centerBox.getChildren().remove(this.formulaireBox);
        });

        this.gererUtilisateursBtn.setOnAction(e -> {
            if (this.centerBox.getChildren().contains(scrollPane)) {
                this.centerBox.getChildren().remove(scrollPane);
            } else {
                // Génère la liste avant de l'afficher
                remplirListeUtilisateurs(this.listeUtilisateursBox);
                int index = this.centerBox.getChildren().indexOf(this.gererUtilisateursBtn);
                this.centerBox.getChildren().add(index + 1, scrollPane);
            }
        });

    }

    private void remplirListeUtilisateurs(VBox listeBox) {
        listeBox.getChildren().clear();

        // Liste réelle : chaque objet est une instance de sous-classe
        List<Utilisateur> utilisateurs = List.of(
                Pilot.builder().nom("Alice").prenom("Dupont").login("alice123").build(),
                Pilot.builder().nom("Charlie").prenom("Lefevre").login("charlie321").build(),
                Pilot.builder().nom("David").prenom("Martin").login("david456").build(),
                Observateur.builder().nom("Bob").prenom("Martin").login("bob456").build(),
                MaitreDeJeu.builder().nom("Claire").prenom("Durand").login("claire789").build(),
                Administrateur.builder().nom("Admin").prenom("Root").login("admin").build());

        for (Utilisateur u : utilisateurs) {
            HBox ligne = new HBox(15);
            ligne.setAlignment(Pos.CENTER);

            // Nom complet
            Label nomLabel = new Label(u.getNom() + " " + u.getPrenom());
            nomLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            nomLabel.setPrefWidth(200);

            // Rôle déduit de la classe réelle
            String role;
            if (u instanceof Pilot)
                role = "Pilote";
            else if (u instanceof Observateur)
                role = "Observateur";
            else if (u instanceof MaitreDeJeu)
                role = "Maître de jeu";
            else if (u instanceof Administrateur)
                role = "Administrateur";
            else
                role = "Inconnu";

            Label roleLabel = new Label(role);
            roleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            roleLabel.setPrefWidth(150);

            // Bouton Supprimer
            Button btnSupprimer = new Button("Supprimer");
            btnSupprimer.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            btnSupprimer.setOnAction(ev -> {
                System.out.println("Supprimer : " + u.getLogin());
                // Fenêtre de confirmation ici
            });

            ligne.getChildren().addAll(nomLabel, roleLabel, btnSupprimer);
            listeBox.getChildren().add(ligne);
        }

        // Bouton "Ajouter un utilisateur"
        JFXButton btnAjout = new JFXButton("Ajouter un utilisateur");
        btnAjout.setButtonType(JFXButton.ButtonType.RAISED);
        btnAjout.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
        btnAjout.setOnAction(e -> {
            System.out.println("Ajout d’un nouvel utilisateur");
            // Appelle formulaire d’ajout
        });

        HBox wrapper = new HBox(btnAjout);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setPadding(new Insets(15, 0, 0, 0));
        listeBox.getChildren().add(wrapper);
    }

    /**
     * Lance le simulateur dans un nouveau thread.
     */

    private void lancerSimulateur() {
        new Thread(() -> {
            Simulateur.main(new String[] {});
        }).start();
    }
}
