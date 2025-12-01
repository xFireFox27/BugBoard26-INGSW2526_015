package it.unina.backend.controller;

import it.unina.backend.dao.UserDao;
import it.unina.backend.entity.Comment;
import it.unina.backend.dao.CommentDao;
import it.unina.backend.security.RequireJWTAuthentication;
import it.unina.backend.dto.CommentDto;
import it.unina.backend.dto.CommentResponseDto;
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
import java.util.stream.Collectors;

@Path("/comments")
@RequireJWTAuthentication
public class CommentController {

    private final CommentDao commentDao = CommentDao.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@QueryParam("issue-id") Integer issueId) {

        if (issueId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of(ERROR_KEY, "missing_parameter",
                                          MESSAGE_KEY, "Parameter 'issue-id' must be provided"))
                           .build();
        }

        try {
            List<Comment> comments = commentDao.findCommentsByIssueId(issueId);
            List<CommentResponseDto> responseDtos = comments.stream()
                                                            .map(CommentResponseDto::new)
                                                            .collect(Collectors.toList());
            return Response.ok(responseDtos).build();
        } catch (SQLException e) {
            logger.error("Impossible to retrieve comments for the specified issue", e);
            return Response.serverError()
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to retrieve comments for the specified issue"))
                           .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComment(@Context SecurityContext securityContext, CommentDto commentDto) {
        if (commentDto == null ||
            commentDto.getText() == null ||
            commentDto.getText().isEmpty() ||
            commentDto.getIssueId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of(ERROR_KEY, "missing_fields",
                                          MESSAGE_KEY, "All fields are required"))
                           .build();
        }

        try {
            String username = securityContext.getUserPrincipal().getName();

            if (!securityContext.isUserInRole("Admin") &&
                !securityContext.isUserInRole("Normal"))) {
                logger.warn("User {} not allowed to insert comments", username);
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(Map.of(ERROR_KEY, "forbidden",
                                       MESSAGE_KEY, "Only Admin and Normal users can add comments"))
                        .build();
            }

            Comment comment = new Comment(commentDto);
            comment.setUser(userDao.findUserByUsername(username));
            commentDao.insertComment(comment);
            CommentResponseDto responseDto = new CommentResponseDto(comment);
            return Response.status(Response.Status.CREATED).entity(responseDto).build();
        } catch (SQLException e) {
            logger.error("Impossible to insert the comment", e);
            return Response.serverError()
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to insert the comment"))
                           .build();
        }
    }
}
