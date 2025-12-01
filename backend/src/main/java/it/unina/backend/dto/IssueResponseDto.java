package it.unina.backend.dto;

import it.unina.backend.entity.Issue;
import java.time.OffsetDateTime;

public class IssueResponseDto {
    private Integer id;
    private String title;
    private String description;
    private String type;
    private String priority;
    private String status;
    private OffsetDateTime createdOn;
    private UserResponseDto author; 

    public IssueResponseDto(Issue issue) {
        this.id = issue.getId();
        this.title = issue.getTitle();
        this.description = issue.getDescription();
        this.type = issue.getType();
        this.priority = issue.getPriority();
        this.status = issue.getStatus();
        this.createdOn = issue.getCreatedOn();

        // Se c'è un autore, lo convertiamo nel formato sicuro (senza password)
        if (issue.getCreatedBy() != null) {
            this.author = new UserResponseDto(issue.getCreatedBy());
        }
    }

    // Getters necessari per la serializzazione JSON
    public Integer getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public OffsetDateTime getCreatedOn() { return createdOn; }
    public UserResponseDto getAuthor() { return author; }
}