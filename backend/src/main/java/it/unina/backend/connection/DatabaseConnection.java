package it.unina.backend.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private String url;
    private String username;
    private String password;

    private DatabaseConnection() {
        Properties props = new Properties();

        // Carica il file una volta sola alla creazione del Singleton
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                // Se manca il file, è un errore grave: meglio lanciare un'eccezione che stampare solo testo
                throw new IllegalStateException("Impossibile trovare il file config.properties");
            }

            props.load(input);

            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.user");
            this.password = props.getProperty("db.password");

        } catch (IOException ex) {
            // Se non riesco a leggere la config, l'app non deve partire
            throw new UncheckedIOException("Errore nella lettura della configurazione DB", ex);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.username, this.password);
    }
}
