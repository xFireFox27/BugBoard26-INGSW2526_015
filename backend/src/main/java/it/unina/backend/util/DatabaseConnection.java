package it.unina.backend.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private final String url;
    private final String username;
    private final String password;

    private DatabaseConnection() {
        this.url = "jdbc:postgresql://localhost:5432/backend";
        this.username = "postgres";
        this.password = "postgres";
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
