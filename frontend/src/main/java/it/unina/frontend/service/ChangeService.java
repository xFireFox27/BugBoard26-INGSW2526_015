package it.unina.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.frontend.exception.ChangeServiceException;
import it.unina.frontend.model.Change;
import it.unina.frontend.util.ApiConfig;
import it.unina.frontend.util.SessionManager;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ChangeService {
    private static final String API_URL = ApiConfig.BASE_URL + "/changes";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public ChangeService(HttpClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public List<Change> getChangesByIssue(int issueId) {
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
                return mapper.readValue(response.body(), new TypeReference<List<Change>>() {});
            }
            else if(response.statusCode() == 404){
                return List.of();
            }
            else{
                throw new ChangeServiceException("Error getting changes for the issue" + response.statusCode());
            }
        }
        catch(InterruptedException e){
            throw new ChangeServiceException("Operation interrupted", e);
        }
        catch(Exception e){
            throw new ChangeServiceException("Error getting changes for the issue", e);
        }
    }

}
