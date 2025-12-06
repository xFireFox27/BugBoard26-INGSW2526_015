package it.unina.backend.entity;

import it.unina.backend.dto.IssueDto;

import java.time.OffsetDateTime;

public class Issue {
    private Integer id;
    private final String title;
    private final String description;
    private final String type;
    private final String priority;
    private final String status;
    private User createdBy;
    private OffsetDateTime createdOn;

    public Issue(
        Integer id,
        String title,
        String description,
        String type,
        String priority,
        String status,
        User createdBy,
        OffsetDateTime createdOn
    ) {
        if (!checkType(type)) {
            throw new IllegalArgumentException();
        }

        if (!checkStatus(status)) {
            throw new IllegalArgumentException();
        }

        if (!checkPriority(priority)) {
            throw new IllegalArgumentException();
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

    public void setId(Integer id) {
        this.id = id;
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

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getUsername(){
        return createdBy.getUsername();
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    private boolean checkType(String type) {
        return type.equals("Bug") ||
               type.equals("Documentation") ||
               type.equals("Question") || type.equals("Feature");
    }

    private boolean checkStatus(String status) {
        return status.equals("To Do") ||
               status.equals("In Progress") ||
               status.equals("Done") ||
               status.equals("Archived");
    }

    private boolean checkPriority(String priority) {
        return priority.equals("Low") ||
               priority.equals("Medium") ||
               priority.equals("High");
    }
}
