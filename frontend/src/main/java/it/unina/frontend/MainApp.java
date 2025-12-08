package it.unina.frontend;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX; // Importante per lo stile

import java.io.IOException;

public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/it/unina/frontend/view/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        stage.setTitle("Gestionale Unina - BugBoard");
        stage.setScene(scene);

        stage.setResizable(true);

        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.show();
    }

    /**
     * Metodo helper per cambiare scena facilmente da altre classi (es. dal Controller)
     */
    public static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/it/unina/frontend/view/" + fxml + ".fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        // Ricordati di riapplicare Bootstrap anche nelle nuove scene!
        scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}