package fr.univtln.infomath.dronsim.launcheur;

import fr.univtln.infomath.dronsim.simulation.Simulateur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.scene.control.Label;
import java.util.List;

public class Gui {
    private final Group root;
    private final int width;
    private final int height;
    private final Stage stage;

    public Gui(Object user, Group root, int width, int height, Stage stage, Scene scene) {
        this.root = root;
        this.width = width;
        this.height = height;
        this.stage = stage;

        scene.getStylesheets().add(getClass().getResource("/styles/styles.css").toExternalForm());

        // Image de fond
        Image bgImage = new Image(getClass().getResource("/page_home/MenuPrincipal.png").toExternalForm());
        BackgroundImage bg = new BackgroundImage(bgImage,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, true, false));

        BorderPane rootPane = new BorderPane();

        rootPane.setBackground(new Background(bg));

        VBox centerBox = new VBox(20);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(40));

        JFXButton dronesConnectBtn = new JFXButton("Drone connecté");
        dronesConnectBtn.setButtonType(JFXButton.ButtonType.RAISED);
        dronesConnectBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");

        VBox listeDronesBox = new VBox(10);
        listeDronesBox.setAlignment(Pos.CENTER);
        listeDronesBox.setPadding(new Insets(10));
        listeDronesBox.setStyle("jfx-combo-box");

        List<String> droneConnectes = List.of("Drone Alpha", "Drone Beta", "Drone Gamma");

        listeDronesBox.getChildren().clear();
        for (String drone : droneConnectes) {
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
        dronesConnectBtn.setOnAction(e -> {
            if (centerBox.getChildren().contains(listeDronesBox)) {
                centerBox.getChildren().remove(listeDronesBox);
            } else {
                int index = centerBox.getChildren().indexOf(dronesConnectBtn);
                centerBox.getChildren().add(index + 1, listeDronesBox);
            }
        });

        // Ajout du bouton pour ajouter un drone
        JFXButton ajouterDroneBtn = new JFXButton("Ajouter un drone");
        ajouterDroneBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");

        VBox formulaireBox = new VBox(15);
        formulaireBox.setPadding(new Insets(20));
        formulaireBox.setAlignment(Pos.CENTER);

        JFXComboBox<String> piloteCombo = new JFXComboBox<>();
        piloteCombo.getStyleClass().add("jfx-combo-box");
        piloteCombo.getItems().addAll("Alice", "Bob", "Charlie");
        piloteCombo.setPromptText("Choisir un pilote");

        JFXComboBox<String> modeCombo = new JFXComboBox<>();
        modeCombo.getStyleClass().add("jfx-combo-box");
        modeCombo.getItems().addAll("local", "réseau");
        modeCombo.setPromptText("Choisir le mode de connexion");

        JFXComboBox<String> droneCombo = new JFXComboBox<>();
        droneCombo.getStyleClass().add("jfx-combo-box");
        droneCombo.getItems().addAll("Drone Alpha", "Drone Beta");
        droneCombo.setPromptText("Choisir un drone");

        JFXButton validerBtn = new JFXButton("Valider");
        validerBtn.setButtonType(JFXButton.ButtonType.RAISED);
        validerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 18px;");
        Button annulerBtn = new Button("Annuler");
        annulerBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 18px;");
        HBox boutonsValidation = new HBox(10);
        boutonsValidation.setAlignment(Pos.CENTER);
        boutonsValidation.getChildren().addAll(validerBtn, annulerBtn);

        formulaireBox.getChildren().addAll(piloteCombo, modeCombo, droneCombo,
                boutonsValidation);

        // Comportement du bouton principal
        ajouterDroneBtn.setOnAction(e -> {
            if (centerBox.getChildren().contains(formulaireBox)) {
                centerBox.getChildren().remove(formulaireBox);
            } else {
                int index = centerBox.getChildren().indexOf(ajouterDroneBtn);
                centerBox.getChildren().add(index + 1, formulaireBox);
            }
        });

        validerBtn.setOnAction(e -> {
            String pilote = piloteCombo.getValue();
            String mode = modeCombo.getValue();
            String drone = droneCombo.getValue();

            if (pilote != null && mode != null && drone != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                        "Pilote '" + pilote + "' connecté en " + mode + " avec le drone '" + drone + "'.");
                alert.showAndWait();
                formulaireBox.setVisible(false);
                piloteCombo.setValue(null);
                modeCombo.setValue(null);
                droneCombo.setValue(null);
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
                alert.showAndWait();
            }
        });

        annulerBtn.setOnAction(e -> {
            formulaireBox.setVisible(false);
            piloteCombo.setValue(null);
            modeCombo.setValue(null);
            droneCombo.setValue(null);
        });

        // Intégration dans la zone (par exemple leftMenu)
        // leftMenu.getChildren().addAll(ajouterPiloteBtn, formulaireBox);

        // Bas de page : bouton quitter et jouer
        JFXButton playBtn = new JFXButton("Démarrer le simulateur");
        playBtn.setButtonType(JFXButton.ButtonType.RAISED);
        playBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");
        JFXButton quitBtn = new JFXButton("QUITTER");
        quitBtn.setButtonType(JFXButton.ButtonType.RAISED);
        quitBtn.setStyle("-fx-background-color: #00bcd4;  -fx-font-size: 18px;");

        quitBtn.setOnAction(e -> stage.close());
        playBtn.setOnAction(e -> lancerSimulateur());

        centerBox.getChildren().addAll(dronesConnectBtn, ajouterDroneBtn, playBtn, quitBtn);

        rootPane.setCenter(centerBox);

        scene.setRoot(rootPane);
        stage.setScene(scene);
        stage.show();
    }

    private void lancerSimulateur() {
        new Thread(() -> {
            Simulateur.main(new String[] {});
        }).start();
    }
}
