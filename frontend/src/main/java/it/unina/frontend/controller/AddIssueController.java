package it.unina.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.frontend.MainApp;
import it.unina.frontend.model.Issue;
import it.unina.frontend.model.IssueCreateRequest;
import it.unina.frontend.service.AttachmentService;
import it.unina.frontend.service.IssueService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class AddIssueController {

    // Campi FXML
    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private Label fileNameLabel; // Label per mostrare il file scelto

    // Services
    private final IssueService issueService = new IssueService();
    private final AttachmentService attachmentService = new AttachmentService(new ObjectMapper());

    // Stato locale
    private File selectedFile;

    @FXML
    public void initialize() {
        // Popolo i menu con i valori validi del backend
        typeCombo.getItems().addAll("Bug", "Feature", "Documentation", "Question");
        typeCombo.getSelectionModel().select("Bug");

        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.getSelectionModel().select("Low");
    }

    @FXML
    public void onSelectFile() {
        // Configuro il FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Allegato");

        // Filtri per tipo di file (Immagini e PDF come esempio, puoi aggiungere altro)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Documenti PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Tutti i file", "*.*")
        );

        // Apro la finestra di dialogo sopra la finestra attuale
        File file = fileChooser.showOpenDialog(fileNameLabel.getScene().getWindow());

        if (file != null) {
            this.selectedFile = file;
            // Aggiorno la UI
            fileNameLabel.setText(file.getName());
            fileNameLabel.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void onSave() {
        // 1. Validazione Input
        if (titleField.getText().isEmpty() || descArea.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Dati Mancanti", "Titolo e Descrizione sono obbligatori.");
            return;
        }

        // 2. Preparazione Oggetto Request
        IssueCreateRequest request = new IssueCreateRequest(
                titleField.getText(),
                descArea.getText(),
                typeCombo.getValue(),
                priorityCombo.getValue(),
                "To Do" // Status iniziale
        );

        try {
            // STEP A: Creazione della Issue nel DB
            Issue createdIssue = issueService.createIssue(request);

            // STEP B: Upload dell'allegato (se presente)
            if (selectedFile != null) {
                try {
                    // Passo l'ID della issue appena creata
                    attachmentService.uploadAttachment(selectedFile, createdIssue.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    // Se l'upload fallisce, avvisiamo l'utente ma non blocchiamo il successo della issue
                    showAlert(Alert.AlertType.WARNING, "Upload Fallito",
                            "La segnalazione è stata creata (ID: " + createdIssue.getId() + "), " +
                                    "ma non è stato possibile caricare l'allegato: " + e.getMessage());
                    MainApp.setRoot("home");
                    return;
                }
            }

            // Tutto ok
            showAlert(Alert.AlertType.INFORMATION, "Successo", "Segnalazione creata correttamente!");
            MainApp.setRoot("home");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Errore Critico", "Impossibile creare la segnalazione: " + e.getMessage());
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