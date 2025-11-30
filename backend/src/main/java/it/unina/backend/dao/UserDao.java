package it.unina.backend.dao;

import it.unina.backend.entity.User;
import it.unina.backend.daointerface.UserDaoInterface;
import it.unina.backend.connection.DatabaseConnection;
import static it.unina.backend.util.DaoUtil.*;
import java.time.OffsetDateTime;
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

    public User findUserByUsername(String username) throws SQLException {
        String sql = "SELECT username, email, name, surname, role, created_on" +
                        "FROM User WHERE username = ?";
        try(Connection connection = DatabaseConnection.getInstance().getConnection();
            PreparedStatement st = connection.prepareStatement(sql)){
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if(rs.next()){
                return createUserFromResultSet(rs);
            }
            return null;
        }
    }

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
