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

    @Override
    public void insertUser(User user) throws SQLException {

        String sql = "INSERT INTO \"user\" (email, username, password_hash, name, surname, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?)"
        ;

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)
        ) {
            st.setString(1, user.getEmail());
            st.setString(2, user.getUsername());
            st.setString(3, user.getPasswordHash());
            st.setString(4, user.getName());
            st.setString(5, user.getSurname());
            st.setString(6, user.getRole());
            st.executeUpdate();
        }
    }

    /*
    @Override
    public User findUserByUsername(String username) throws SQLException {
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
    }
    */

    @Override
    public User findUserByEmail(String email) throws SQLException {
        String sql = "SELECT username, email, password_hash, name, surname, " +
                "role, created_on FROM \"user\" WHERE email = ?";

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    OffsetDateTime createdOn = resultSet.getTimestamp("created_on")
                            .toLocalDateTime()
                            .atOffset(java.time.ZoneOffset.UTC);

                    return new User(
                            resultSet.getString("email"),
                            resultSet.getString("username"),
                            resultSet.getString("password_hash"),
                            resultSet.getString("name"),
                            resultSet.getString("surname"),
                            resultSet.getString("role"),
                            createdOn
                    );
                }
                return null;
            }
        }
    }


}
