package it.unina.backend.controller;

import it.unina.backend.dto.LoginRequestDto;
import it.unina.backend.dto.LoginResponseDto;
import it.unina.backend.dto.UserDto;
import it.unina.backend.entity.User;
import it.unina.backend.service.UserService;
import it.unina.backend.security.JwtAuth;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.util.Map;

@Path("/login")
public class LoginController {

    private final UserService userService = UserService.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequestDto request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            logger.warn("Login request is null or request fields are invalid");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(ERROR_KEY, "missing field",
                                   MESSAGE_KEY, "both email and password are required"))
                    .build();
        }

        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());

            String token = JwtAuth.generateToken(user.getEmail(), user.getUsername(), user.getRole());

            UserDto userDto = new UserDto(user.getUsername(), user.getEmail(), user.getRole());
            LoginResponseDto responseBody = new LoginResponseDto(token, userDto);

            return Response.ok(responseBody).build();

        } catch (IllegalArgumentException e) {
            logger.error("error: Login request is invalid {}", e.getMessage(), e);
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of(ERROR_KEY, "missing field",
                                   MESSAGE_KEY, "Both email and password are required"))
                    .build();
        } catch (SQLException e) {
            logger.error("error: Login error {}", e.getMessage(), e);
            return Response.serverError()
                    .entity(Map.of(ERROR_KEY, "Database error",
                                   MESSAGE_KEY, "Error occurred while trying to login"))
                    .build();
        }
    }
}
