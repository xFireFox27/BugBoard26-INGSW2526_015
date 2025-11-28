package it.unina.backend.entity;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;

public class User{
    @NotNull
    private String email;
    @NotNull
    private String username;
    @NotNull
    private String passwordHash;
    @NotNull
    private String name;
    @NotNull
    private String surname;
    @NotNull
    private String role;
    @NotNull
    private OffsetDateTime createdOn;

    public User(String email, String username, String passwordHash, String name, String surname, String role, OffsetDateTime createdOn)  {
        if(role.equals("admin") || role.equals("normal") || role.equals("external")){
            this.role = role;
        }
        else throw new IllegalArgumentException("tentativo di creazione di un utente con ruolo: " + role + " ruolo deve assumere un valore tra: admin, normal o external.");
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.surname = surname;
        this.createdOn = createdOn;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getSurname() {
        return surname;
    }

    public String getRole() {
        return role;
    }

    public OffsetDateTime getCreatedOn() {
        return createdOn;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }
}