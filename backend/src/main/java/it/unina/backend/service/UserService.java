package it.unina.backend.service;

import it.unina.backend.dao.UserDao;
import it.unina.backend.dto.UserRegistrationRequestDto;
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
            throw new IllegalArgumentException();
        }

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException();
        }

        return user;
    }

    public User registerUser(UserRegistrationRequestDto request) throws SQLException {
        if (userDao.findUserByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException();
        }

        if (userDao.findUserByUsername(request.getUsername()) != null) {
            throw new IllegalStateException();
        }

        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        User user = new User(request.getEmail(), request.getUsername(), passwordHash, request.getName(), request.getSurname(), request.getRole());

        if (userDao.insertUser(user)) {
            return user;
        }

        throw new SQLException();
    }
}
