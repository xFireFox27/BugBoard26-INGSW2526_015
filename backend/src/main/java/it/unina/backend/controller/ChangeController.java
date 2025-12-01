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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/changes")
@RequireJWTAuthentication
public class ChangeController {

    private final ChangeDao changeDao = ChangeDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(ChangeController.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChanges(@Context SecurityContext securityContext, @QueryParam("issue-id") Integer issueId) {
        if (issueId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{error: \"'issue-id' must be provided\"}")
                    .build();
        }

        try {
            List<Change> changes = changeDao.findChangesByIssueId(issueId);
            return Response.ok(changes).build();
        } catch (SQLException e) {
            logger.error("error: impossible to retrieve changes for the specified issues ", e);
            return Response.serverError().entity("{error: \"Database error\"}").build();
        }
    }
}
