package it.unina.backend.controller;

import it.unina.backend.dao.ChangeDao;
import it.unina.backend.entity.Change;
import it.unina.backend.security.RequireJWTAuthentication;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.List;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/changes")
@RequireJWTAuthentication
public class ChangeController {

    private final ChangeDao changeDao = ChangeDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(ChangeController.class);
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChanges(@Context SecurityContext securityContext, @QueryParam("issue-id") Integer issueId) {
        if (issueId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of(ERROR_KEY, "Parameter 'issue-id' must be provided"))
                           .build();
        }

        try {
            List<Change> changes = changeDao.findChangesByIssueId(issueId);
            return Response.ok(changes).build();
        } catch (SQLException e) {
            logger.error("Impossible to retrieve changes for the specified issues ", e);
            return Response.serverError()
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to retrieve changes for issue " + issueId
                           )).build();
        }
    }
}
