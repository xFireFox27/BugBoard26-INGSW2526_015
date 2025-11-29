package it.unina.backend.entity;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Issue {
    @NotNull
    private final Integer id;

    @NotBlank
    @Size(max=100)
    private final String title;

    @NotBlank
    @Size(max=1000)
    private final String description;

    @NotBlank
    @Size(max=100)
    private final String type;

    @NotBlank
    @Size(max=100)
    private final String status;

    @NotBlank
    @Size(max=100)
    private final String createdBy;

    @NotNull
    private final OffsetDateTime createdOn;

    public Issue(Integer id,
                 String title,
                 String description,
                 String type,
                 String status,
                 String createdBy,
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

    public String getCreatedBy() {
        return createdBy;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }
}
