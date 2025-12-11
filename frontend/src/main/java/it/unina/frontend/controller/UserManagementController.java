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

    private UserService userService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        HttpClient client = HttpClient.newBuilder().build();
        ObjectMapper mapper = new ObjectMapper();
        this.userService = new UserService(client, mapper);

        cmbRole.getItems().addAll("Normal", "Admin", "External");
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
            showAlert("Errore di Validazione", "Tutti i campi sono obbligatori."); // Messaggio aggiornato
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
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
