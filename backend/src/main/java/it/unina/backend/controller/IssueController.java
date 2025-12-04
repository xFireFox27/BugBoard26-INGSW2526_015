package it.unina.backend.controller;

import it.unina.backend.dao.UserDao;
import it.unina.backend.dto.IssueDto;
import it.unina.backend.dto.IssueResponseDto;
import it.unina.backend.entity.Issue;
import it.unina.backend.entity.User;
import it.unina.backend.exception.TransactionException;
import it.unina.backend.security.RequireJWTAuthentication;
import it.unina.backend.service.IssueService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;

import jakarta.ws.rs.core.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/issues")
@RequireJWTAuthentication
public class IssueController {
    private final IssueService issueService = IssueService.getInstance();
    private final UserDao userDao = UserDao.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(IssueController.class);
    /*
    private IssueDao issueDao = IssueDao.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIssueByIssueId(@Context SecurityContext securityContext, @QueryParam("issue_id") Integer issueId){
        if (issueId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{error: \"'issue_id' must be provided\"}")
                    .build();
        }

        try {
            Issue issue = issueDao.findIssueById(issueId);
            return Response.ok(issue).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().entity("{error: \"Database error\"}").build();
        }
    }
    */

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getIssues(@QueryParam("status") String status,
                              @QueryParam("type") String type,
                              @QueryParam("sortBy") String sortBy) {
        try {
            List<Issue> issues = issueService.getIssuesFilteredAndSorted(status, type, sortBy);

            // Convertiamo la lista di Issue in una lista di IssueResponseDto
            List<IssueResponseDto> responseDtos = issues.stream()
                    .map(IssueResponseDto::new)
                    .toList();
            
            return Response.ok(responseDtos).build();
        }
        catch (SQLException e) {
            logger.error("error: impossible to retrieve issues with the specified filters ", e);
            return Response.serverError().entity("{\"error\": \"Database error\"}").build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addIssue(@Context SecurityContext securityContext, IssueDto issueDto) {
        if(issueDto == null || issueDto.getDescription() == null || issueDto.getTitle() == null ||
            issueDto.getType() == null || issueDto.getStatus() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try {
            Issue issue = new Issue(issueDto);
            String username = securityContext.getUserPrincipal().getName();
            if(!(securityContext.isUserInRole("Admin") || securityContext.isUserInRole("Normal"))) {
                logger.warn("forbidden: user {} not allowed to insert comments", username);
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"error\": \"Only Admin and User roles can submit issues\"}")
                        .build();
            }
            User user = userDao.findUserByUsername(username);
            issue.setCreatedBy(user);
            issueService.insertIssueWithChange(issue);
            IssueResponseDto responseDto = new IssueResponseDto(issue);
            return Response.status(Response.Status.CREATED).entity(responseDto).build();
        }
        catch (SQLException e) {
            logger.error("error: impossible to add issue with the specified filters ", e);
            return Response.serverError().entity("{\"error\": \"Database error\"}").build();
        }
        catch (TransactionException e) {
            logger.error("error: Transaction error", e);
            return Response.serverError().entity("Transaction error").build();
        }
    }
}
