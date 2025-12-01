package it.unina.backend.util;

import it.unina.backend.entity.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class DaoUtil {

    private DaoUtil(){}

    public static User createUserFromResultSet(ResultSet rs) throws SQLException{

        return new User(
                rs.getString("email"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("role"),
                rs.getObject("created_on", OffsetDateTime.class)
        );
    }
}
