package it.unina.backend.entity;

import java.time.OffsetDateTime;

public class Comment {
    private int id;
    String text;
    OffsetDateTime createdOn;
    int issueId;
    User writtenBy;

    public Comment(int id, String text, OffsetDateTime createdOn, int issueId, User writtenBy) {
        this.id = id;
        this.text = text;
        this.createdOn = createdOn;
        this.issueId = issueId;
        this.writtenBy = writtenBy;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public int getIssueId() {
        return issueId;
    }

    public User getWrittenBy() {
        return writtenBy;
    }

    public String getUserUsername() {
        return writtenBy.getUsername();
    }
}
