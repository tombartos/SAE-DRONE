package fr.univtln.infomath.dronsim.client.launcher;

import javafx.application.Application;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

/**
 * Main application class for launching the JavaFX application.
 * Initializes the login page and manages the lifecycle of the application.
 */
@Slf4j
public final class App extends Application {

    /**
     * The main entry point of the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the JavaFX application.
     * Displays the login page with the specified dimensions.
     *
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        int width = 1200;
        int height = 800;
        LoginPage.showLoginPage(stage, width, height);
    }

    /**
     * Stops the application and releases resources.
     * Closes the {@link EntityManagerFactory} if it is initialized.
     */
    @Override
    public void stop() {
        System.exit(0);
    }
}

// Pour executer cette classe,
//
// mvn exec:java
//
// ce qui revient à
//
// mvn exec:java "-Dexec.mainClass=fr.univtln.m1im.png.App"
