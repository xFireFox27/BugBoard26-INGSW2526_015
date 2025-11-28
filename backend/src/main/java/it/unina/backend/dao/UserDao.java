package it.unina.backend.dao;

import it.unina.backend.entity.User;
import it.unina.backend.daointerface.UserDaoInterface;
import java.time.OffsetDateTime;
import it.unina.backend.util.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public class UserDao implements UserDaoInterface {

    private static UserDao instance;

    private UserDao() {}

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    public void creaUser(String email, String username, String passwordHash, String name, String surname, String role) throws SQLException {

        String sql = "INSERT INTO User (email, username, password_hash, name, surname, role) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement st = connection.prepareStatement(sql)){
            st.setString(1, email);
            st.setString(2, username);

        }
    }

    public User findByEmailAndPassword(String email, String plainPassword) throws SQLException {
        String sql = "SELECT username, email, password_hash, name, surname, " +
                "role, created_on FROM \"user\" WHERE email = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("password_hash");

                    if (!org.mindrot.jbcrypt.BCrypt.checkpw(plainPassword, hashedPassword)) {
                        return null;
                    }

                    OffsetDateTime createdOn = resultSet.getTimestamp("created_on")
                            .toLocalDateTime()
                            .atOffset(java.time.ZoneOffset.UTC);

                    return new User(
                            resultSet.getString("email"),
                            resultSet.getString("username"),
                            hashedPassword,
                            resultSet.getString("name"),
                            resultSet.getString("surname"),
                            resultSet.getString("role"),
                            createdOn
                    );
                } else {
                    return null;
                }
            }
        }
    }



}