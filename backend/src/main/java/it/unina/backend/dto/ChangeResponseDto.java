package it.unina.backend.dto;

import it.unina.backend.entity.Change;
import java.time.OffsetDateTime;

public class ChangeResponseDto {
    private final int id;
    private final String action;
    private final String details;
    private final OffsetDateTime createdOn;
    private final int issueId;
    private UserResponseDto createdBy;

    public ChangeResponseDto(Change change) {
        this.id = change.getId();
        this.action = change.getAction();
        this.details = change.getDetails();
        this.createdOn = change.getCreatedOn();
        this.issueId = change.getIssueId();

        if (change.getCreatedBy() != null) {
            this.createdBy = new UserResponseDto(change.getCreatedBy());
        }
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

    public int getIssueId() {
        return issueId;
    }

    public UserResponseDto getCreatedBy() {
        return createdBy;
    }
}
