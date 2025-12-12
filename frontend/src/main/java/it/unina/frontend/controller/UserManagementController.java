package it.unina.frontend.controller;

import it.unina.frontend.MainApp;
import it.unina.frontend.exception.UserServiceException;
import it.unina.frontend.model.UserRequest;
import it.unina.frontend.model.User;
import it.unina.frontend.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private UserService userService;

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

        addSilentLimit(txtUsername, MAX_CHARS);
        addSilentLimit(txtEmail, MAX_CHARS);
        addSilentLimit(txtName, MAX_CHARS);
        addSilentLimit(txtSurname, MAX_CHARS);

        pwdPassword.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            if (newVal.length() > MAX_CHARS) {
                pwdPassword.setText(newVal.substring(0, MAX_CHARS));
                return;
            }

            if (newVal.length() == 0) {
                lblPasswordStatus.setText("La password deve avere almeno 8 caratteri");
                lblPasswordStatus.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 11px;"); // Grigio
            } else if (newVal.length() < 8) {
                lblPasswordStatus.setText("Password troppo corta (" + newVal.length() + "/8)");
                lblPasswordStatus.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 11px;"); // Rosso
            } else {
                lblPasswordStatus.setText("Password valida ✓");
                lblPasswordStatus.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 11px;"); // Verde
            }
        });
    }

    private void addSilentLimit(TextField field, int max) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            if (newVal.length() > max) {
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
            showAlert("Errore di Validazione", "Tutti i campi sono obbligatori.");
            return;
        }

        if (password.length() < 8) {
            showAlert("Password Debole", "La password deve contenere almeno 8 caratteri.");
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
                showAlert("Successo", "Utente '" + newUser.getUsername() + "' creato con successo!");
                clearForm();
            } catch (UserServiceException e) {
                e.printStackTrace();
                showAlert("Errore di Creazione", "Impossibile creare l'utente. Dettagli: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleBackAction() {
        try {
            MainApp.setRoot("home");
        } catch (IOException e) {
            showAlert("Errore", "Impossibile tornare alla Dashboard.");
        }
    }

    private void clearForm() {
        txtUsername.clear();
        txtEmail.clear();
        txtName.clear();
        txtSurname.clear();
        pwdPassword.clear();
        cmbRole.getSelectionModel().clearSelection();

        lblPasswordStatus.setText("La password deve avere almeno 8 caratteri");
        lblPasswordStatus.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-font-size: 11px;");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}