package it.unina.backend.dto;

import it.unina.backend.entity.User;

public class IssueDto {
    String title;
    String description;
    String type;
    String priority = "Low";
    String status = "To Do";

    IssueDto(){}

    IssueDto(String title, String description, String type, String priority, String status, User user) {
        if (!checkType(type)) {
            throw new IllegalArgumentException(
                    "Attempt to insert an invalid type: " + type +
                            "\nType must be: Bug, Documentation, Question or Feature."
            );
        }
        if (!checkStatus(status)) {
            throw new IllegalArgumentException(
                    "Attempt to insert an invalid status: " + status +
                            "\nStatus must be: To Do, In Progress, Done or Archived."
            );
        }
        if (!checkPriority(priority)) {
            throw new IllegalArgumentException(
                    "Attempt to insert an invalid priority: " + priority +
                            "\nPriority must be: To Do, In Progress, Done or Archived."
            );
        }
        this.title = title;
        this.description = description;
        this.type = type;
        this.priority = priority;
        this.status = status;
    }

    public String getTitle() { return title;}

    public String getDescription() { return description;}

    public String getType() { return type;}

    public String getPriority() { return priority;}

    public String getStatus() { return status;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private boolean checkType(String type){
        return(type.equals("Bug") || type.equals("Documentation") || type.equals("Question") || type.equals("Feature"));
    }

    private boolean checkStatus(String status){
        return (status.equals("To Do") || status.equals("In Progress") || status.equals("Done")) || status.equals("Archived");
    }

    private boolean checkPriority(String priority){
        return priority.equals("Low") || priority.equals("Medium") || priority.equals("High");
    }
}


