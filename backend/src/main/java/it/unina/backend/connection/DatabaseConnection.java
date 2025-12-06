package it.unina.backend.connection;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final String url;
    private final String username;
    private final String password;

    private DatabaseConnection() {
        Dotenv dotenv = Dotenv.configure()
                              .directory("./")
                              .ignoreIfMissing()
                              .load();

        this.url = dotenv.get("DB_URL");
        this.username = dotenv.get("DB_USER");
        this.password = dotenv.get("DB_PASSWORD");

        if (this.url == null || this.username == null || this.password == null) {
            throw new IllegalStateException("Missing Database configuration in .env file");
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
