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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class IssueViewController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(IssueViewController.class);

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

    private CommentService commentService;
    private AttachmentService attachmentService;
    private int currentIssueId;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String STYLE_BTN_CONFIRM = "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;";
    private static final String STYLE_BTN_CANCEL = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;";
    private static final String STYLE_COMMENT_BOX = "-fx-background-color: #ffffff; -fx-border-color: #e1e4e8; -fx-border-radius: 6; -fx-padding: 10;";
    private static final String STYLE_COMMENT_HEADER = "-fx-font-weight: bold; -fx-text-fill: #586069; -fx-font-size: 12px;";
    private static final String STYLE_COMMENT_BODY = "-fx-fill: #24292e; -fx-font-size: 14px;";
    private static final String STYLE_TEXT_ERR = "-fx-text-fill: #e74c3c; -fx-font-size: 11px; -fx-font-weight: bold;";
    private static final String STYLE_TEXT_MUTED = "-fx-text-fill: #999; -fx-font-size: 11px;";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupServices();
        checkPermissions();
    }

    private void setupServices() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        HttpClient sharedClient = HttpClient.newHttpClient();

        this.commentService = new CommentService(sharedClient, mapper);
        this.attachmentService = new AttachmentService(mapper);
    }

    private void checkPermissions() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null && "External".equalsIgnoreCase(user.getRole())) {
            btnAddComment.setVisible(false);
            btnAddComment.setManaged(false); // Rimuove anche lo spazio occupato
        }
    }

    public void setIssueData(Issue issue) {
        if (issue == null) return;
        this.currentIssueId = issue.getId();

        lblTitle.setText(String.format("Issue #%d: %s", issue.getId(), issue.getTitle() != null ? issue.getTitle() : "Nessun Titolo"));
        lblPriority.setText(issue.getPriority());
        lblStatus.setText(issue.getStatus());
        lblType.setText(issue.getType());
        txtDescription.setText(issue.getDescription());
        lblAuthor.setText(issue.getAuthor() != null ? issue.getAuthor().getUsername() : "Sconosciuto");

        if (issue.getCreatedOn() != null) {
            lblDate.setText(issue.getCreatedOn().format(dateFormatter));
        }

        updateStatusColor(issue.getStatus());
        updatePriorityColor(issue.getPriority());

        new Thread(() -> {
            loadComments(this.currentIssueId);
            loadAttachment(this.currentIssueId);
        }).start();
    }


    @FXML
    public void handleAddComment() {
        Dialog<String> dialog = createCommentDialog();
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(text -> {
            if (text.trim().isEmpty()) {
                showAlert("Attenzione", "Il commento non può essere vuoto.");
                return;
            }
            sendComment(text);
        });
    }

    private Dialog<String> createCommentDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Nuovo Commento");
        dialog.setHeaderText("Scrivi il tuo commento");

        ButtonType btnTypeInvia = new ButtonType("Invia", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnTypeInvia, ButtonType.CANCEL);

        Button btnInvia = (Button) dialog.getDialogPane().lookupButton(btnTypeInvia);
        btnInvia.setStyle(STYLE_BTN_CONFIRM);
        Button btnAnnulla = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        btnAnnulla.setText("Annulla");
        btnAnnulla.setStyle(STYLE_BTN_CANCEL);

        TextArea textArea = new TextArea();
        textArea.setPromptText("Inserisci qui il commento...");
        textArea.setWrapText(true);
        textArea.setPrefRowCount(5);
        textArea.setPrefWidth(400);

        Label charCountLabel = new Label("0/1000");
        charCountLabel.setStyle(STYLE_TEXT_MUTED);

        textArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 1000) {
                textArea.setText(oldVal);
            } else {
                charCountLabel.setText(newVal.length() + "/1000");
                charCountLabel.setStyle(newVal.length() >= 950 ? STYLE_TEXT_ERR : STYLE_TEXT_MUTED);
            }
        });

        VBox content = new VBox(10, textArea, charCountLabel);
        dialog.getDialogPane().setContent(content);
        Platform.runLater(textArea::requestFocus);

        dialog.setResultConverter(btn -> (btn == btnTypeInvia) ? textArea.getText() : null);

        return dialog;
    }

    private void sendComment(String text) {
        CommentRequest requestBody = new CommentRequest(text, this.currentIssueId);
        new Thread(() -> {
            try {
                commentService.createComment(requestBody);
                Platform.runLater(() -> loadComments(this.currentIssueId));
            } catch (CommentServiceException e) {
                showError("Errore Invio", "Impossibile aggiungere il commento: " + e.getMessage());
            } catch (Exception e) {
                showError("Errore Imprevisto", e.getMessage());
            }
        }).start();
    }

    private void loadComments(int issueId) {
        try {
            List<CommentResponse> comments = commentService.getCommentsByIssue(issueId);
            Platform.runLater(() -> populateCommentsList(comments));
        } catch (Exception e) {
            Platform.runLater(() -> {
                vboxCommentsList.getChildren().clear();
                Label err = new Label("Errore caricamento commenti.");
                err.setStyle(STYLE_TEXT_ERR);
                vboxCommentsList.getChildren().add(err);
            });
            logger.error("Errore loadComments: {}", e.getMessage());
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
            vboxCommentsList.getChildren().add(createCommentNode(c));
        }
    }

    private VBox createCommentNode(CommentResponse c) {
        VBox commentBox = new VBox(5);
        commentBox.setStyle(STYLE_COMMENT_BOX);

        String author = (c.getAuthor() != null) ? c.getAuthor().getUsername() : "Utente";
        String date = (c.getCreatedOn() != null) ? c.getCreatedOn().format(dateFormatter) : "";

        Label header = new Label(author + " • " + date);
        header.setStyle(STYLE_COMMENT_HEADER);

        Text textNode = new Text(c.getText());
        textNode.setStyle(STYLE_COMMENT_BODY);
        TextFlow bodyFlow = new TextFlow(textNode);

        commentBox.getChildren().addAll(header, bodyFlow);
        return commentBox;
    }


    private void loadAttachment(int issueId) {
        try {
            List<Attachment> attachments = attachmentService.getAttachmentsByIssue(issueId);
            Platform.runLater(() -> updateAttachmentUI(attachments));
        } catch (Exception e) {
            Platform.runLater(this::showNoAttachmentPlaceholder);
            logger.error("Errore loadAttachment: {}", e.getMessage());
        }
    }

    private void updateAttachmentUI(List<Attachment> attachments) {
        if (attachments != null && !attachments.isEmpty()) {
            Attachment att = attachments.get(0);
            if (att.getUrl() != null && !att.getUrl().isEmpty()) {
                imgAttachment.setImage(new Image(att.getUrl(), true));
                imgAttachment.setVisible(true);
                lblNoAttachment.setVisible(false);
                return;
            }
        }
        showNoAttachmentPlaceholder();
    }

    private void showNoAttachmentPlaceholder() {
        imgAttachment.setImage(null);
        imgAttachment.setVisible(false);
        lblNoAttachment.setVisible(true);
    }


    @FXML
    public void handleOpenChangelog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unina/frontend/view/Changelog.fxml"));
            Parent root = loader.load();

            ChangelogController controller = loader.getController();
            controller.initialize(this.currentIssueId, HttpClient.newHttpClient(), new ObjectMapper().registerModule(new JavaTimeModule()));

            Stage stage = new Stage();
            stage.setTitle("Cronologia Issue #" + this.currentIssueId);
            stage.initOwner(lblTitle.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            showAlert("Errore", "Impossibile aprire la cronologia: " + e.getMessage());
        }
    }

    private void updateStatusColor(String status) {
        if (status == null) return;
        String color = switch (status.toLowerCase()) {
            case "done" -> "#5cb85c";
            case "in progress" -> "#5bc0de";
            case "to do" -> "#f0ad4e";
            default -> "#777";
        };
        lblStatus.setStyle("-fx-text-fill: white; -fx-padding: 2 8; -fx-background-radius: 4; -fx-background-color: " + color + ";");
    }

    private void updatePriorityColor(String priority) {
        if (priority == null) return;
        String color = switch (priority.toLowerCase()) {
            case "high" -> "#e74c3c";
            case "medium" -> "#e67e22";
            case "low" -> "#f1c40f";
            default -> "#7f8c8d";
        };
        lblPriority.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: " + color + ";");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}