package it.unina.frontend.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
public class User {
    private String username;
    private String email;
    private String name;
    private String surname;
    private String role;
    private OffsetDateTime createdOn;
}