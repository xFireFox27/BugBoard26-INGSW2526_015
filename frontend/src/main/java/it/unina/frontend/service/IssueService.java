package it.unina.frontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unina.frontend.exception.IssueServiceException;
import it.unina.frontend.model.Issue;
import it.unina.frontend.model.IssueCreateRequest;
import it.unina.frontend.util.ApiConfig;
import it.unina.frontend.util.SessionManager;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class IssueService {

    private static final String API_URL = ApiConfig.BASE_URL + "/issues";
    private static final String ALL_FILTER = "Tutti";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public IssueService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
    }

    public List<Issue> getFilteredIssues(String status, String type, String priority, String sortBy) throws IssueServiceException {
        String token = SessionManager.getInstance().getToken();

        StringBuilder query = new StringBuilder("?");

        if (status != null && !status.isEmpty() && !ALL_FILTER.equals(status)) {
            query.append("status=").append(URLEncoder.encode(status, StandardCharsets.UTF_8)).append("&");
        }
        if (type != null && !type.isEmpty() && !ALL_FILTER.equals(type)) {
            query.append("type=").append(URLEncoder.encode(type, StandardCharsets.UTF_8)).append("&");
        }
        if (priority != null && !priority.isEmpty() && !ALL_FILTER.equals(priority)) {
            query.append("priority=").append(URLEncoder.encode(priority, StandardCharsets.UTF_8)).append("&");
        }
        if (sortBy != null && !sortBy.isEmpty()) {
            query.append("sort-by=").append(URLEncoder.encode(sortBy, StandardCharsets.UTF_8));
        }

        String fullUrl = API_URL + query.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), new TypeReference<List<Issue>>() {});
            } else {
                throw new IssueServiceException("Errore ricerca (" + response.statusCode() + "): "
                        + response.body(), response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IssueServiceException("Operazione interrotta durante la ricerca delle issue", e);
        } catch (Exception e) {
            throw new IssueServiceException("Errore durante la ricerca delle issue", e);
        }
    }

    public List<Issue> getAllIssues() throws IssueServiceException {
        return getFilteredIssues(null, null, null, null);
    }


    public Issue createIssue(IssueCreateRequest requestBody) throws IssueServiceException {
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

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                return mapper.readValue(response.body(), Issue.class);
            } else {
                throw new IssueServiceException("Errore creazione (" + response.statusCode() + "): " + response.body(), response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IssueServiceException("Operazione interrotta durante la creazione dell'issue", e);
        } catch (Exception e) {
            throw new IssueServiceException("Errore durante la creazione dell'issue", e);
        }
    }

}
