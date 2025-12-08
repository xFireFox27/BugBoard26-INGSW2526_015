package it.unina.frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class Issue {
    private Integer id;
    private String title;
    private String description;
    private String status;
    private String type;
    private String priority;
    private OffsetDateTime createdOn;
    private User author;


    public String getAuthorUsername() {
        return author != null ? author.getUsername() : "Sconosciuto";
    }
}