package it.unina.backend.controller;

import it.unina.backend.dto.LoginRequest;
import it.unina.backend.dto.LoginResponse;
import it.unina.backend.dto.UserDto;
import it.unina.backend.entity.User;
import it.unina.backend.service.UserService;
import it.unina.backend.security.JwtAuth;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.Map;

@Path("/login")
public class LoginController {

    private final UserService userService = UserService.getInstance();

    private static final String ERROR_KEY = "error";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(ERROR_KEY, "Email e password obbligatori"))
                    .build();
        }

        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());

            String token = JwtAuth.generateToken(user.getEmail(), user.getUsername(), user.getRole());

            UserDto userDto = new UserDto(user.getUsername(), user.getEmail(), user.getRole());
            LoginResponse responseBody = new LoginResponse(token, userDto);

            return Response.ok(responseBody).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of(ERROR_KEY, e.getMessage()))
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError()
                    .entity(Map.of(ERROR_KEY, "Errore server durante il login"))
                    .build();
        }
    }
}