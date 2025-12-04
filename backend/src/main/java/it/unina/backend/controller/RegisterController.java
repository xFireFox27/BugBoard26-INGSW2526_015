package it.unina.backend.controller;

import it.unina.backend.dto.UserRegistrationRequest;
import it.unina.backend.entity.User;
import it.unina.backend.security.RequireJWTAuthentication;
import it.unina.backend.service.UserService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Path("/register")
@RequireJWTAuthentication
public class RegisterController {

    private final UserService userService = UserService.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private static final String ERROR_KEY = "error";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(UserRegistrationRequest request, @Context SecurityContext securityContext) {
        logger.info("Attempt to register user: {}", request.getUsername());

        try {
            // Verifica che l'utente autenticato sia admin
            if (!securityContext.isUserInRole("Admin")) {
                logger.warn("Admin permissions needed!");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(Map.of(ERROR_KEY, "Only admins can register new users"))
                        .build();
            }

            // Validazione input
            if (!request.isComplete()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of(ERROR_KEY, "All fields must be filled!"))
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

            logger.info("User: {} registered successfully", request.getUsername());
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of(ERROR_KEY, "Registration completed", "email", user.getEmail()))
                    .build();

        } catch (IllegalArgumentException e) {
            logger.warn("Attempt to register new user failed: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of(ERROR_KEY, e.getMessage()))
                    .build();

        } catch (Exception e) {
            logger.error("Error while trying to register a new user", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(ERROR_KEY, "Error  while trying to register a new user"))
                    .build();
        }
    }
}
