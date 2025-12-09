package it.unina.frontend.service;

import it.unina.frontend.util.ApiConfig;
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

    private static final String UPLOAD_URL = ApiConfig.BASE_URL + "/attachments/upload";

    private final HttpClient client;

    public AttachmentService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(60))
                .build();
    }

    public void uploadAttachment(File file, Integer issueId) throws Exception {
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
            System.err.println("Errore Backend Cloud: " + response.body());
            throw new RuntimeException("Errore Upload (" + response.statusCode() + "): " + response.body());
        }
    }

    private byte[] buildCompleteMultipartBody(File file, Integer issueId, String boundary) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        String crlf = "\r\n";
        String twoHyphens = "--";

        // 1. ID (related-to)
        output.write((twoHyphens + boundary + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"related-to\"" + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));
        output.write(String.valueOf(issueId).getBytes(StandardCharsets.UTF_8));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        // 2. FILE
        String fileName = file.getName();
        String mimeType = Files.probeContentType(file.toPath());
        if (mimeType == null) mimeType = "application/octet-stream";

        output.write((twoHyphens + boundary + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(("Content-Type: " + mimeType + crlf).getBytes(StandardCharsets.UTF_8));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        output.write(Files.readAllBytes(file.toPath()));
        output.write(crlf.getBytes(StandardCharsets.UTF_8));

        // 3. CHIUSURA
        output.write((twoHyphens + boundary + twoHyphens + crlf).getBytes(StandardCharsets.UTF_8));

        return output.toByteArray();
    }
}