package it.unina.backend.dto;

import it.unina.backend.entity.Comment;
import java.time.OffsetDateTime;

public class CommentResponseDto {
    private final int id;
    private final String text;
    private final OffsetDateTime createdOn;
    private final int issueId;
    private UserResponseDto author;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.createdOn = comment.getCreatedOn();
        this.issueId = comment.getIssueId();

        if (comment.getWrittenBy() != null) {
            this.author = new UserResponseDto(comment.getWrittenBy());
        }
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

    public UserResponseDto getAuthor() {
        return author;
    }
}
