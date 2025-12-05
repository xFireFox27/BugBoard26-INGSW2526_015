package it.unina.backend.controller;

import it.unina.backend.dto.UserRegistrationRequestDto;
import it.unina.backend.entity.User;
import it.unina.backend.security.RequireJwtAuthentication;
import it.unina.backend.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Path("/register")
@RequireJwtAuthentication
public class RegisterController {

    private final UserService userService = UserService.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private static final String ERROR_KEY = "error";
    private static final String MESSAGE_KEY = "message";
    private static final String SUCCESS_KEY = "success";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(UserRegistrationRequestDto request, @Context SecurityContext securityContext) {
        logger.info("Attempt to register user: {}", request.getUsername());

        try {
            // Verifica che l'utente autenticato sia admin
            if (!securityContext.isUserInRole("Admin")) {
                logger.warn("Admin permissions needed!");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(Map.of(ERROR_KEY, "Permissions error",
                                       MESSAGE_KEY, "Only admins have permissions to create new users"))
                        .build();
            }

            // Validazione input
            if (!request.isComplete()) {
                logger.warn("Request is incomplete!");
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of(ERROR_KEY, "Missing field",
                                       MESSAGE_KEY, "Both username and password are required!"))
                        .build();
            }

            // Registrazione utente
            User user = userService.registerUser(
                    request.getEmail(),
                    request.getUsername(),
                    request.getPassword(),
                    request.getName(),
                    request.getSurname(),
                    request.getRole()
            );

            return Response.status(Response.Status.CREATED)
                    .entity(Map.of(SUCCESS_KEY, "Registration completed", "email", user.getEmail()))
                    .build();

        } catch (IllegalArgumentException e) {
            logger.warn("Attempt to register new user failed: {}", e.getMessage(), e);
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of(ERROR_KEY, "invalid field!",
                                   MESSAGE_KEY, "email already exists!"))
                    .build();

        } catch (Exception e) {
            logger.error("Error while trying to register a new user {}", e.getMessage(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(ERROR_KEY, "Internal server error",
                                   MESSAGE_KEY, "Error while trying to register a new user"))
                    .build();
        }
    }
}
