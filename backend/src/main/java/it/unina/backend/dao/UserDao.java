package it.unina.backend.dao;

import it.unina.backend.util.*;
import it.unina.backend.entity.User;
import it.unina.backend.daointerface.UserDaoInterface;
import java.time.OffsetDateTime;
import it.unina.backend.util.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.time.ZoneOffset;

public class UserDao implements UserDaoInterface {

    private static UserDao instance;

    private UserDao() {}

    public static UserDao getInstance() {
        if (instance == null) {
            instance = new UserDao();
        }
        return instance;
    }

    public void insertUser(String email, String username, String passwordHash, String name, String surname, String role) throws SQLException {

        String sql = "INSERT INTO \"User\" (email, username, password_hash, name, surname, role) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement st = connection.prepareStatement(sql)){
            st.setString(1, email);
            st.setString(2, username);
            st.setString(3, passwordHash);
            st.setString(4, name);
            st.setString(5, surname);
            st.setString(6, role);
            st.executeUpdate();
        }
    }

    /*public User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM User WHERE username = ?";
        try(Connection connection = DatabaseConnection.getInstance().getConnection();){
            PreparedStatement st = connection.prepareStatement(sql);
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                OffsetDateTime createdOn = rs.getTimestamp("created_on")
                        .toLocalDateTime()
                        .atOffset(ZoneOffset.UTC);

                return new User(
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("surname"),
                        rs.getString("role"),
                        createdOn
                );
            } else {
                return null;
            }
        }
    } */

    public User findByEmailAndPassword(String email, String plainPassword) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            var preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, plainPassword);

            var resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                OffsetDateTime createdOn = resultSet.getTimestamp("created_on")
                        .toLocalDateTime()
                        .atOffset(java.time.ZoneOffset.UTC);

                return new User(
                        resultSet.getString("email"),
                        resultSet.getString("username"),
                        resultSet.getString("password"),
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