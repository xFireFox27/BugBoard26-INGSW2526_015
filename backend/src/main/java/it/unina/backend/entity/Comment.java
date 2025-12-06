package it.unina.backend.entity;

import it.unina.backend.dto.CommentDto;
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

    public Comment(CommentDto commentDto) {
        this.text = commentDto.getText();
        this.issueId = commentDto.getIssueId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public int getIssueId() {
        return issueId;
    }

    public User getWrittenBy() {
        return writtenBy;
    }

    public String getUsername() {
        return writtenBy.getUsername();
    }

    public void setUser(User user) {
        this.writtenBy = user;
    }
}
