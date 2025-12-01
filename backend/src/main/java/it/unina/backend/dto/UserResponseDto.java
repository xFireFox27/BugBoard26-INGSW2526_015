package it.unina.backend.dto;

import it.unina.backend.entity.User;
import java.time.OffsetDateTime;

public class UserResponseDto {
    private String username;
    private String email;
    private String name;
    private String surname;
    private String role;
    private OffsetDateTime createdOn;

    // Costruttore che accetta l'Entity User originale
    public UserResponseDto(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.name = user.getName();
        this.surname = user.getSurname();
        this.role = user.getRole();
        this.createdOn = user.getCreatedOn();
    }

    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getRole() { return role; }
    public OffsetDateTime getCreatedOn() { return createdOn; }
}