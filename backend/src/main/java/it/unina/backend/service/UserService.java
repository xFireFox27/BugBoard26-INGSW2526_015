package it.unina.backend.service;

import it.unina.backend.dao.UserDao;
import it.unina.backend.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class UserService {

    private static UserService instance;
    private final UserDao userDao;

    private UserService() {
        this.userDao = UserDao.getInstance();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    public User authenticateUser(String email, String password) throws SQLException {
        User user = userDao.findUserByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("Credenziali non valide");
        }

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenziali non valide");
        }

        return user;
    }




}




