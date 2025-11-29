package it.unina.backend.controller;

import it.unina.backend.dao.ChangeDao;
import it.unina.backend.entity.Change;
import it.unina.backend.util.RequireJWTAuthentication;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;
import java.sql.SQLException;

@Path("/changes")
@RequireJWTAuthentication
public class ChangeController {

    private final ChangeDao changeDao = ChangeDao.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChanges(@Context SecurityContext securityContext,
                               @QueryParam("issue_id") Integer issueId) {
        if (issueId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"'issue_id' must be provided\"}")
                    .build();
        }

        try {
            List<Change> changes = changeDao.findChangesByIssueId(issueId);
            return Response.ok(changes).build();

        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().entity("{\"error\": \"Errore Database\"}").build();
        }
    }
}