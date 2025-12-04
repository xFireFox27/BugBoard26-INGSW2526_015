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

@Path("/attachments")
@RequireJwtAuthentication
public class AttachmentController {

    private final S3Service s3Service = new S3Service();
    private final AttachmentService attachmentService = new AttachmentService();
    private final AttachmentDao attachmentDao = AttachmentDao.getInstance();
    private final IssueDao issueDao = IssueDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(AttachmentController.class);

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

        if (!securityContext.isUserInRole("Admin") && !securityContext.isUserInRole("Normal")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied.")
                    .build();
        }

        try {
            if (!issueDao.existsById(relatedTo)) return Response.status(Response.Status.NOT_FOUND)
                    .entity("Impossible to find the specified issue.")
                    .build();

            FormDataContentDisposition fileDetail = bodyPart.getFormDataContentDisposition();
            InputStream fileInputStream = bodyPart.getValueAs(InputStream.class);
            String contentType = bodyPart.getMediaType() != null ? bodyPart.getMediaType()
                    .toString() : "application/octet-stream";
            byte[] fileBytes = fileInputStream.readAllBytes();

            if (fileBytes.length == 0) return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Uploaded file is empty.")
                    .build();

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
                    .entity("Database error.")
                    .build();
        } catch (IOException e) {
            logger.error("IO error: {}", e.getMessage(), e);
            return Response.serverError()
                    .entity("File read error: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("Unknown error: {}", e.getMessage(), e);
            return Response.serverError()
                    .entity("Generic error: " + e.getMessage())
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
            logger.error("Internal server error: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
