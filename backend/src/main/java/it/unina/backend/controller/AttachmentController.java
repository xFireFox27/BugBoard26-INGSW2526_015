package it.unina.backend.controller;

import it.unina.backend.dao.AttachmentDao;
import it.unina.backend.dao.IssueDao;
import it.unina.backend.entity.Attachment;
import it.unina.backend.security.RequireJwtAuthentication;
import it.unina.backend.service.AttachmentService;
import it.unina.backend.service.S3Service;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Path("/attachments")
@RequireJwtAuthentication
public class AttachmentController {
    private final S3Service s3Service = new S3Service();
    private final AttachmentService attachmentService = AttachmentService.getInstance();
    private final AttachmentDao attachmentDao = AttachmentDao.getInstance();
    private final IssueDao issueDao = IssueDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAttachment(
            @Context SecurityContext securityContext,
            @FormDataParam("file") FormDataBodyPart bodyPart,
            @FormDataParam("related-to") int relatedTo
    ) {
        logger.info("Uploading Attachment");

        if (!securityContext.isUserInRole("Admin") &&
            !securityContext.isUserInRole("Normal")) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity(Map.of(ERROR_KEY, "forbidden",
                                          MESSAGE_KEY, "User is not allowed to upload files."))
                           .build();
        }

        try {
            if (!issueDao.existsById(relatedTo)) {
                return Response.status(Response.Status.NOT_FOUND)
                               .entity(Map.of(ERROR_KEY, "not_found",
                                              MESSAGE_KEY, "The issue doesn't exist."))
                               .build();
            }

            FormDataContentDisposition fileDetail = bodyPart.getFormDataContentDisposition();
            InputStream fileInputStream = bodyPart.getValueAs(InputStream.class);
            String contentType = bodyPart.getMediaType() != null ? bodyPart.getMediaType()
                                                                           .toString() : "application/octet-stream";
            byte[] fileBytes = fileInputStream.readAllBytes();

            if (fileBytes.length == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity(Map.of(ERROR_KEY, "empty_file",
                                              MESSAGE_KEY, "The uploaded file is empty."))
                               .build();
            }

            String createdBy = securityContext.getUserPrincipal().getName();
            String objectKey = s3Service.uploadFile(fileBytes, fileDetail.getFileName(), contentType);
            Attachment attachment = attachmentService.createAndSaveAttachment(fileDetail.getFileName(),
                                                                                         objectKey,
                                                                                         createdBy,
                                                                                         relatedTo);
            attachment.setUrl(s3Service.generatePresignedUrl(objectKey));

            return Response.ok(attachment)
                           .build();
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to upload the attachment."))
                           .build();
        } catch (IOException e) {
            logger.error("IO error: {}", e.getMessage(), e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of(ERROR_KEY, "reading_error",
                                          MESSAGE_KEY, "Impossible to read file content."))
                           .build();
        } catch (Exception e) {
            logger.error("Unknown error: {}", e.getMessage(), e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of(ERROR_KEY, "generic_error",
                                          MESSAGE_KEY, "An error occurred during the upload."))
                           .build();
        }
    }

    @GET
    @Path("/issue/{issue-id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttachmentsForIssue(@PathParam("issue-id") int issueId) {
        try {
            List<Attachment> attachments = attachmentDao.findAttachmentsByRelatedId(issueId);

            for (Attachment attachment : attachments) {
                String objectKey = attachment.getUrl();
                String presignedUrl = s3Service.generatePresignedUrl(objectKey);
                attachment.setUrl(presignedUrl);
            }

            return Response.ok(attachments)
                           .build();
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to retrieve the attachments."))
                           .build();
        }
    }
}
