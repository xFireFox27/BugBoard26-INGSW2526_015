package it.unina.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Caricamento iniziale (Login)
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/it/unina/frontend/view/login.fxml"));

        // Login: dimensione più piccola
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        stage.setTitle("Gestionale Unina - BugBoard");
        stage.setScene(scene);

        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.centerOnScreen(); // Centra la prima volta
        stage.show();
    }

    /**
     * Metodo per cambiare scena e centrare la finestra
     */
    public static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/it/unina/frontend/view/" + fxml + ".fxml"));

        // Carichiamo la scena.
        // Se vuoi dimensioni specifiche per la Home, puoi cambiarle qui (es. 1200, 800)
        // Altrimenti eredita la dimensione precedente o quella definita nel FXML
        Scene scene = new Scene(fxmlLoader.load());

        // Riapplica lo stile Bootstrap (va perso al cambio scena)
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        // Imposta la nuova scena
        primaryStage.setScene(scene);

        // --- LA SOLUZIONE AL TUO PROBLEMA ---
        // Ridimensiona la finestra in base al contenuto della nuova scena
        primaryStage.sizeToScene();
        // Centra la finestra nello schermo
        primaryStage.centerOnScreen();

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}