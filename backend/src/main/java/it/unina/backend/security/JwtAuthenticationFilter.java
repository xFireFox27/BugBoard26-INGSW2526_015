package it.unina.backend.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

@Provider
@RequireJwtAuthentication
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            abortRequest(requestContext, "Missing Token.");
            return;
        }

        String token = authorizationHeader.substring("Bearer ".length()).trim();

        try {
            if (JwtAuth.validateToken(token)) {
                String username = JwtAuth.getUsernameFromToken(token);
                String role = JwtAuth.getRoleFromToken(token);
                boolean isSecure = requestContext.getSecurityContext().isSecure();

                requestContext.setSecurityContext(new SecurityContext() {
                    @Override
                    public Principal getUserPrincipal() {
                        return () -> username;
                    }

                    @Override
                    public boolean isUserInRole(String roleName) {
                        return role != null &&
                               role.equalsIgnoreCase(roleName);
                    }

                    @Override
                    public boolean isSecure() {
                        return isSecure;
                    }

                    @Override
                    public String getAuthenticationScheme() {
                        return "Bearer";
                    }
                });
            } else {
                abortRequest(requestContext, "Token is invalid or expired.");
            }
        } catch (Exception e) {
            abortRequest(requestContext, "Error while validating token.");
        }
    }

    private void abortRequest(ContainerRequestContext requestContext, String message) {
        requestContext.abortWith(
            Response.status(Response.Status.UNAUTHORIZED)
                    .entity(Map.of("error", message))
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .build()
        );
    }
}
