package it.unina.backend.dao;

import it.unina.backend.daointerface.UserDaoInterface;
import java.time.OffsetDateTime;
import it.unina.backend.util.DatabaseConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;

public class UserDao implements UserDaoInterface {

    public void creaUser(String email, String username, String passwordHash, String name, String surname, String role){

        String sql = "INSERT INTO User (email, username, password_hash, name, surname, role) VALUES (?, ?, ?, ?, ?, ?)";

        try(Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement st = connection.prepareStatement(sql)){
            st.setString(1, email);
            st.setString(2, username);

        }
    }
}