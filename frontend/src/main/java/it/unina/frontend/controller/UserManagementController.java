package it.unina.frontend.controller;

import it.unina.frontend.MainApp;
import it.unina.frontend.exception.UserServiceException;
import it.unina.frontend.model.UserRequest;
import it.unina.frontend.model.User;
import it.unina.frontend.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.util.ResourceBundle;

public class UserManagementController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtName;
    @FXML private TextField txtSurname;
    @FXML private PasswordField pwdPassword;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Button btnCreateUser;
    @FXML private Button btnBack;

    @FXML private Label lblPasswordStatus;
    @FXML private Label lblEmailStatus;

    private UserService userService;

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,}$";
    private static final String PASSWORD_LENGTH_ERROR = "La password deve contenere almeno 8 caratteri";
    private static final String ERROR_BOX_STYLE = "-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 11px;";
    private static final String VALID_BOX_STYLE = "-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 11px;";

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HttpClient client = HttpClient.newBuilder().build();
        ObjectMapper mapper = new ObjectMapper();
        this.userService = new UserService(client, mapper);

        cmbRole.getItems().addAll("Normal", "Admin", "External");

        setupValidation();
    }

    private void setupValidation() {
        final int MAX_CHARS = 100;
        final int MIN_CHARS = 8;

        addSilentLimit(txtUsername, MAX_CHARS);
        addSilentLimit(txtEmail, MAX_CHARS);
        addSilentLimit(txtName, MAX_CHARS);
        addSilentLimit(txtSurname, MAX_CHARS);

        lblEmailStatus.setText("");
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                lblEmailStatus.setText("");
                return;
            }
            // Tronca se troppo lungo
            if (newVal.length() > MAX_CHARS) txtEmail.setText(newVal.substring(0, MAX_CHARS));

            if (newVal.matches(EMAIL_REGEX)) {
                lblEmailStatus.setText("Vaild Email ✓");
                lblEmailStatus.setStyle(VALID_BOX_STYLE);
            } else {
                lblEmailStatus.setText("Email format is incorrect");
                lblEmailStatus.setStyle(ERROR_BOX_STYLE);
            }
        });

        lblPasswordStatus.setText(PASSWORD_LENGTH_ERROR);
        lblPasswordStatus.setStyle(ERROR_BOX_STYLE);

        pwdPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > MAX_CHARS) pwdPassword.setText(newVal.substring(0, MAX_CHARS));

            if (newVal == null || newVal.isEmpty()) {
                lblPasswordStatus.setText(PASSWORD_LENGTH_ERROR);
                lblPasswordStatus.setStyle(ERROR_BOX_STYLE);
            } else if (newVal.length() < MIN_CHARS) {
                lblPasswordStatus.setText(PASSWORD_LENGTH_ERROR + "(Password length: " + newVal.length() + "/8)");
                lblPasswordStatus.setStyle(ERROR_BOX_STYLE);
            } else {
                lblPasswordStatus.setText("Valid Password ✓");
                lblPasswordStatus.setStyle(VALID_BOX_STYLE);
            }
        });
    }

    private void addSilentLimit(TextField field, int max) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > max) {
                field.setText(newVal.substring(0, max));
            }
        });
    }

    @FXML
    private void handleCreateUserAction() {
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String name = txtName.getText();
        String surname = txtSurname.getText();
        String password = pwdPassword.getText();
        String role = cmbRole.getValue();

        if (username.isEmpty() || email.isEmpty() || name.isEmpty() || surname.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Dati mancanti",
                                            "Compila tutti i campi obbligatori.");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            showAlert(Alert.AlertType.WARNING, "Email non valida",
                                            "Correggi il formato dell'email.");
            return;
        }
        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Password debole", PASSWORD_LENGTH_ERROR);
            return;
        }

        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(username);
        userRequest.setEmail(email);
        userRequest.setName(name);
        userRequest.setSurname(surname);
        userRequest.setPassword(password);
        userRequest.setRole(role);

        Platform.runLater(() -> {
            try {
                User newUser = userService.createUser(userRequest);

                showSuccessNotification("Utente creato!",
                                        "L'utente " + newUser.getUsername() + " è stato aggiunto.");

                clearForm();
            } catch (UserServiceException e) {
                showAlert(Alert.AlertType.ERROR, "Errore","Impossibile creare utente: " + e.getMessage());
                logger.error("Impossiblie creare utente: {}", e.getMessage(), e);
            }
        });
    }

    private void showSuccessNotification(String title, String text) {
        FontIcon icon = new FontIcon("fas-check-circle");
        icon.setIconColor(Color.web("#2ecc71"));
        icon.setIconSize(48);

        Notifications.create()
                .title(title)
                .text(text)
                .owner(btnCreateUser)
                .hideAfter(Duration.seconds(4))
                .position(Pos.BOTTOM_RIGHT)
                .graphic(icon)

                .show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleBackAction() {
        try {
            MainApp.setRoot("home");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void clearForm() {
        txtUsername.clear();
        txtEmail.clear();
        txtName.clear();
        txtSurname.clear();
        pwdPassword.clear();
        cmbRole.getSelectionModel().clearSelection();
        lblEmailStatus.setText("");
        lblPasswordStatus.setText(PASSWORD_LENGTH_ERROR);
        lblPasswordStatus.setStyle(ERROR_BOX_STYLE);
    }
}