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
            throw new IllegalArgumentException("Invalid credentials");
        }

        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("invalid credentials");
        }

        return user;
    }

    public User registerUser(UserRegistrationRequestDto request) throws SQLException {
        // Verifica se l'utente esiste già
        if (userDao.findUserByEmail(request.getEmail()) != null) {
            throw new IllegalArgumentException("Email già in uso");
        }

        if (userDao.findUserByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("Username già in uso");
        }

        // Hash della password
        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // Crea l'utente senza data
        User user = new User(request.getEmail(), request.getUsername(), passwordHash, request.getName(), request.getSurname(), request.getRole());

        // Inserisci nel database (aggiorna automaticamente createdOn)
        if (userDao.insertUser(user)) {
            return user;
        }

        throw new SQLException("Errore durante l'inserimento dell'utente");
    }
}
