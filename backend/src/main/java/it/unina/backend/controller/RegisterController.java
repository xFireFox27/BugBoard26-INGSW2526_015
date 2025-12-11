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
    private static final String INPUT_ERR = "invalid_input";

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(UserRegistrationRequestDto request, @Context SecurityContext securityContext) {
        logger.info("Registering user");

        try {
            if (!securityContext.isUserInRole("Admin")) {
                return Response.status(Response.Status.FORBIDDEN)
                               .entity(Map.of(ERROR_KEY, "forbidden",
                                              MESSAGE_KEY, "Only admins can create new users."))
                               .build();
            }

            if (!request.isComplete()) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity(Map.of(ERROR_KEY, INPUT_ERR,
                                              MESSAGE_KEY, "Missign required fields in the registration."))
                               .build();
            }

            if (request.getPassword().length() < 8) {
                return Response.status(Response.Status.BAD_REQUEST)
                               .entity(Map.of(ERROR_KEY, INPUT_ERR,
                                              MESSAGE_KEY, "Password must be at least 8 characters long."))
                               .build();
            }

            User user = userService.registerUser(request);

            return Response.status(Response.Status.CREATED)
                           .entity(Map.of("success", "done",
                                          "email", user.getEmail()))
                           .build();

        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument: {}", e.getMessage());

            return Response.status(Response.Status.CONFLICT)
                           .entity(Map.of(ERROR_KEY, INPUT_ERR,
                                          MESSAGE_KEY, "The email already exists."))
                           .build();

        } catch (IllegalStateException e) {
            logger.error("Illegal state: {}", e.getMessage());

            return Response.status(Response.Status.CONFLICT)
                           .entity(Map.of(ERROR_KEY, INPUT_ERR,
                                          MESSAGE_KEY, "The username is already taken."))
                           .build();

        } catch (Exception e) {
            logger.error("Unknown error: {}", e.getMessage(), e);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                           .entity(Map.of(ERROR_KEY, "generic_error",
                                          MESSAGE_KEY, "An error occurred during the registration."))
                           .build();
        }
    }
}
