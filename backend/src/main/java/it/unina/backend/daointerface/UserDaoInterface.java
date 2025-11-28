package it.unina.backend.daointerface;

import it.unina.backend.entity.User;

import java.sql.SQLException;
import java.time.OffsetDateTime;

public interface UserDaoInterface{

    public void creaUser(String email, String username, String passwordHash, String name, String surname, String role) throws SQLException;
    public User findByEmailAndPassword(String email, String plainPassword) throws SQLException;
}