package it.unina.frontend.controller;

import it.unina.frontend.model.*;
import it.unina.frontend.util.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unina.frontend.exception.AttachmentServiceException;
import it.unina.frontend.exception.CommentServiceException;
import it.unina.frontend.service.AttachmentService;
import it.unina.frontend.service.CommentService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class IssueViewController implements Initializable {

    // --- Riferimenti all'FXML ---
    @FXML private Label lblTitle;
    @FXML private Label lblStatus;
    @FXML private Label lblPriority;
    @FXML private Label lblAuthor;
    @FXML private Label lblDate;
    @FXML private Label lblType;
    @FXML private TextArea txtDescription;
    @FXML private ImageView imgAttachment;
    @FXML private Label lblNoAttachment;
    @FXML private VBox vboxCommentsList;
    @FXML private Button btnAddComment;

    // --- Servizi ---
    private CommentService commentService;
    private AttachmentService attachmentService;

    // Stato corrente
    private int currentIssueId;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup Jackson e HttpClient
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        HttpClient sharedClient = HttpClient.newHttpClient();

        // Inizializzazione Service
        this.commentService = new CommentService(sharedClient, mapper);
        this.attachmentService = new AttachmentService(mapper);

        User user = SessionManager.getInstance().getCurrentUser();

        if (user != null && user.getRole() != null && user.getRole().equalsIgnoreCase("External")) {
            btnAddComment.setVisible(false);
        }
    }

    public void setIssueData(Issue issue) {
        if (issue == null) return;

        this.currentIssueId = issue.getId();

        lblTitle.setText("Issue #" + issue.getId() + ": " + (issue.getTitle() != null ? issue.getTitle() : "Nessun Titolo"));
        lblPriority.setText(issue.getPriority());
        lblStatus.setText(issue.getStatus());
        lblType.setText(issue.getType());
        txtDescription.setText(issue.getDescription());

        String authorName = (issue.getAuthor() != null) ? issue.getAuthor().getUsername() : "Sconosciuto";
        lblAuthor.setText(authorName);

        if (issue.getCreatedOn() != null) {
            lblDate.setText(issue.getCreatedOn().format(dateFormatter));
        }

        updateStatusColor(issue.getStatus());
        updatePriorityColor(issue.getPriority());

        // --- Caricamento Asincrono (Commenti e Allegati) ---
        new Thread(() -> {
            loadComments(this.currentIssueId);
            loadAttachment(this.currentIssueId);
        }).start();
    }

    @FXML
    public void handleOpenChangelog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unina/frontend/view/Changelog.fxml"));
            Parent root = loader.load();

            ChangelogController controller = loader.getController();
            HttpClient sharedClient = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            controller.initialize(this.currentIssueId, sharedClient, mapper);

            Stage stage = new Stage();
            stage.setTitle("Cronologia Issue #" + this.currentIssueId);
            Stage parentStage = (Stage) lblTitle.getScene().getWindow();
            stage.initOwner(parentStage);
            stage.setScene(new Scene(root));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            showAlert("Errore", "Impossibile aprire la cronologia: " + e.getMessage());
        }
    }

    @FXML
    public void handleAddComment() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Commento");
        dialog.setHeaderText("Scrivi il tuo commento");

        ButtonType buttonTypeInvia = new ButtonType("Invia", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeAnnulla = ButtonType.CANCEL;

        dialog.getDialogPane().getButtonTypes().addAll(buttonTypeInvia, buttonTypeAnnulla);

        Button btnInvia = (Button) dialog.getDialogPane().lookupButton(buttonTypeInvia);
        btnInvia.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");

        Button btnAnnulla = (Button) dialog.getDialogPane().lookupButton(buttonTypeAnnulla);
        btnAnnulla.setText("Annulla");
        btnAnnulla.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");

        TextArea textArea = new TextArea();
        textArea.setPromptText("Inserisci qui il commento...");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        textArea.setPrefWidth(400);
        Label charCountLabel = new Label("0/1000");
        charCountLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1000) {
                textArea.setText(oldValue);
            } else {
                charCountLabel.setText(newValue.length() + "/1000");
                if (newValue.length() >= 950) {
                    charCountLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-font-weight: bold;");
                } else {
                    charCountLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 11px;");
                }
            }
        });

        VBox content = new VBox(10);
        content.getChildren().addAll(textArea, charCountLabel);
        dialog.getDialogPane().setContent(content);

        Platform.runLater(textArea::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonTypeInvia) {
                return textArea.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(text -> {
            if (text.trim().isEmpty()) {
                showAlert("Attenzione", "Il commento non può essere vuoto.");
                return;
            }

            CommentRequest requestBody = new CommentRequest(text, this.currentIssueId);
            new Thread(() -> {
                try {
                    commentService.createComment(requestBody);
                    Platform.runLater(() -> loadComments(this.currentIssueId));
                } catch (CommentServiceException e) {
                    Platform.runLater(() -> showAlert("Errore", "Impossibile aggiungere il commento: " + e.getMessage()));
                } catch (Exception e) {
                    Platform.runLater(() -> showAlert("Errore Imprevisto", "Errore: " + e.getMessage()));
                }
            }).start();
        });
    }

    private void loadAttachment(int issueId) {
        try {
            List<Attachment> attachments = attachmentService.getAttachmentsByIssue(issueId);
            Platform.runLater(() -> {
                if (attachments != null && !attachments.isEmpty()) {
                    Attachment att = attachments.get(0);
                    if (att.getUrl() != null && !att.getUrl().isEmpty()) {
                        Image image = new Image(att.getUrl(), true);
                        imgAttachment.setImage(image);
                        imgAttachment.setVisible(true);
                        lblNoAttachment.setVisible(false);
                    } else {
                        showNoAttachmentPlaceholder();
                    }
                } else {
                    showNoAttachmentPlaceholder();
                }
            });
        } catch (AttachmentServiceException e) {
            boolean isConnectionError = e.getCause() != null && e.getCause().toString().contains("ConnectException");
            Platform.runLater(() -> {
                showNoAttachmentPlaceholder();
                if (isConnectionError) {
                    System.err.println("Impossibile contattare il server per gli allegati.");
                }
            });
        }
    }

    private void showNoAttachmentPlaceholder() {
        imgAttachment.setImage(null);
        imgAttachment.setVisible(false);
        lblNoAttachment.setVisible(true);
    }

    private void loadComments(int issueId) {
        try {
            List<CommentResponse> comments = commentService.getCommentsByIssue(issueId);
            Platform.runLater(() -> populateCommentsList(comments));
        } catch (CommentServiceException e) {
            boolean isConnectionError = e.getCause() != null && e.getCause().toString().contains("ConnectException");
            Platform.runLater(() -> {
                vboxCommentsList.getChildren().clear();
                Label err = new Label(isConnectionError ? "Server Offline: Impossibile scaricare i commenti." : "Errore caricamento commenti.");
                err.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                vboxCommentsList.getChildren().add(err);
            });
        }
    }

    private void populateCommentsList(List<CommentResponse> comments) {
        vboxCommentsList.getChildren().clear();

        if (comments == null || comments.isEmpty()) {
            Label placeholder = new Label("Nessun commento presente.");
            placeholder.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            vboxCommentsList.getChildren().add(placeholder);
            return;
        }

        for (CommentResponse c : comments) {
            VBox commentBox = new VBox(5);
            commentBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e1e4e8; -fx-border-radius: 6; -fx-padding: 10;");

            String author = (c.getAuthor() != null) ? c.getAuthor().getUsername() : "Utente";
            String date = (c.getCreatedOn() != null) ? c.getCreatedOn().format(dateFormatter) : "";

            Label header = new Label(author + " • " + date);
            header.setStyle("-fx-font-weight: bold; -fx-text-fill: #586069; -fx-font-size: 12px;");

            Text textNode = new Text(c.getText());
            textNode.setStyle("-fx-fill: #24292e; -fx-font-size: 14px;");
            TextFlow bodyFlow = new TextFlow(textNode);

            commentBox.getChildren().addAll(header, bodyFlow);
            vboxCommentsList.getChildren().add(commentBox);
        }
    }

    private void updateStatusColor(String status) {
        if (status == null) return;
        String baseStyle = "-fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 4; ";
        switch (status.toLowerCase()) {
            case "done": lblStatus.setStyle(baseStyle + "-fx-background-color: #5cb85c;"); break;
            case "in progress": lblStatus.setStyle(baseStyle + "-fx-background-color: #5bc0de;"); break;
            case "to do": lblStatus.setStyle(baseStyle + "-fx-background-color: #f0ad4e;"); break;
            default: lblStatus.setStyle(baseStyle + "-fx-background-color: #777;");
        }
    }

    private void updatePriorityColor(String priority) {
        if (priority == null) return;

        String baseStyle = "-fx-font-weight: bold; -fx-font-size: 15px; ";

        switch (priority.toLowerCase()) {
            case "high":
                lblPriority.setStyle(baseStyle + "-fx-text-fill: #e74c3c;");
                break;
            case "medium":
                lblPriority.setStyle(baseStyle + "-fx-text-fill: #e67e22;");
                break;
            case "low":
                lblPriority.setStyle(baseStyle + "-fx-text-fill: #f1c40f;");
                break;
            default:
                lblPriority.setStyle(baseStyle + "-fx-text-fill: #7f8c8d;");
                break;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}