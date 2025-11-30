package it.unina.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;



public class User{
    private final String email;
    private final String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String passwordHash;
    private final String name;
    private final String surname;
    private final String role;
    private  OffsetDateTime createdOn;

    public User(String email,
                String username,
                String passwordHash,
                String name,
                String surname,
                String role,
                OffsetDateTime createdOn)  {

        if (!role.equals("Admin") &&
            !role.equals("Normal") &&
            !role.equals("External")) {

            throw new IllegalArgumentException(
                "Attempt to insert an invalid role: " +
                role +
                ".\nRole must be: Admin, Normal or External."
            );
        }

        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.createdOn = createdOn;
    }

    //Costruttore senza createdOn per facilitare la registrazione degli utenti
    public User(String email,
                String username,
                String passwordHash,
                String name,
                String surname,
                String role)  {

        if (!role.equals("Admin") &&
            !role.equals("Normal") &&
            !role.equals("External")) {

            throw new IllegalArgumentException(
                "Attempt to insert an invalid role: " +
                role +
                ".\nRole must be: Admin, Normal or External."
            );
        }

        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.createdOn = null;
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

    public void setCreatedOn(OffsetDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
