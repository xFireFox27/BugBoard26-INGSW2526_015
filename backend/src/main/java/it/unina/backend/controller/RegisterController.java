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
        try {
            // Verifica che l'utente autenticato sia admin
            if (!securityContext.isUserInRole("Admin")) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(Map.of(ERROR_KEY, "Solo gli admin possono registrare nuovi utenti"))
                        .build();
            }

            logger.info("Tentativo di registrazione per utente: {}", request.getUsername());

            // Validazione input
            if (!request.isComplete()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(Map.of(ERROR_KEY, "Tutti i campi sono obbligatori"))
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

            logger.info("Registrazione completata per utente: {}", request.getUsername());
            return Response.status(Response.Status.CREATED)
                    .entity(Map.of(ERROR_KEY, "Registrazione completata", "email", user.getEmail()))
                    .build();

        } catch (IllegalArgumentException e) {
            logger.warn("Tentativo di registrazione fallito: {}", e.getMessage());
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of(ERROR_KEY, e.getMessage()))
                    .build();

        } catch (Exception e) {
            logger.error("Errore durante la registrazione", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of(ERROR_KEY, "Errore durante la registrazione"))
                    .build();
        }
    }
}
