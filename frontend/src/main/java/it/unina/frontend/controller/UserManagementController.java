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

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

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

        // Limite "Silenzioso" (tronca senza dire nulla)
        addSilentLimit(txtUsername, MAX_CHARS);
        addSilentLimit(txtEmail, MAX_CHARS);
        addSilentLimit(txtName, MAX_CHARS);
        addSilentLimit(txtSurname, MAX_CHARS);

        // --- VALIDAZIONE VISIVA EMAIL ---
        lblEmailStatus.setText("");
        txtEmail.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            // Tronca se troppo lungo
            if (newVal.length() > MAX_CHARS) txtEmail.setText(newVal.substring(0, MAX_CHARS));

            if (newVal.isEmpty()) {
                lblEmailStatus.setText("");
                return;
            }
            if (newVal.matches(EMAIL_REGEX)) {
                lblEmailStatus.setText("Email valida ✓");
                lblEmailStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 11px; -fx-font-weight: bold;");
            } else {
                lblEmailStatus.setText("Formato non valido");
                lblEmailStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 11px;");
            }
        });

        // --- VALIDAZIONE VISIVA PASSWORD ---
        lblPasswordStatus.setText("La password deve avere almeno 8 caratteri");
        lblPasswordStatus.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 11px;");

        pwdPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            if (newVal.length() > MAX_CHARS) pwdPassword.setText(newVal.substring(0, MAX_CHARS));

            if (newVal.isEmpty()) {
                lblPasswordStatus.setText("La password deve avere almeno 8 caratteri");
                lblPasswordStatus.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 11px;");
            } else if (newVal.length() < 8) {
                lblPasswordStatus.setText("Password troppo corta (" + newVal.length() + "/8)");
                lblPasswordStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;");
            } else {
                lblPasswordStatus.setText("Password valida ✓");
                lblPasswordStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 11px;");
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

        // Controlli bloccanti (mostrano Alert perché sono errori dell'utente)
        if (username.isEmpty() || email.isEmpty() || name.isEmpty() || surname.isEmpty() || password.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Dati mancanti", "Compila tutti i campi obbligatori.");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            showAlert(Alert.AlertType.WARNING, "Email Invalida", "Correggi il formato dell'email.");
            return;
        }
        if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Password Debole", "La password deve essere di almeno 8 caratteri.");
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

                // --- SUCCESSO: Mostra NOTIFICA TOAST (Verde, Basso a Destra) ---
                showSuccessNotification("Utente creato!", "L'utente " + newUser.getUsername() + " è stato aggiunto.");

                clearForm();
            } catch (UserServiceException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Errore", "Impossibile creare utente: " + e.getMessage());
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
            e.printStackTrace();
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
        lblPasswordStatus.setText("La password deve avere almeno 8 caratteri");
        lblPasswordStatus.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 11px;");
    }
}