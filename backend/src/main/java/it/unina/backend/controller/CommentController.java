package it.unina.backend.controller;

import it.unina.backend.entity.Comment;
import it.unina.backend.dao.CommentDao;
import it.unina.backend.security.RequireJWTAuthentication;
import it.unina.backend.dto.CommentDto;
import it.unina.backend.dto.CommentResponseDto; // <--- Importa il nuovo DTO
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors; // <--- Serve per le liste

@Path("/comments")
@RequireJWTAuthentication
public class CommentController {

    private final CommentDao commentDao = CommentDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@QueryParam("issueId") Integer issueId) {

        if (issueId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"'issueId' must be provided\"}")
                    .build();
        }

        try {
            List<Comment> comments = commentDao.findCommentsByIssueId(issueId);

            List<CommentResponseDto> responseDtos = comments.stream()
                    .map(CommentResponseDto::new)
                    .collect(Collectors.toList());

            return Response.ok(responseDtos).build();
        } catch (SQLException e) {
            logger.error("error: impossible to retrieve comments for the specified issues ", e);
            return Response.serverError().entity("{\"error\": \"Errore Database\"}").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addComment(@Context SecurityContext securityContext, CommentDto commentDto) {
        if (commentDto == null || commentDto.getText() == null || commentDto.getText().isEmpty()
                || commentDto.getIssueId() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("all fields required")
                    .build();
        }

        try {
            String username = securityContext.getUserPrincipal().getName();
            Comment comment = new Comment(commentDto);
            commentDao.insertComment(comment, username);


            CommentResponseDto responseDto = new CommentResponseDto(comment);

            return Response.status(Response.Status.CREATED).entity(responseDto).build();
        } catch (SQLException e) {
            logger.error("error: impossible to insert the comment ", e);
            return Response.serverError().entity("{\"error\": \"Errore Database\"}").build();
        }
    }
}