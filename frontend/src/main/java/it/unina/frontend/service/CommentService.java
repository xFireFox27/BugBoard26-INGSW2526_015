package it.unina.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.frontend.exception.CommentServiceException;
import it.unina.frontend.model.CommentRequest;
import it.unina.frontend.model.CommentResponse;
import it.unina.frontend.util.SessionManager;
import it.unina.frontend.util.ApiConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CommentService {

    private static final String API_URL = ApiConfig.BASE_URL + "/comments";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public CommentService(HttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }
    public CommentResponse createComment(CommentRequest requestBody){
        String token = SessionManager.getInstance().getToken();

        try{
            String jsonBody = mapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 || response.statusCode() == 201){
                return mapper.readValue(response.body(), CommentResponse.class);
            }
            else{
                throw new CommentServiceException("Error creating comment" + response.statusCode());
            }
        }
        catch(InterruptedException e){
            throw new CommentServiceException("Operation interrupted" + e);
        }
        catch(Exception e){
            throw new CommentServiceException("Error creating comment" + e);
        }
    }

    public List<CommentResponse> getCommentsByIssue(int issueId){
        String token = SessionManager.getInstance().getToken();

        String url = API_URL + "?issue-id=" + issueId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .GET()
                .build();
        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                return mapper.readValue(response.body(), new TypeReference<List<CommentResponse>>() {});
            }
            else if(response.statusCode() == 400){
                return List.of();
            }
            else{
                throw new CommentServiceException("Error getting comments: " + response.statusCode());
            }
        }
        catch(InterruptedException e){
            throw new CommentServiceException("Operation interrupted", e);
        }
        catch(Exception e){
            throw new CommentServiceException("Error getting comments" + e);
        }
    }
}
