package it.unina.backend.entity;

import it.unina.backend.dto.IssueDto;

import java.time.OffsetDateTime;

public class Issue {
    private Integer id;
    private String title;
    private String description;
    private String type;
    private String priority = "Low";
    private String status = "To Do";
    private User createdBy;
    private OffsetDateTime createdOn;

    public Issue(Integer id,
                 String title,
                 String description,
                 String type,
                 String priority,
                 String status,
                 User createdBy,
                 OffsetDateTime createdOn) {
        if (!checkType(type)) {
            throw new IllegalArgumentException(
                "Attempt to insert an invalid type: " +
                type +
                "\nType must be: Bug, Documentation, Question or Feature."
            );
        }
        if (!checkStatus(status)) {
            throw new IllegalArgumentException(
                "Attempt to insert an invalid status: " +
                status +
                "\nStatus must be: To Do, In Progress, Done or Archived."
            );
        }
        if (!checkPriority(priority)) {
            throw new IllegalArgumentException(
                    "Attempt to insert an invalid priority: " + priority +
                            "\nPriority must be: Low, Medium or High."
            );
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    public Issue(IssueDto issueDto) {
        this.title = issueDto.getTitle();
        this.description = issueDto.getDescription();
        this.type = issueDto.getType();
        this.priority = issueDto.getPriority();
        this.status = issueDto.getStatus();
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

    public String getPriority() {return priority;}

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

    public void setId(Integer id) { this.id = id;}

    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    private boolean checkType(String type){
        return(type.equals("Bug") || type.equals("Documentation") || type.equals("Question") || type.equals("Feature"));
    }

    private boolean checkStatus(String status){
        return (status.equals("To Do") || status.equals("In Progress") || status.equals("Done"));
    }

    private boolean checkPriority(String priority){
        return priority.equals("Low") || priority.equals("Medium") || priority.equals("High");
    }
}
