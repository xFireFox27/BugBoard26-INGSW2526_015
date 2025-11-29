package it.unina.backend.daointerface;

import it.unina.backend.entity.User;
import java.sql.SQLException;

public interface UserDaoInterface{
    public void insertUser(User user) throws SQLException;

    // public User findUserByUsername(String username) throws SQLException;

    public User findUserByEmail(String email) throws SQLException;
}
