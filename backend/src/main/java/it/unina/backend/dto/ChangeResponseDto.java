package it.unina.backend.dto;

import it.unina.backend.entity.Change;
import java.time.OffsetDateTime;

public class ChangeResponseDto {
    private int id;
    private String action;
    private String details;
    private OffsetDateTime createdOn;
    private int issueId;
    private UserResponseDto createdBy; // Usiamo il DTO sicuro qui!

    public ChangeResponseDto(Change change) {
        this.id = change.getId();
        this.action = change.getAction();
        this.details = change.getDetails();
        this.createdOn = change.getCreatedOn();
        this.issueId = change.getIssueId();

        // converte l'utente che ha creato la modifica in UserResponseDto (senza password)
        if (change.getCreatedBy() != null) {
            this.createdBy = new UserResponseDto(change.getCreatedBy());
        }
    }

    // Getters necessari per la serializzazione JSON
    public int getId() { return id; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public OffsetDateTime getCreatedOn() { return createdOn; }
    public int getIssueId() { return issueId; }
    public UserResponseDto getCreatedBy() { return createdBy; }
}