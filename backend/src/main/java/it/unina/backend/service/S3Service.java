package it.unina.backend.service;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.UUID;

public class S3Service {
    private final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public S3Service() {
        Dotenv dotenv = Dotenv.load();
        this.bucketName = dotenv.get("AWS_BUCKET_NAME");
        String accessKey = dotenv.get("AWS_ACCESS_KEY_ID");
        String secretKey = dotenv.get("AWS_SECRET_ACCESS_KEY");
        String regionStr = dotenv.get("AWS_REGION");
        Region region = Region.of(regionStr);
        var credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        this.s3Client = S3Client.builder()
                                .region(region)
                                .credentialsProvider(credentials)
                                .build();
        this.s3Presigner = S3Presigner.builder()
                                      .region(region)
                                      .credentialsProvider(credentials)
                                      .build();
    }

    public String uploadFile(byte[] fileBytes, String originalFileName, String contentType) {
        String newFileName = UUID.randomUUID() + "-" + originalFileName;
        PutObjectRequest putRequest = PutObjectRequest.builder()
                                                      .bucket(bucketName)
                                                      .key(newFileName)
                                                      .contentType(contentType)
                                                      .build();
        s3Client.putObject(putRequest, RequestBody.fromBytes(fileBytes));
        return newFileName;
    }

    public String generatePresignedUrl(String objectKey) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                                                                        .signatureDuration(Duration.ofMinutes(30))
                                                                        .getObjectRequest(b -> b.bucket(bucketName)
                                                                                                       .key(objectKey))
                                                                        .build();
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        return presignedRequest.url().toString();
    }


    public void deleteFile(String fileUrl) {
        try {
            String key = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                                                                         .bucket(bucketName)
                                                                         .key(key)
                                                                         .build();
            s3Client.deleteObject(deleteObjectRequest);
            logger.info("File deleted");
        } catch (Exception e) {
            logger.error("Unknown error: {}", e.getMessage(), e);
        }
    }
}
