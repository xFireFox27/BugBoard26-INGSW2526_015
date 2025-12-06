package it.unina.backend.controller;

import it.unina.backend.dao.UserDao;
import it.unina.backend.dto.IssueDto;
import it.unina.backend.dto.IssueResponseDto;
import it.unina.backend.entity.Issue;
import it.unina.backend.entity.User;
import it.unina.backend.exception.TransactionException;
import it.unina.backend.security.RequireJwtAuthentication;
import it.unina.backend.service.IssueService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/issues")
@RequireJwtAuthentication
public class IssueController {
    private final IssueService issueService = IssueService.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(IssueController.class);
    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIssues(
        @QueryParam("status") String status,
        @QueryParam("type") String type,
        @QueryParam("priority") String priority,
        @QueryParam("sort-by") String sortBy
    ) {
        logger.info("Retrieving issues");

        try {
            List<Issue> issues = issueService.getIssuesFilteredAndSorted(status, type, priority, sortBy);
            List<IssueResponseDto> responseDtos = issues.stream()
                                                        .map(IssueResponseDto::new)
                                                        .toList();

            return Response.ok(responseDtos).build();
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);

            return Response.serverError()
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to retrieve the issues."))
                           .build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addIssue(
        @Context SecurityContext securityContext,
        IssueDto issueDto
    ) {
        logger.info("Creating issue");

        if (issueDto == null ||
            issueDto.hasMissingFields()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(Map.of(ERROR_KEY, "invalid_input",
                                          MESSAGE_KEY, "Missing required fields in the issue."))
                           .build();
        }

        try {
            String username = securityContext.getUserPrincipal().getName();

            if (!securityContext.isUserInRole("Admin") &&
                !securityContext.isUserInRole("Normal")) {
                return Response.status(Response.Status.FORBIDDEN)
                               .entity(Map.of(ERROR_KEY, "forbidden",
                                              MESSAGE_KEY, "Only Admin and Normal users can submit issues."))
                               .build();
            }

            Issue issue = new Issue(issueDto);
            User user = userDao.findUserByUsername(username);
            issue.setCreatedBy(user);
            issueService.insertIssueWithChange(issue);
            IssueResponseDto responseDto = new IssueResponseDto(issue);

            return Response.status(Response.Status.CREATED)
                           .entity(responseDto)
                           .build();
        } catch (SQLException e) {
            logger.error("Database error: {}", e.getMessage(), e);

            return Response.serverError()
                           .entity(Map.of(ERROR_KEY, "database_error",
                                          MESSAGE_KEY, "Impossible to add the issue."))
                           .build();
        } catch (TransactionException e) {
            logger.error("Transaction error: {}", e.getMessage(), e);

            return Response.serverError()
                           .entity(Map.of(ERROR_KEY, "transaction_error",
                                          MESSAGE_KEY, "An error occurred while creating the issue."))
                           .build();
        }
    }
}
