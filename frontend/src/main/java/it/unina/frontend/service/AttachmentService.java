package it.unina.frontend.service;

import it.unina.frontend.util.ApiConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unina.frontend.exception.AttachmentServiceException;
import it.unina.frontend.model.Attachment;
import it.unina.frontend.util.SessionManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentService {

    private static final String GET_URL = ApiConfig.BASE_URL + "/attachments/issue/{issue-id}";
    private static final String UPLOAD_URL = ApiConfig.BASE_URL + "/attachments/upload";

    private final HttpClient client;
    ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(AttachmentService.class);

    public AttachmentService(ObjectMapper mapper) {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(60))
                .build();
        this.mapper = mapper;
    }

    public void uploadAttachment(File file, Integer issueId) throws IOException, InterruptedException {
        String token = SessionManager.getInstance().getToken();
        String boundary = "MioBoundary" + System.currentTimeMillis();

        byte[] fullBody = buildCompleteMultipartBody(file, issueId, boundary);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(UPLOAD_URL))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(fullBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            logger.error("Errore Backend Cloud");
            throw new IOException("Errore Upload (" + response.statusCode() + "): " + response.body());
        }
    }

    public List<Attachment> getAttachmentsByIssue(Integer issueId) throws AttachmentServiceException {
        String token = SessionManager.getInstance().getToken();

        String url = GET_URL.replace("{issue-id}", issueId.toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .GET()
                .build();

        try{
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                return mapper.readValue(response.body(), new TypeReference<List<Attachment>>() {});
            }
            else if(response.statusCode() == 404){
                return new ArrayList<>();
            }
            else{
                throw new AttachmentServiceException("Error while getting the attachment(" + response.statusCode() + "): " + response.body());
            }
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
            throw new AttachmentServiceException("Operation interrupted while searching for the attachment", e);
        }
        catch(Exception e){
            throw new AttachmentServiceException("Error getting the attachment", e);
        }

    }

    private byte[] buildCompleteMultipartBody(File file, Integer issueId, String boundary) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String crlf = "\r\n";
        String twoHyphens = "--";

        output.write((twoHyphens + boundary + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"related-to\"" + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));
        output.write(String.valueOf(issueId).getBytes(StandardCharsets.UTF_8));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        String fileName = file.getName();
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) mimeType = "application/octet-stream";

        output.write((twoHyphens + boundary + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Type: " + mimeType + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        output.write(Files.readAllBytes(file.toPath()));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        output.write((twoHyphens + boundary + twoHyphens + crlf).getBytes(StandardCharsets.UTF_8));

        return output.toByteArray();
    }
}