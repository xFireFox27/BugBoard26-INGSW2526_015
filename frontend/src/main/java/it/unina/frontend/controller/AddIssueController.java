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

    @FXML private TextField titleField;
    @FXML private TextArea descArea;
    @FXML private ComboBox<String> typeCombo;
    @FXML private ComboBox<String> priorityCombo;
    @FXML private Label fileNameLabel;

    @FXML private Label titleCharCountLabel;
    @FXML private Label descCharCountLabel;

    private final IssueService issueService = new IssueService();
    private final AttachmentService attachmentService = new AttachmentService(new ObjectMapper());
    private File selectedFile;

    @FXML
    public void initialize() {
        // Setup Combo
        typeCombo.getItems().addAll("Bug", "Feature", "Documentation", "Question");
        typeCombo.getSelectionModel().select("Bug");

        priorityCombo.getItems().addAll("Low", "Medium", "High");
        priorityCombo.getSelectionModel().select("Low");

        // --- LIMITATORE TITOLO (Max 100) ---
        final int MAX_TITLE_CHARS = 100;
        titleCharCountLabel.setText("0/" + MAX_TITLE_CHARS);

        titleField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            if (newVal.length() > MAX_TITLE_CHARS) {
                titleField.setText(newVal.substring(0, MAX_TITLE_CHARS));
                return;
            }
            updateCharCountLabel(titleCharCountLabel, newVal.length(), MAX_TITLE_CHARS);
        });

        // --- LIMITATORE DESCRIZIONE (Max 1000) ---
        final int MAX_DESC_CHARS = 1000;
        descCharCountLabel.setText("0/" + MAX_DESC_CHARS);

        descArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            if (newVal.length() > MAX_DESC_CHARS) {
                descArea.setText(newVal.substring(0, MAX_DESC_CHARS));
                return;
            }
            updateCharCountLabel(descCharCountLabel, newVal.length(), MAX_DESC_CHARS);
        });
    }

    // Metodo helper per evitare codice duplicato
    private void updateCharCountLabel(Label label, int currentLength, int max) {
        label.setText(currentLength + "/" + max);
        if (currentLength >= max) {
            label.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
        } else {
            label.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11px;");
        }
    }

    @FXML
    public void onSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Allegato");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("Documenti PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Tutti i file", "*.*")
        );

        File file = fileChooser.showOpenDialog(fileNameLabel.getScene().getWindow());

        if (file != null) {
            this.selectedFile = file;
            fileNameLabel.setText(file.getName());
            fileNameLabel.setStyle("-fx-text-fill: #2980b9; -fx-font-weight: bold;");
        }
    }

    @FXML
    public void onSave() {
        if (titleField.getText().isEmpty() || descArea.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Dati Mancanti", "Titolo e Descrizione sono obbligatori.");
            return;
        }

        IssueCreateRequest request = new IssueCreateRequest(
                titleField.getText(),
                descArea.getText(),
                typeCombo.getValue(),
                priorityCombo.getValue(),
                "To Do"
        );

        try {
            Issue createdIssue = issueService.createIssue(request);

            if (selectedFile != null) {
                try {
                    attachmentService.uploadAttachment(selectedFile, createdIssue.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.WARNING, "Upload Fallito",
                            "Issue creata (ID: " + createdIssue.getId() + "), ma allegato fallito: " + e.getMessage());
                    MainApp.setRoot("home");
                    return;
                }
            }

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