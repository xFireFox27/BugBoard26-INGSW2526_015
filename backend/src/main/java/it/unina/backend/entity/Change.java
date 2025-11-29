package it.unina.backend.entity;

import java.time.OffsetDateTime;
import it.unina.backend.entity.User;
import it.unina.backend.entity.Issue;

public class Change {
    private final int id;
    private final String action;
    private final String details;
    private final OffsetDateTime createdOn;
    private final User createdBy;
    private final Issue relatedTo;

    public Change(int id, String action, String details, OffsetDateTime createdOn, User createdBy, Issue relatedTo){
        this.id = id;
        this.action = action;
        this.details = details;
        this.createdOn = createdOn;
        this.createdBy = createdBy;
        this.relatedTo = relatedTo;
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

    public Issue getRelatedTo() {
        return relatedTo;
    }
}
