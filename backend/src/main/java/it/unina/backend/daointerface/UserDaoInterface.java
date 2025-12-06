package it.unina.backend.daointerface;

import it.unina.backend.entity.User;
import java.sql.SQLException;

public interface UserDaoInterface{
    boolean insertUser(User user) throws SQLException;
    User findUserByUsername(String username) throws SQLException;
    User findUserByEmail(String email) throws SQLException;
}
