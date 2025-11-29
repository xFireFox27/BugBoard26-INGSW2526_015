package it.unina.backend.controller;

import it.unina.backend.dao.ChangeDao;
import it.unina.backend.entity.Change;
import it.unina.backend.entity.Issue;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.sql.SQLException;

@Path("/changes")
public class ChangeController {

    private final ChangeDao changeDao = ChangeDao.getInstance();

    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChanges(@QueryParam("issue_id") Integer issueId) {
        if (issueId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("'issue_id' must be provided")
                    .build();
        }

        try {
            // Qui avviene l'auto-unboxing da Integer a int se il DAO vuole int
            List<Change> changes = changeDao.findChangesByIssueId(issueId);
            return Response.ok(changes).build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().entity("Errore Database").build();
        }
    }

}