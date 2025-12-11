package it.unina.frontend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.frontend.exception.UserServiceException;
import it.unina.frontend.model.UserRequest;
import it.unina.frontend.model.User;
import it.unina.frontend.util.ApiConfig;
import it.unina.frontend.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UserService {

    private static final String API_URL = ApiConfig.BASE_URL + "/register";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public UserService(HttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public User createUser(UserRequest requestBody) {
        String token = SessionManager.getInstance().getToken();

        try {
            String jsonBody = mapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                User createdUser = new User();
                createdUser.setUsername(requestBody.getUsername());
                createdUser.setEmail(requestBody.getEmail());
                createdUser.setName(requestBody.getName());
                createdUser.setSurname(requestBody.getSurname());
                createdUser.setRole(requestBody.getRole());

                return createdUser;
            } else {
                throw new UserServiceException("Errore dal server: " + response.statusCode() + " - " + response.body(), response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new UserServiceException("Operazione interrotta", e);
        } catch (Exception e) {
            throw new UserServiceException("Errore durante la creazione dell'utente: " + e.getMessage(), e);
        }
    }
}