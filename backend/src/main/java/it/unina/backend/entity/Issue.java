package it.unina.backend.entity;

import java.time.OffsetDateTime;

public class Issue {
    private final Integer id;
    private final String title;
    private final String description;
    private final String type;
    private final String status;
    private final User createdBy;
    private final OffsetDateTime createdOn;

    public Issue(Integer id,
                 String title,
                 String description,
                 String type,
                 String status,
                 User createdBy,
                 OffsetDateTime createdOn) {
        if (!type.equals("Bug") &&
            !type.equals("Documentation") &&
            !type.equals("Question") &&
            !type.equals("Feature")) {
            throw new IllegalArgumentException(
                "Attempt to insert an invalid type: " +
                type +
                ".\nType must be: Bug, Documentation, Question or Feature."
            );
        }
        if (!status.equals("To Do") &&
            !status.equals("In Progress") &&
            !status.equals("Done") &&
            !status.equals("Archived")) {
            throw new IllegalArgumentException(
                "Attempt to insert an invalid status: " +
                status +
                ".\nStatus must be: To Do, In Progress, Done or Archived."
            );
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.status = status;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public String getUserUsername(){
        return createdBy.getUsername();
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }
}
