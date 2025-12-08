package it.unina.frontend.controller;

import it.unina.frontend.MainApp;
import it.unina.frontend.model.IssueCreateRequest;
import it.unina.frontend.service.IssueService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AddIssueController {

    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> priorityCombo;

    private final IssueService issueService = new IssueService();

    @FXML
    public void initialize() {
        // Valori esatti richiesti dal Backend
        typeCombo.getItems().addAll("Bug", "Feature", "Documentation", "Question");
        typeCombo.getSelectionModel().select("Bug");

        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.getSelectionModel().select("Low");
    }

    @FXML
    public void onSave() {
        if (titleField.getText().isEmpty() || descArea.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Titolo e Descrizione sono obbligatori.");
            return;
        }

        // Creazione Oggetto Request
        IssueCreateRequest request = new IssueCreateRequest(
                titleField.getText(),
                descArea.getText(),
                typeCombo.getValue(),
                priorityCombo.getValue(),
                "To Do" // Default status iniziale
        );

        try {
            // Chiamata al Service
            issueService.createIssue(request);

            showAlert(Alert.AlertType.INFORMATION, "Fatto", "Segnalazione creata con successo!");
            MainApp.setRoot("home"); // Torna alla Dashboard

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile creare issue: " + e.getMessage());
        }
    }

    @FXML
    public void onCancel() throws IOException {
        MainApp.setRoot("home");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}