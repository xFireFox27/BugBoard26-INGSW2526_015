package it.unina.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.frontend.exception.AuthServiceException;
import it.unina.frontend.model.LoginRequest;
import it.unina.frontend.model.LoginResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {

    private static final String API_URL = "http://localhost:8080/api/login";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public AuthService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    public LoginResponse login(String email, String password) throws AuthServiceException {
        try {
            LoginRequest loginRequest = new LoginRequest(email, password);
            String jsonBody = mapper.writeValueAsString(loginRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), LoginResponse.class);
            } else {
                throw new AuthServiceException("Login fallito: " + response.statusCode(), response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AuthServiceException("Operazione di login interrotta", e);
        } catch (Exception e) {
            throw new AuthServiceException("Errore durante il login", e);
        }
    }
}
