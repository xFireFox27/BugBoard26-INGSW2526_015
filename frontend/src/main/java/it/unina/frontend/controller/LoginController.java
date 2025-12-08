package it.unina.frontend.controller;

import it.unina.frontend.MainApp;
import it.unina.frontend.model.LoginResponse;
import it.unina.frontend.service.AuthService;
import it.unina.frontend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    protected void onLoginButtonClick() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Inserisci email e password");
            return;
        }

        try {
            // 1. Chiamata al backend
            LoginResponse response = authService.login(email, password);

            // 2. Salva la sessione (Token e Utente)
            SessionManager.getInstance().setToken(response.getToken());
            SessionManager.getInstance().setCurrentUser(response.getUser());

            System.out.println("Login effettuato! Ruolo: " + response.getUser().getRole());

            // 3. CAMBIO SCENA: Vai alla Home
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