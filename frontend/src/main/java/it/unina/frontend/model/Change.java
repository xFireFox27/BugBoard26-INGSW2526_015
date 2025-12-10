package it.unina.frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class Change {
    private int id;
    private String action;
    private String details;
    private OffsetDateTime createdOn;
    private int issueId;
    private User createdBy;
}
