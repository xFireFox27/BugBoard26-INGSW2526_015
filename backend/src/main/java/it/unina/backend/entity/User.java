package it.unina.backend.entity;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class User{
    @NotBlank
    @Size(max = 100)
    private String email;

    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(max = 100)
    private String passwordHash;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 100)
    private String surname;

    @NotBlank
    @Size(max = 100)
    private String role;

    @NotNull
    private OffsetDateTime createdOn;

    public User(String email,
                String username,
                String passwordHash,
                String name,
                String surname,
                String role,
                OffsetDateTime createdOn)  {

        if(!role.equals("Admin") && !role.equals("Normal") && !role.equals("External")) {
            throw new IllegalArgumentException("Attempt to insert an invalid role: " +
                                               role +
                                               ".\nRole must be: Admin, Normal or External.")
            ;
        }

        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.surname = surname;
        this.role = role;
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
