package it.unina.backend.dto;

public class UserDto {
    private String username;
    private String email;
    private String role;

    // Costruttore vuoto per Jackson (necessario)
    public UserDto() {}

    public UserDto(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getter e Setter
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}