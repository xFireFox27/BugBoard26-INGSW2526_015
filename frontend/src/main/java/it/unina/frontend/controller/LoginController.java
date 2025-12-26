package it.unina.frontend.controller;

import it.unina.frontend.MainApp;
import it.unina.frontend.model.LoginResponse;
import it.unina.frontend.service.AuthService;
import it.unina.frontend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordTextField;
    @FXML private FontIcon passwordIcon;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        passwordTextField.textProperty().bindBidirectional(passwordField.textProperty());
    }

    @FXML
    protected void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            passwordIcon.setIconLiteral("fas-eye-slash"); // Cambia icona
        } else {
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordIcon.setIconLiteral("fas-eye"); // Ripristina icona
        }
    }

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Inserisci email e password");
            return;
        }

        try {
            LoginResponse response = authService.login(email, password);
            SessionManager.getInstance().setToken(response.getToken());
            SessionManager.getInstance().setCurrentUser(response.getUser());
            System.out.println("Login effettuato! Ruolo: " + response.getUser().getRole());
            MainApp.setRoot("home");

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Errore caricamento Home: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Credenziali non valide o errore server.");
        }
    }
}
