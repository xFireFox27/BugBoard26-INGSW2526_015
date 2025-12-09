package it.unina.frontend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unina.frontend.MainApp;
import it.unina.frontend.exception.AttachmentServiceException;
import it.unina.frontend.exception.CommentServiceException;
import it.unina.frontend.model.Attachment;
import it.unina.frontend.model.CommentRequest;
import it.unina.frontend.model.CommentResponse;
import it.unina.frontend.model.Issue;
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
    @FXML private Button btnBack;
    @FXML private Label lblTitle;
    @FXML private Label lblStatus;
    @FXML private Label lblPriority;
    @FXML private Label lblAuthor;
    @FXML private Label lblDate;
    @FXML private TextArea txtDescription;
    @FXML private ImageView imgAttachment;
    @FXML private Label lblNoAttachment;
    @FXML private VBox vboxCommentsList;

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

        // Listener
        btnBack.setOnAction(event -> handleBackAction());
    }

    public void setIssueData(Issue issue) {
        if (issue == null) return;

        // Memorizziamo l'ID per usarlo successivamente (es. per creare commenti)
        this.currentIssueId = issue.getId();

        // --- Popolamento Dati Statici ---
        lblTitle.setText("Bug #" + issue.getId() + ": " + (issue.getTitle() != null ? issue.getTitle() : "Nessun Titolo"));
        lblPriority.setText(issue.getPriority());
        lblStatus.setText(issue.getStatus());
        txtDescription.setText(issue.getDescription());

        String authorName = (issue.getAuthor() != null) ? issue.getAuthor().getUsername() : "Sconosciuto";
        lblAuthor.setText(authorName);

        if (issue.getCreatedOn() != null) {
            lblDate.setText(issue.getCreatedOn().format(dateFormatter));
        }

        updateStatusColor(issue.getStatus());

        // --- Caricamento Asincrono (Commenti e Allegati) ---
        new Thread(() -> {
            loadComments(this.currentIssueId);
            loadAttachment(this.currentIssueId);
        }).start();
    }

    /**
     * Gestisce il click sul bottone "Aggiungi Commento".
     * Apre un dialog, crea il CommentRequest e chiama il Service.
     */
    @FXML
    public void handleAddComment() {
        // 1. Chiediamo il testo all'utente
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nuovo Commento");
        dialog.setHeaderText("Scrivi il tuo commento");
        dialog.setContentText("Testo:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(text -> {
            if (text.trim().isEmpty()) {
                showAlert("Attenzione", "Il commento non può essere vuoto.");
                return;
            }

            // 2. Creiamo l'oggetto Request usando il costruttore @AllArgsConstructor di Lombok
            //
            CommentRequest requestBody = new CommentRequest(text, this.currentIssueId);

            // 3. Eseguiamo la chiamata al service in un thread separato
            new Thread(() -> {
                try {
                    // Chiamata esatta al metodo del service
                    commentService.createComment(requestBody);

                    // Se va a buon fine, ricarichiamo la lista dei commenti
                    Platform.runLater(() -> loadComments(this.currentIssueId));

                } catch (CommentServiceException e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert("Errore", "Impossibile aggiungere il commento: " + e.getMessage()));
                } catch (Exception e) {
                    e.printStackTrace();
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
            // Gestione specifica per server down
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
            case "done": case "completed": lblStatus.setStyle(baseStyle + "-fx-background-color: #5cb85c;"); break;
            case "in progress": lblStatus.setStyle(baseStyle + "-fx-background-color: #5bc0de;"); break;
            case "to do": case "open": lblStatus.setStyle(baseStyle + "-fx-background-color: #f0ad4e;"); break;
            default: lblStatus.setStyle(baseStyle + "-fx-background-color: #777;");
        }
    }

    private void handleBackAction() {
        try {
            MainApp.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Errore", "Impossibile tornare alla Dashboard.");
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