package it.unina.backend.controller;

import it.unina.backend.dao.IssueDao;
import it.unina.backend.entity.Issue;
import it.unina.backend.security.RequireJWTAuthentication;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.sql.SQLException;

@Path("/issue")
@RequireJWTAuthentication
public class IssueController {

    IssueDao issueDao = IssueDao.getInstance();

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

}
