package it.unina.frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRequest {
    private String username;
    private String email;
    private String name;
    private String surname;
    private String password;
    private String role;
}
