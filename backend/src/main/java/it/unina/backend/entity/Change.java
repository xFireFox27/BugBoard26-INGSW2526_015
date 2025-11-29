package it.unina.backend.entity;


import java.time.OffsetDateTime;
import it.unina.backend.entity.User;
import it.unina.backend.entity.Issue;

public class Change {
    private int id;
    private String action;
    private String details;
    private OffsetDateTime createdOn;
    private User createdBy;
    private int issueId;

    public Change(int id, String action, String details, OffsetDateTime createdOn, User createdBy, int issueId){

        this.id = id;
        this.action = action;
        this.details = details;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.issueId = issueId;
    }

    public int getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public int getIssueId() {
        return issueId;
    }

    public String getUserUsername() {
        return createdBy.getUsername();
    }
}
