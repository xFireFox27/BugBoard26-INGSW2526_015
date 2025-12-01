package it.unina.backend.service;

import it.unina.backend.dao.UserDao;
import it.unina.backend.entity.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.time.OffsetDateTime;

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

    public User registerUser(String email, String username, String password, String name, String surname, String role) throws SQLException {
        // Verifica se l'utente esiste già
        if (userDao.findUserByEmail(email) != null) {
            throw new IllegalArgumentException("Email già in uso");
        }

        if (userDao.findUserByUsername(username) != null) {
            throw new IllegalArgumentException("Username già in uso");
        }

        // Hash della password
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        // Crea l'utente senza data
        User user = new User(email, username, passwordHash, name, surname, role);

        // Inserisci nel database (aggiorna automaticamente createdOn)
        if (userDao.insertUser(user)) {
            return user;
        }

        throw new SQLException("Errore durante l'inserimento dell'utente");
    }




}




