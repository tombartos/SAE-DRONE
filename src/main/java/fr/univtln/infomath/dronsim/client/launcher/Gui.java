package fr.univtln.infomath.dronsim.client.launcher;

import fr.univtln.infomath.dronsim.server.simulation.client.SimulatorClient;
import fr.univtln.infomath.dronsim.server.simulation.evenements.Evenement;
import fr.univtln.infomath.dronsim.shared.DroneAssociation;
import fr.univtln.infomath.dronsim.shared.PilotInitResp;
import fr.univtln.infomath.dronsim.shared.User;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ScrollPane;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gui {
    private static final Logger log = LoggerFactory.getLogger(SimulatorClient.class);

    private final Group root;
    private final int width;
    private final int height;
    private final Stage stage;
    private User utilisateur;

    private VBox centerBox;
    private VBox listeDronesBox;
    private VBox formulaireBox;
    private VBox ajouteEventBox;
    private VBox listeEventsBox;
    private VBox listeUtilisateursBox;
    private JFXButton dronesConnectBtn;
    private JFXButton ajouterDroneBtn;
    private JFXButton gereEventsBtn;
    private JFXButton ajouteEventBtn;
    private JFXButton listeEventsBtn;
    private JFXButton playBtn;
    private JFXButton quitBtn;
    private JFXButton validerBtn;
    private JFXButton annulerBtn;
    private JFXButton gererUtilisateursBtn;
    private JFXComboBox<String> piloteCombo;
    private JFXComboBox<String> modeCombo;
    private JFXComboBox<String> droneCombo;
    private List<String> droneConnectes;
    private int test_username;

    public Gui(User utilisateur, Group root, int width, int height, Stage stage, Scene scene,
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

        this.dronesConnectBtn = new JFXButton("Voir pilotes ajoutés");
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

        // Ajout du bouton pour ajouter un drone
        this.ajouterDroneBtn = new JFXButton("Ajouter un drone");
        this.ajouterDroneBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");

        this.formulaireBox = new VBox(15);
        this.formulaireBox.setPadding(new Insets(20));
        this.formulaireBox.setAlignment(Pos.CENTER);

        this.piloteCombo = new JFXComboBox<>();
        this.piloteCombo.getStyleClass().add("jfx-combo-box");
        this.piloteCombo.setPromptText("Choisir un pilote");

        this.modeCombo = new JFXComboBox<>();
        this.modeCombo.getStyleClass().add("jfx-combo-box");
        this.modeCombo.setPromptText("Choisir le mode de connexion");

        this.droneCombo = new JFXComboBox<>();
        this.droneCombo.getStyleClass().add("jfx-combo-box");
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

        // Gerer les événements
        this.gereEventsBtn = new JFXButton("Gérer les événements");
        this.gereEventsBtn.setButtonType(JFXButton.ButtonType.RAISED);
        this.gereEventsBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");
        this.ajouteEventBtn = new JFXButton("Ajouter un événement");
        this.ajouteEventBtn.setStyle("-fx-background-color:#7fdbe7;  -fx-font-size: 18px;");
        this.ajouteEventBox = new VBox(15);
        this.ajouteEventBox.setPadding(new Insets(20));
        this.ajouteEventBox.setAlignment(Pos.CENTER);
        this.listeEventsBtn = new JFXButton("Lister les événements");
        this.listeEventsBtn.setStyle("-fx-background-color:#7fdbe7;  -fx-font-size: 18px;");
        this.listeEventsBox = new VBox(10);
        this.listeEventsBox.setAlignment(Pos.CENTER);
        this.listeEventsBox.setPadding(new Insets(10));
        ScrollPane scrollEventBox = new ScrollPane(this.listeEventsBox);
        scrollEventBox.setFitToWidth(true);
        scrollEventBox.setPrefHeight(150); // hauteur visible max
        scrollEventBox.setBackground(Background.EMPTY);
        scrollEventBox.setBorder(Border.EMPTY);
        scrollEventBox.setStyle("""
                    -fx-background-color: transparent;
                    -fx-background: transparent;
                    -fx-control-inner-background: transparent;
                    -fx-padding: 0;
                    -fx-border-color: transparent;
                """);
        VBox eventsBtnBox = new VBox(10, this.ajouteEventBtn, this.listeEventsBtn);
        eventsBtnBox.setAlignment(Pos.CENTER);

        if (test_username == 1) {
            // si l'utilisateur observateur
            this.centerBox.getChildren().addAll(this.playBtn, this.dronesConnectBtn, this.quitBtn);

        } else if (test_username == 2) {
            // si l'utilisateur est un maitre de jeu
            this.centerBox.getChildren().addAll(this.playBtn, this.dronesConnectBtn, this.ajouterDroneBtn,
                    this.gereEventsBtn, this.quitBtn);
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

            this.listeDronesBox.getChildren().clear();
            this.droneConnectes = new ArrayList<>();
            List<DroneAssociation> droneAssociations = RestClient.getDroneAsso();
            for (DroneAssociation droneAsso : droneAssociations) {
                String pilote = droneAsso.getPilotLogin();
                String drone = droneAsso.getDroneModelName();
                int mode = droneAsso.getConnexionMode();
                String modeStr = (mode == 0) ? "cloud" : "local";
                this.droneConnectes.add(drone);
                Label nomDrone = new Label("Pilote : " + pilote + ", Drone : " + drone + ", Mode : " + modeStr);
                nomDrone.setStyle("-fx-text-fill: white; -fx-font-size: 25px;");
                listeDronesBox.getChildren().add(nomDrone);
            }
        });
        // Comportement du bouton principal
        this.ajouterDroneBtn.setOnAction(e -> {
            this.modeCombo.getItems().clear();
            this.modeCombo.getItems().addAll("cloud", "local");

            this.piloteCombo.getItems().clear();
            List<User> pilotes = RestClient.getPilotList();
            if (pilotes != null) {
                for (User pilote : pilotes) {
                    this.piloteCombo.getItems().add(pilote.getLogin());
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la récupération des pilotes.");
                alert.showAndWait();
            }

            this.droneCombo.getItems().clear();
            List<String> droneModels = RestClient.getDroneModels();
            if (droneModels != null) {
                for (String drone : droneModels) {
                    this.droneCombo.getItems().add(drone);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la récupération des modèles de drones.");
                alert.showAndWait();
            }

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
            int intmode;

            if (pilote != null && mode != null && drone != null) {
                // Appel de la méthode pour créer la demande d'association
                if (mode.equals("cloud"))
                    intmode = 0;
                else
                    intmode = 1;
                boolean result = RestClient.createDroneAssoReq(drone, pilote, intmode);
                if (!result) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la création de la demande.");
                    alert.showAndWait();
                    return;
                }
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

        // Gérer les événements
        this.gereEventsBtn.setOnAction(e -> {
            if (this.centerBox.getChildren().contains(eventsBtnBox)) {
                this.centerBox.getChildren().remove(eventsBtnBox);
                this.centerBox.getChildren().remove(this.ajouteEventBox); // Cache aussi le formulaire d’ajout s’il est
                                                                          // affiché
                this.centerBox.getChildren().remove(this.listeEventsBox); // Cache aussi la liste s’il est affiché
            } else {
                int index = this.centerBox.getChildren().indexOf(this.gereEventsBtn);
                this.centerBox.getChildren().add(index + 1, eventsBtnBox);
            }
        });

        this.ajouteEventBtn.setOnAction(e -> {
            // Affiche le formulaire d’ajout d’événement
            this.ajouteEventBox.getChildren().clear();
            // Ligne 1 : Type d'événement
            JFXComboBox<String> typeCombo = new JFXComboBox<>();
            typeCombo.getStyleClass().add("jfx-combo-box");
            typeCombo.getItems().addAll("Courant", "Poisson", "Bateau");
            typeCombo.setPromptText("Choisir type d'événement");
            // Ligne 2 : Zone centre
            List<Vector3f> positions = Arrays.asList(
                    new Vector3f(-20, 3, -25),
                    new Vector3f(-10, 3, -5),
                    new Vector3f(0, -3, 0),
                    new Vector3f(3, -5, -15),
                    new Vector3f(-10, -1, 10),
                    new Vector3f(-5, -5, -25),
                    new Vector3f(5, -8, -35),
                    new Vector3f(-10, -11, 0));
            JFXComboBox<Vector3f> positionCombo = new JFXComboBox<>(FXCollections.observableArrayList(positions));
            positionCombo.getStyleClass().add("jfx-combo-box");
            positionCombo.setPromptText("Choisir une position");
            configureVector3fComboBox(positionCombo);
            // Ligne 3 : Zone size
            List<Vector3f> tailles = Arrays.asList(
                    new Vector3f(10, 10, 10),
                    new Vector3f(25, 25, 25),
                    new Vector3f(50, 5, 100),
                    new Vector3f(50, 5, 150),
                    new Vector3f(80, 10, 200));
            JFXComboBox<Vector3f> sizeCombo = new JFXComboBox<>(FXCollections.observableArrayList(tailles));
            sizeCombo.getStyleClass().add("jfx-combo-box");
            sizeCombo.setPromptText("Choisir une zone");
            configureVector3fComboBox(sizeCombo);
            // Ligne 4 : Direction
            List<Vector3f> directions = Arrays.asList(
                    new Vector3f(1, 0, 0),
                    new Vector3f(-1, 0, 0),
                    new Vector3f(0, 0, 1),
                    new Vector3f(0, 0, -1),
                    new Vector3f(1, 0, 1).normalizeLocal(),
                    new Vector3f(-1, 0, -1).normalizeLocal());
            JFXComboBox<Vector3f> dirCombo = new JFXComboBox<>(FXCollections.observableArrayList(directions));
            dirCombo.getStyleClass().add("jfx-combo-box");
            dirCombo.setPromptText("Choisir une direction");
            configureVector3fComboBox(dirCombo);
            // Ligne 5 : Vitesse
            List<Float> vitesses = Arrays.asList(
                    0.5f, 1.0f, 1.5f, 1000.0f, 1500.0f, 2000.0f);
            JFXComboBox<Float> speedField = new JFXComboBox<>(FXCollections.observableArrayList(vitesses));
            speedField.getStyleClass().add("jfx-combo-box");
            speedField.setPromptText("vitesse / intensité");

            // Bouton Valider
            JFXButton validerEventBtn = new JFXButton("Valider");
            validerEventBtn.setButtonType(JFXButton.ButtonType.RAISED);
            validerEventBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px;");
            validerEventBtn.setOnAction(ev -> {
                String type = typeCombo.getValue();
                Vector3f centre = positionCombo.getValue();
                Vector3f taille = sizeCombo.getValue();
                Vector3f direction = dirCombo.getValue();
                Float vitesse = speedField.getValue();

                if (type != null && centre != null && taille != null && direction != null && vitesse != null) {
                    if (type.equals("Courant")) {
                        if (vitesse < 1000.0f) {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "La vitesse pour un courant doit être supérieure à 1000.");
                            alert.showAndWait();
                            return;
                        } else if (centre.y > -3.0f) {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "Le centre du courant doit être inférieure à -3.0 en Y.");
                            alert.showAndWait();
                            return;
                        }
                    } else if (type.equals("Poisson")) {
                        if (vitesse > 1.5f) {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "La vitesse pour un poisson doit être inférieure à 1.5.");
                            alert.showAndWait();
                            return;
                        } else if (centre.y > -3.0f) {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "Le centre du poisson doit être inférieure à -3.0 en Y.");
                            alert.showAndWait();
                            return;
                        }
                    } else if (type.equals("Bateau")) {
                        if (vitesse > 1.5f) {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "La vitesse pour un bateau doit être supérieure à 1.5.");
                            alert.showAndWait();
                            return;
                        }
                        if (centre.y != -3.0f) {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "Le centre du bateau doit être  à -3.0 en Y.");
                            alert.showAndWait();
                            return;
                        }
                    }
                    // Appel de la méthode pour créer l'événement
                    // RestClient.createEvent(type, centre, taille, direction, vitesse);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION,
                            "Événement '" + type + "' créé avec succès.");
                    alert.showAndWait();

                    this.ajouteEventBox.getChildren().clear();
                    eventsBtnBox.getChildren().remove(this.ajouteEventBox);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
                    alert.showAndWait();
                }
            });

            JFXButton annulerEventBtn = new JFXButton("Annuler");
            annulerEventBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 18px;");
            annulerEventBtn.setOnAction(ev -> {
                this.ajouteEventBox.getChildren().clear();
                eventsBtnBox.getChildren().remove(this.ajouteEventBox);
            });
            HBox validationEventBtns = new HBox(10);
            validationEventBtns.setAlignment(Pos.CENTER);
            validationEventBtns.getChildren().addAll(validerEventBtn, annulerEventBtn);

            // Ajout à la VBox
            this.ajouteEventBox.getChildren().addAll(typeCombo, positionCombo, sizeCombo, dirCombo, speedField,
                    validationEventBtns);

            if (eventsBtnBox.getChildren().contains(this.ajouteEventBox)) {
                eventsBtnBox.getChildren().remove(this.ajouteEventBox);
            } else {
                int index = eventsBtnBox.getChildren().indexOf(this.ajouteEventBtn);
                eventsBtnBox.getChildren().add(index + 1, this.ajouteEventBox);
            }
        });

        this.listeEventsBtn.setOnAction(e -> {

            this.listeEventsBox.getChildren().clear();

            // TODO : Récupérer la liste des événements actifs depuis le serveur
            // List<Evenement> evenementsActifs = RestClient.getActiveEvents(); // méthode à
            // implémenter
            /*
             * for (Evenement evt : evenementsActifs) {
             * Label eventLabel = new Label(evt.getType() + " à " + evt.getZoneCenter());
             * eventLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
             *
             * JFXButton stopBtn = new JFXButton("Arrêter");
             * stopBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
             *
             * HBox eventLine = new HBox(10, eventLabel, stopBtn);
             * eventLine.setAlignment(Pos.CENTER);
             * this.listeEventsBox.getChildren().add(eventLine);
             *
             * // Action pour le bouton "Arrêter"
             * stopBtn.setOnAction(evStop -> {
             * // TODO : implémenter la méthode pour arrêter l'événement
             * // ReadableString response = RestClient.stopEvent(evt.getId());
             * this.listeEventsBox.getChildren().remove(eventLine);
             * });
             *
             * }
             */

            if (eventsBtnBox.getChildren().contains(scrollEventBox)) {
                eventsBtnBox.getChildren().remove(scrollEventBox);
            } else {
                int index = eventsBtnBox.getChildren().indexOf(this.listeEventsBtn);
                eventsBtnBox.getChildren().add(index + 1, scrollEventBox);
            }

        });

    }

    private void configureVector3fComboBox(ComboBox<Vector3f> comboBox) {
        comboBox.setCellFactory(lv -> new ListCell<Vector3f>() {
            @Override
            protected void updateItem(Vector3f item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null
                        : String.format("(%.1f, %.1f, %.1f)", item.x, item.y, item.z));
            }
        });
        comboBox.setButtonCell(comboBox.getCellFactory().call(null));
    }

    private void remplirListeUtilisateurs(VBox listeBox) {
        listeBox.getChildren().clear();

        // Liste réelle : chaque objet est une instance de sous-classe
        List<User> utilisateurs = List.of(
                User.builder().id(0).login("test").role(1).build()
        // User.builder().nom("Charlie").prenom("Lefevre").login("charlie321").build(),
        // User.builder().nom("David").prenom("Martin").login("david456").build(),
        // User.builder().nom("Bob").prenom("Martin").login("bob456").build(),
        // User.builder().nom("Claire").prenom("Durand").login("claire789").build(),
        // User.builder().nom("Admin").prenom("Root").login("admin").build());
        );
        for (User u : utilisateurs) {
            HBox ligne = new HBox(15);
            ligne.setAlignment(Pos.CENTER);

            // Nom complet
            Label nomLabel = new Label(u.getLogin());
            nomLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            nomLabel.setPrefWidth(200);

            // Rôle déduit de la classe réelle
            String role;
            if (u.getRole() == 0)
                role = "Pilote";
            else if (u.getRole() == 1)
                role = "Observateur";
            else if (u.getRole() == 2)
                role = "Maître de jeu";
            else if (u.getRole() == 3)
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
        if (test_username == 2) {
            String serverStarted = RestClient.startSimulation();
            if (serverStarted != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, serverStarted);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du démarrage du simulateur.");
                alert.showAndWait();
            }
        }

        if (test_username == 3) {
            PilotInitResp connReq = RestClient.connectPilot();
            // The first boolean indicates if the connection is successful, the second
            // indicates the connection mode
            if (connReq.getSuccess()) {
                // Start Ardupilot and QGroundControl in two different shells
                // Start Ardupilot
                try {
                    String[] cmd = {
                            "bash", "-c",
                            "cd ~/SAE-DRONE && source ./venv/bin/activate && cd ardupilot && ./Tools/autotest/sim_vehicle.py -v ArduSub --out=udp:127.0.0.1:14550 --console --map; exec bash"
                    };
                    new ProcessBuilder(cmd)
                            .directory(new java.io.File(System.getProperty("user.home")))
                            .inheritIO()
                            .start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR,
                            "Erreur lors du lancement du script shell pour Ardupilot.");
                    alert.showAndWait();
                }

                // Start QGroundControl
                try {
                    String[] cmd = {
                            "bash", "-c",
                            "~/SAE-DRONE/qgc/QGroundControl.AppImage; exec bash"
                    };
                    new ProcessBuilder(cmd)
                            .directory(new java.io.File(System.getProperty("user.home")))
                            .inheritIO()
                            .start();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors du lancement de QGroundControl.");
                    alert.showAndWait();
                }

                if (connReq.getClientId() > -1) {
                    // Start the simulator client
                    new Thread(() -> {
                        SimulatorClient.main(new String[] {
                                "127.0.0.1", connReq.getJME_server_ip(), String.valueOf(connReq.getClientId()) });
                    }).start();
                }

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de la connexion au simulateur.");
                alert.showAndWait();
            }

        }

        // new Thread(() -> {
        // Simulateur.main(new String[] {});
        // }).start();

        // Test de la connexion au serveur REST

    }
}
