package it.unina.backend.controller;

import it.unina.backend.entity.Comment;
import it.unina.backend.dao.CommentDao;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

@Path("/comments")
public class CommentController{

    CommentDao commentDao = CommentDao.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getComments(@QueryParam("issueId") Integer issueId){

        if(issueId == null){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"'issueId' must be provided\"}")
                    .build();
        }

        try{
            List<Comment> comments = commentDao.findCommentsByIssueId(issueId);
            return Response.ok(comments).build();
        }
        catch(SQLException e){
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Errore Database\"}").build();
        }

    }

}
