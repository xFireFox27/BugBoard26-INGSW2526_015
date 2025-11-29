package it.unina.backend.controller;

import it.unina.backend.dto.LoginRequest;
import it.unina.backend.entity.User;
import it.unina.backend.service.UserService;
import it.unina.backend.util.JwtUtil;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;

@Path("/login")
public class LoginController {

    private final UserService userService = UserService.getInstance();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {
        if (request.getEmail() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Email e password obbligatori\"}")
                    .build();
        }

        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());

            String token = JwtUtil.generateToken(user.getEmail(), user.getUsername());
            return Response.ok()
                    .entity("{\"token\": \"" +
                            token +
                            "\", \"user\": {\"username\": \"" +
                            user.getUsername() +
                            "\", \"email\": \"" +
                            user.getEmail() +
                            "\", \"role\": \"" +
                            user.getRole() +
                            "\"}}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Errore durante il login\"}")
                    .build();
        }
    }
}
