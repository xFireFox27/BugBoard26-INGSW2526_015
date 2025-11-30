package it.unina.backend.controller;

import it.unina.backend.entity.Issue;
import it.unina.backend.security.RequireJWTAuthentication;
import it.unina.backend.service.IssueService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/issues")
@RequireJWTAuthentication
public class IssueController {
    private final IssueService issueService = IssueService.getInstance();
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
            return Response.ok(issues).build();
        }
        catch (SQLException e) {
            logger.error("error: impossible to retrieve issues with the specified filters ", e);
            return Response.serverError().entity("{\"error\": \"Errore Database\"}").build();
        }
    }
}
