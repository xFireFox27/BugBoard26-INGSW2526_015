package it.unina.backend.entity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class User{
    @NotNull
    @Size(max = 100)
    private String email;
    @NotNull
    @Size(max = 100)
    private String username;
    @NotNull
    @Size(max = 100)
    private String passwordHash;
    @NotNull
    @Size(max = 100)
    private String name;
    @NotNull
    @Size(max = 100)
    private String surname;
    @NotNull
    @Size(max = 100)
    private String role;
    @NotNull
    private OffsetDateTime createdOn;

    public User(String email, String username, String passwordHash, String name, String surname, String role, OffsetDateTime createdOn)  {
        if(role.equals("Admin") || role.equals("Normal") || role.equals("External")){
            this.role = role;
        }
        else throw new IllegalArgumentException("attempt to insert an invalid role: " + role + " role must be: admin, normal or external.");
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