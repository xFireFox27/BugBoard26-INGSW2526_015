package it.unina.backend.controller;

import it.unina.backend.dao.ChangeDao;
import it.unina.backend.entity.Change;
import it.unina.backend.dto.ChangeResponseDto;
import it.unina.backend.security.RequireJwtAuthentication;
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
@RequireJwtAuthentication
public class ChangeController {

    private final ChangeDao changeDao = ChangeDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(ChangeController.class);
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getChanges(
        @Context SecurityContext securityContext,
        @QueryParam("issue-id") Integer issueId
    ) {
        if (issueId == null) {
            logger.error("error: no issue_id provided");
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of(ERROR_KEY, "missing_parameter",
                                          MESSAGE_KEY, "missing required field"))
                           .build();
        }

        try {
            List<Change> changes = changeDao.findChangesByIssueId(issueId);

            List<ChangeResponseDto> responseDtos = changes.stream()
                                                          .map(ChangeResponseDto::new)
                                                          .toList();

            return Response.ok(responseDtos)
                           .build();
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to retrieve changes for the specified issue"))
                           .build();
        }
    }
}
