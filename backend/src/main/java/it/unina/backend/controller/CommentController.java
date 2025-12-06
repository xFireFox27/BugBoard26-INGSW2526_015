package it.unina.backend.controller;

import it.unina.backend.entity.Comment;
import it.unina.backend.dao.CommentDao;
import it.unina.backend.security.RequireJwtAuthentication;
import it.unina.backend.dto.CommentDto;
import it.unina.backend.dto.CommentResponseDto;
import it.unina.backend.service.CommentService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Path("/comments")
@RequireJwtAuthentication
public class CommentController {

    private final CommentDao commentDao = CommentDao.getInstance();
    private final CommentService commentService = CommentService.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@QueryParam("issue-id") Integer issueId) {

        logger.info("Retrieving comments");

        if (issueId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of(ERROR_KEY, "missing_parameter",
                                          MESSAGE_KEY, "Missing required field."))
                           .build();
        }

        try {
            List<Comment> comments = commentDao.findCommentsByIssueId(issueId);
            List<CommentResponseDto> responseDtos = comments.stream()
                                                            .map(CommentResponseDto::new)
                                                            .toList();

            return Response.ok(responseDtos).build();
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            return Response.serverError()
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to retrieve the comments."))
                           .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComment(
        @Context SecurityContext securityContext,
        CommentDto commentDto
    ) {
        logger.info("Creating comment");

        if (!securityContext.isUserInRole("Admin") &&
            !securityContext.isUserInRole("Normal")) {
            return Response.status(Response.Status.FORBIDDEN)
                           .entity(Map.of(ERROR_KEY, "forbidden",
                                          MESSAGE_KEY, "User is not allowed to insert comments."))
                           .build();
        }

        try {
            String username = securityContext.getUserPrincipal().getName();
            Comment createdComment = commentService.addComment(commentDto, username);

            return Response.status(Response.Status.CREATED)
                           .entity(new CommentResponseDto(createdComment))
                           .build();
        } catch (IllegalArgumentException e) {
            logger.error("Argument error: {}", e.getMessage(), e);
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of(ERROR_KEY, "invalid_input",
                                          MESSAGE_KEY, "The input is not valid."))
                           .build();
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            return Response.serverError()
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to insert the comment."))
                           .build();
        }
    }
}
