package it.unina.backend.dto;

public class CommentDto {
    String text;
    Integer issueId;

    public CommentDto() {}

    public CommentDto(String text, int issueId) {
        this.text = text;
        this.issueId = issueId;
    }

    public String getText() {
        return text;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public void setText(String text) {
        this.text = text;
    }
}
