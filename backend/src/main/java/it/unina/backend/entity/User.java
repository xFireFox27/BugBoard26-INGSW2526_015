package it.unina.backend.entity;


import java.time.OffsetDateTime;



public class User{
    private final String email;
    private final String username;
    private final String passwordHash;
    private final String name;
    private final String surname;
    private final String role;
    private OffsetDateTime createdOn;

    public User(String email,
                String username,
                String passwordHash,
                String name,
                String surname,
                String role,
                OffsetDateTime createdOn)  {

        if (!validateUserData(email, username, passwordHash, name, surname, role)) {
            throw new IllegalArgumentException("User data is not valid");
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

        if (!validateUserData(email, username, passwordHash, name, surname, role)) {
            throw new IllegalArgumentException("User data is not valid");
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

    public static boolean validateUserData(String email,
                                           String username,
                                           String password,
                                           String name,
                                           String surname,
                                           String role) {
        if (email == null || email.isBlank()) {
            return false;
        }
        if (username == null || username.isBlank()) {
            return false;
        }
        if (password == null || password.length() < 8) {
            return false;
        }
        if (name == null || name.isBlank()) {
            return false;
        }
        if (surname == null || surname.isBlank()) {
            return false;
        }
        return role.equals("Admin") || role.equals("Normal") || role.equals("External");
    }


}
