package it.unina.backend.controller;

import it.unina.backend.dao.AttachmentDao;
import it.unina.backend.dao.IssueDao;
import it.unina.backend.entity.Attachment;
import it.unina.backend.security.RequireJWTAuthentication;
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
import java.time.OffsetDateTime;
import java.util.List;

@Path("/attachments")
@RequireJWTAuthentication
public class AttachmentController {

    Logger logger = LoggerFactory.getLogger(AttachmentController.class);
    private final S3Service s3Service = new S3Service();
    private final AttachmentDao attachmentDao = AttachmentDao.getInstance();
    private final IssueDao issueDao = IssueDao.getInstance();

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadAttachment(
            @Context SecurityContext securityContext,
            @FormDataParam("file") FormDataBodyPart bodyPart,
            @FormDataParam("createdBy") String createdBy,
            @FormDataParam("relatedTo") int relatedTo
    ) {
        logger.info("Uploading Attachment");

        if (!securityContext.isUserInRole("Admin") && !securityContext.isUserInRole("Normal")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied").build();
        }

        String s3Url = null;
        try {
            if (!issueDao.existsById(relatedTo)) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Impossible to find the specified issue.").build();
            }

            FormDataContentDisposition fileDetail = bodyPart.getFormDataContentDisposition();
            InputStream fileInputStream = bodyPart.getValueAs(InputStream.class);
            String contentType = bodyPart.getMediaType() != null ? bodyPart.getMediaType().toString() : "application/octet-stream";

            byte[] fileBytes = fileInputStream.readAllBytes();

            if (fileBytes.length == 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Uploaded file is empty!.").build();
            }

            s3Url = s3Service.uploadFile(fileBytes, fileDetail.getFileName(), contentType);

            Attachment attachment = createAndSaveAttachment(fileDetail.getFileName(), s3Url, createdBy, relatedTo);

            return Response.ok(attachment).build();

        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("DB Error").build();
        } catch (IOException e) {
            logger.error("IO Error: {}", e.getMessage(), e);
            return Response.serverError().entity("File read error: " + e.getMessage()).build();
        } catch (Exception e) {
            logger.error("Unknown error: {}", e.getMessage(), e);
            return Response.serverError().entity("Generic error: " + e.getMessage()).build();
        }
    }


    @GET
    @Path("/issue/{issueId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttachmentsForIssue(@PathParam("issueId") int issueId) {
        try {
            List<Attachment> attachments = attachmentDao.findAttachmentsByRelatedId(issueId);
            return Response.ok(attachments).build();
        } catch (SQLException e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Attachment createAndSaveAttachment(String fileName, String s3Url, String createdBy, int relatedTo) throws SQLException {
        Attachment attachment = new Attachment(
                0, fileName, s3Url, OffsetDateTime.now(),
                createdBy != null ? createdBy : "system", relatedTo
        );
        if (!attachmentDao.insertAttachment(attachment)) {
            throw new SQLException("Upload failed");
        }
        return attachment;
    }
}