package it.unina.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.github.cdimascio.dotenv.Dotenv;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final Key SECRET_KEY;
    private static final long EXPIRATION_TIME = 86400000; // 24 hours

    private JwtUtil() {}

    static {
        // 1. Carica il file .env
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 2. Cerca la chiave nel file .env O nelle variabili di sistema
        String secret = dotenv.get("JWT_SECRET");

        // 3. Se dotenv non trova nulla, prova il fallback diretto al sistema
        if (secret == null) {
            secret = System.getenv("JWT_SECRET");
        }

        // 4. Validazione
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("ERRORE CRITICO: JWT_SECRET non trovato nel file .env o nelle variabili d'ambiente.");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("ERRORE SICUREZZA: JWT_SECRET deve essere lungo almeno 32 caratteri.");
        }

        // 5. Inizializza la chiave crittografica
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public static String generateToken(String email, String username, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("username", username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public static String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public static String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }

    public static String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
}
