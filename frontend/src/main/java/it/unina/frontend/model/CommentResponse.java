package it.unina.frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class CommentResponse {
    private int id;
    private String text;
    private OffsetDateTime createdOn;
    private int issueId;
    private User author;
}
