package it.unina.frontend.service;

import it.unina.frontend.exception.AttachmentServiceException;
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

public class AttachmentService {

    private static final String UPLOAD_URL = "http://localhost:8080/api/attachments/upload";
    private final HttpClient client;

    public AttachmentService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public void uploadAttachment(File file, Integer issueId) throws AttachmentServiceException {
        String token = SessionManager.getInstance().getToken();
        String boundary = "MioBoundary";

        try {
            byte[] body = buildMultipartBody(file, issueId, boundary);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(UPLOAD_URL))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("Errore Backend: " + response.body());
                throw new AttachmentServiceException("Errore Upload (" + response.statusCode() + "): " + response.body(), response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AttachmentServiceException("Operazione interrotta durante l'upload dell'allegato", e);
        } catch (IOException e) {
            throw new AttachmentServiceException("Errore durante l'upload dell'allegato", e);
        }
    }


    private byte[] buildMultipartBody(File file, Integer issueId, String boundary) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        String crlf = "\r\n";
        String twoHyphens = "--";

        output.write((twoHyphens + boundary + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + crlf).getBytes(StandardCharsets.UTF_8));

        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) mimeType = "application/octet-stream";
        output.write(("Content-Type: " + mimeType + crlf).getBytes(StandardCharsets.UTF_8));

        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        output.write(Files.readAllBytes(file.toPath()));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        output.write((twoHyphens + boundary + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"related-to\"" + crlf).getBytes(StandardCharsets.UTF_8));


        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        output.write(String.valueOf(issueId).getBytes(StandardCharsets.UTF_8));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        output.write((twoHyphens + boundary + twoHyphens + crlf).getBytes(StandardCharsets.UTF_8));

        return output.toByteArray();
    }
}